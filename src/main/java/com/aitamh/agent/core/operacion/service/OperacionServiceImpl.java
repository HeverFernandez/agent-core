package com.aitamh.agent.core.operacion.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.*;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.entidadfinanciera.enums.EstadoEntidad;
import com.aitamh.agent.core.entidadfinanciera.enums.TipoEntidad;
import com.aitamh.agent.core.entidadfinanciera.repository.EntidadRepository;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.operacion.dto.OperacionResponse;
import com.aitamh.agent.core.operacion.entity.Operacion;
import com.aitamh.agent.core.operacion.mapper.OperacionMapper;
import com.aitamh.agent.core.operacion.repository.OperacionRepository;
import com.aitamh.agent.core.saldo.entity.Saldo;
import com.aitamh.agent.core.saldo.enums.EstadoSaldo;
import com.aitamh.agent.core.saldo.repository.SaldoRepository;
import com.aitamh.agent.core.saldo.service.SaldoService;
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperacionServiceImpl implements OperacionService {

    private final OperacionRepository repository;
    private final OperacionMapper mapper;
    private final SaldoRepository saldoRepository;
    private final SaldoService saldoService;
    private final EntidadRepository entidadRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public OperacionResponse create(OperacionRequest request) {

        Entidad banco = entidadRepository
                .findById(request.getIdEntidadBanco())
                .orElseThrow(() ->
                        new BusinessException(
                                "Entidad bancaria no encontrada"
                        )
                );

        validarBanco(banco);

        // Validar tipo de operación
        TipoOperacion tipo = TipoOperacion.fromString(request.getTipo());
        if (tipo == null) {
            throw new OperacionInvalidaException(
                    String.format("Tipo de operación no válido: %s", request.getTipo()));
        }

        Long entidadId = request.getIdEntidadBanco();

        // Obtener saldo con estado VIGENTE con bloqueo para evitar condiciones de carrera
        Saldo saldo = saldoRepository
                .findTopByEntidadAndEstadoForUpdate(entidadId, EstadoSaldo.ACTIVO)
                .orElseThrow(() -> new SaldoNotFoundException(
                        String.format("No existe un saldo con estado '%s' para la entidad financiera %d", EstadoSaldo.ACTIVO, entidadId)));

        BigDecimal monto = request.getMonto();
        BigDecimal nuevoMonto;

        switch (tipo) {
            case DEPOSITO:
                nuevoMonto = saldo.getMontoDisponible().add(monto);
                break;
            case RETIRO:
            case PAGO_SERVICIO:
                if (saldo.getMontoDisponible().compareTo(monto) < 0) {
                    throw new SaldoInsuficienteException(entidadId, saldo.getMontoDisponible(), monto);
                }
                nuevoMonto = saldo.getMontoDisponible().subtract(monto);
                break;
            default:
                throw new OperacionInvalidaException(
                        String.format("Tipo de operación no válido: %s", request.getTipo()));
        }

        saldo.setMontoDisponible(nuevoMonto);
        saldoRepository.save(saldo);

        // Registrar operación
        Operacion operacion = mapper.toEntity(request);
        operacion.setEntidadBanco(banco);
        operacion.setEstado(EstadoOperacion.COMPLETADA);
        operacion.setActivo(true);
        operacion = repository.save(operacion);

        log.info("Operación creada: {}", operacion.getId());
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
            String tipoOperacion, String estadoOperacion, Long idEntidad, String finicio, String ffin, Pageable pageable) {

        // Paso 1: Resolver rango de fechas
        LocalDateTime fechaInicio;
        LocalDateTime fechaFin;

        if (finicio == null || finicio.trim().isEmpty()) {
            LocalDate hoy = LocalDate.now();
            fechaInicio = LocalDateTime.of(hoy, LocalTime.MIN);
        } else {
            fechaInicio = LocalDateTime.parse(finicio.trim());
        }

        if (ffin == null || ffin.trim().isEmpty()) {
            LocalDate hoy = LocalDate.now();
            fechaFin = LocalDateTime.of(hoy, LocalTime.MAX);
        } else {
            fechaFin = LocalDateTime.parse(ffin.trim());
        }

        // Paso 2: Resolver tipo de operación
        TipoOperacion tipo = null;
        if (tipoOperacion != null && !tipoOperacion.trim().isEmpty() && !"Todos".equalsIgnoreCase(tipoOperacion.trim())) {
            try {
                tipo = TipoOperacion.valueOf(tipoOperacion.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException(
                        String.format("Tipo de operación no válido: %s", tipoOperacion));
            }
        }

        // Paso 3: Resolver estado de operación
        EstadoOperacion estado;
        try {
            estado = EstadoOperacion.valueOf(estadoOperacion.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException(
                    String.format("Estado de operación no válido: %s", estadoOperacion));
        }

        // Paso 4: Ejecutar query según combinación de filtros
        Page<Operacion> page;

        if (idEntidad == null || idEntidad == 0) {
            // Sin filtro de entidad
            if (tipo == null) {
                // Sin tipo de operación
                page = repository.findByEstadoAndFechaBetweenAndActivo(
                        estado, fechaInicio, fechaFin, true, pageable);
            } else {
                // Con tipo de operación
                page = repository.findByTipoAndEstadoAndFechaBetweenAndActivo(
                        tipo, estado, fechaInicio, fechaFin, true, pageable);
            }
        } else {
            Entidad entidad = entidadRepository.findById(idEntidad)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Entidad bancaria no encontrada: %d", idEntidad)));
            // Con filtro de entidad
            if (tipo == null) {
                // Sin tipo de operación
                page = repository.findByEntidadBancoAndEstadoAndFechaBetweenAndActivo(
                        entidad, estado, fechaInicio, fechaFin, true, pageable);
            } else {
                // Con tipo de operación
                page = repository.findByEntidadBancoAndTipoAndEstadoAndFechaBetweenAndActivo(
                        entidad, tipo, estado, fechaInicio, fechaFin, true, pageable);
            }
        }

        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByUsuario(Long usuarioId, Pageable pageable) {
        Page<Operacion> page = repository.findByUsuarioIdAndActivo(usuarioId, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByTipoAndEntidad(String tipoOperacion, Long idEntidadFinanciera, Pageable pageable) {
        Page<Operacion> page = repository.findByTipoAndEntidadBancoAndActivo(tipoOperacion, idEntidadFinanciera, true, pageable);
        return buildPageResponse(page);
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

    private void validarBanco(Entidad entidad) {
        if (entidad.getTipoEntidad() != TipoEntidad.BANCO) {
            throw new BusinessException(
                    "La entidad seleccionada no es un banco"
            );
        }

        if (!entidad.isActivo()) {
            throw new BusinessException(
                    "El banco se encuentra inactivo"
            );
        }

        if (entidad.getEstado() != EstadoEntidad.ACTIVA) {
            throw new BusinessException(
                    "El banco no se encuentra habilitado"
            );
        }
    }

    private PageResponse<OperacionResponse> buildPageResponse(Page<Operacion> page) {
        return PageResponse.<OperacionResponse>builder()
                .content(page.getContent().stream().map(mapper::toResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
