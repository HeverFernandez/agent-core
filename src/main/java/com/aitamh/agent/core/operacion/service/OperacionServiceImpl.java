package com.aitamh.agent.core.operacion.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.*;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.entidadfinanciera.repository.EntidadRepository;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.operacion.dto.OperacionResponse;
import com.aitamh.agent.core.operacion.entity.Operacion;
import com.aitamh.agent.core.operacion.mapper.OperacionMapper;
import com.aitamh.agent.core.operacion.repository.OperacionRepository;
import com.aitamh.agent.core.operacion.validator.OperacionFiltroValidator;
import com.aitamh.agent.core.operacion.validator.OperacionRequestValidator;
import com.aitamh.agent.core.saldo.entity.Saldo;
import com.aitamh.agent.core.saldo.enums.EstadoSaldo;
import com.aitamh.agent.core.saldo.repository.SaldoRepository;
import com.aitamh.agent.core.usuario.entity.Usuario;
import com.aitamh.agent.core.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para Operación.
 * Orquesta la lógica de negocio delegando validaciones a clases especializadas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperacionServiceImpl implements OperacionService {

    private final OperacionRepository repository;
    private final OperacionMapper mapper;
    private final SaldoRepository saldoRepository;
    private final EntidadRepository entidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final OperacionRequestValidator requestValidator;
    private final OperacionFiltroValidator filtroValidator;

    @Override
    public OperacionResponse create(OperacionRequest request) {
        // Validar solicitud general
        requestValidator.validarSolicitud(request);

        // Obtener y validar banco
        Entidad banco = entidadRepository
                .findById(request.getIdEntidadBanco())
                .orElseThrow(() -> new EntityNotFoundException("Entidad bancaria no encontrada"));
        requestValidator.validarEntidadBancaria(banco);

        // Obtener y validar usuario
        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        requestValidator.validarUsuario(usuario);

        // Validar tipo de operación y obtener entidad de servicio si aplica
        TipoOperacion tipo = TipoOperacion.fromString(request.getTipo());
        Entidad entidadServicio = null;

        if (tipo == TipoOperacion.PAGO_SERVICIO) {
            entidadServicio = entidadRepository
                    .findById(request.getIdEntidadServicio())
                    .orElseThrow(() -> new EntityNotFoundException("Entidad de servicio no encontrada"));
            requestValidator.validarEntidadServicio(entidadServicio);
        }

        // Obtener saldo con bloqueo para evitar condiciones de carrera
        Long entidadId = request.getIdEntidadBanco();
        Saldo saldo = saldoRepository
                .findTopByEntidadAndEstadoForUpdate(entidadId, EstadoSaldo.ACTIVO)
                .orElseThrow(() -> new SaldoNotFoundException(
                        String.format("No existe un saldo con estado '%s' para la entidad financiera %d",
                                EstadoSaldo.ACTIVO, entidadId)));

        // Procesar cambio de saldo según tipo de operación
        BigDecimal nuevoMonto = procesarOperacionSaldo(tipo, saldo, request.getMonto(), entidadId);
        saldo.setMontoDisponible(nuevoMonto);
        saldoRepository.save(saldo);

        // Registrar operación
        Operacion operacion = mapper.toEntity(request);
        operacion.setEntidadBanco(banco);
        operacion.setEntidadServicio(entidadServicio);
        operacion.setEstado(EstadoOperacion.COMPLETADA);
        operacion.setActivo(true);
        operacion.setUsuario(usuario);
        operacion = repository.save(operacion);

        log.info("Operación creada: {} - Tipo: {}, Monto: {}", operacion.getId(), tipo, request.getMonto());
        return mapper.toResponse(operacion);
    }

    @Override
    @Transactional(readOnly = true)
    public OperacionResponse findById(Long id) {
        Operacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Operación no encontrada: %d", id)));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findAll(
            String tipoOperacion, String estadoOperacion, Long idEntidad,
            String finicio, String ffin, Pageable pageable) {

        // Validar y resolver tipo de operación
        TipoOperacion tipo = filtroValidator.validarYObtenerTipoOperacion(tipoOperacion);

        // Validar y resolver estado de operación
        EstadoOperacion estado = filtroValidator.validarYObtenerEstadoOperacion(estadoOperacion);

        // Validar ID de entidad
        filtroValidator.validarEntidadId(idEntidad);

        // Validar y resolver fechas
        LocalDateTime fechaInicio = filtroValidator.validarYObtenerFechaInicio(finicio);
        LocalDateTime fechaFin = filtroValidator.validarYObtenerFechaFin(ffin);

        // Establecer valores por defecto (día actual completo)
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        }
        if (fechaFin == null) {
            fechaFin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        }

        // Validar rango de fechas
        filtroValidator.validarRangoFechas(fechaInicio, fechaFin);

        // Ejecutar query según combinación de filtros
        Page<Operacion> page = obtenerPaginaOperaciones(tipo, estado, idEntidad, fechaInicio, fechaFin, pageable);

        // Calcular total de comisiones para los filtros aplicados
        BigDecimal totalComision = repository.sumComisionByFilters(
                estado, fechaInicio, fechaFin, tipo, idEntidad, true);

        return buildPageResponse(page, totalComision);
    }

    /**
     * Obtiene la página de operaciones según los filtros aplicados.
     * Ejecuta la query más específica según qué filtros están activos.
     */
    private Page<Operacion> obtenerPaginaOperaciones(
            TipoOperacion tipo, EstadoOperacion estado, Long idEntidad,
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {

        if (idEntidad == null || idEntidad == 0) {
            // Sin filtro de entidad
            if (tipo == null) {
                return repository.findByEstadoAndFechaBetweenAndActivo(
                        estado, fechaInicio, fechaFin, true, pageable);
            } else {
                return repository.findByTipoAndEstadoAndFechaBetweenAndActivo(
                        tipo, estado, fechaInicio, fechaFin, true, pageable);
            }
        } else {
            // Con filtro de entidad
            Entidad entidad = entidadRepository.findById(idEntidad)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Entidad financiera no encontrada: %d", idEntidad)));

            if (tipo == null) {
                return repository.findByEntidadBancoAndEstadoAndFechaBetweenAndActivo(
                        entidad, estado, fechaInicio, fechaFin, true, pageable);
            } else {
                return repository.findByEntidadBancoAndTipoAndEstadoAndFechaBetweenAndActivo(
                        entidad, tipo, estado, fechaInicio, fechaFin, true, pageable);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByTipoAndEntidad(String tipoOperacion, Long idEntidadFinanciera, Pageable pageable) {
        Page<Operacion> page = repository.findByTipoAndEntidadBancoAndActivo(tipoOperacion, idEntidadFinanciera, true, pageable);
        return buildPageResponse(page, new BigDecimal(0));
    }

    @Override
    public OperacionResponse update(Long id, OperacionRequest request) {
        Operacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Operación no encontrada: %d", id)));

        TipoOperacion tipo = TipoOperacion.fromString(request.getTipo());

        entity.setTipo(tipo);
        entity.setMonto(request.getMonto());
        entity.setDescripcion(request.getDescripcion());
        entity.setNumeroReferencia(request.getNumeroReferencia());
        entity.setEntidadBanco(request.getIdEntidadBanco() != null ? entidadRepository.findById(request.getIdEntidadBanco())
                .orElseThrow(() -> new BusinessException("Entidad bancaria no encontrada")) : null);
        entity.setUsuario(request.getUsuarioId() != null ? usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado")) : null);

        entity = repository.save(entity);
        log.info("Operación actualizada: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        Operacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Operación no encontrada: %d", id)));

        entity.setActivo(false);
        repository.save(entity);
        log.info("Operación eliminada: {}", id);
    }

    /**
     * Procesa el cambio de saldo según el tipo de operación.
     * Para retiros y pagos de servicio, valida que haya saldo suficiente.
     */
    private BigDecimal procesarOperacionSaldo(
            TipoOperacion tipo, Saldo saldo, BigDecimal monto, Long entidadId) {

        return switch (tipo) {
            case DEPOSITO -> saldo.getMontoDisponible().add(monto);
            case RETIRO, PAGO_SERVICIO -> {
                requestValidator.validarSaldoSuficiente(
                        saldo.getMontoDisponible(), monto, entidadId);
                yield saldo.getMontoDisponible().subtract(monto);
            }
            default -> throw new OperacionInvalidaException(
                    String.format("Tipo de operación no válido: %s", tipo));
        };
    }

    /**
     * Construye la respuesta paginada con el total de comisiones.
     */
    private PageResponse<OperacionResponse> buildPageResponse(
            Page<Operacion> page, BigDecimal totalComision) {

        return PageResponse.<OperacionResponse>builder()
                .content(page.getContent().stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .totalComision(totalComision)
                .build();
    }
}
