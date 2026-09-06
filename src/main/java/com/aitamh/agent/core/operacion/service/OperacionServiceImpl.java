package com.aitamh.agent.core.operacion.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.common.exception.OperacionInvalidaException;
import com.aitamh.agent.core.common.exception.SaldoInsuficienteException;
import com.aitamh.agent.core.common.exception.SaldoNotFoundException;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.operacion.dto.OperacionResponse;
import com.aitamh.agent.core.operacion.entity.Operacion;
import com.aitamh.agent.core.operacion.mapper.OperacionMapper;
import com.aitamh.agent.core.operacion.repository.OperacionRepository;
import com.aitamh.agent.core.saldo.entity.Saldo;
import com.aitamh.agent.core.saldo.enums.EstadoSaldo;
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
    private final com.aitamh.agent.core.saldo.repository.SaldoRepository saldoRepository;
    private final com.aitamh.agent.core.saldo.service.SaldoService saldoService;

    @Override
    public OperacionResponse create(OperacionRequest request) {

        // Validar tipo de operación
        TipoOperacion tipo = TipoOperacion.fromString(request.getTipoOperacion());
        if (tipo == null) {
            throw new OperacionInvalidaException(
                    String.format("Tipo de operación no válido: %s", request.getTipoOperacion()));
        }

        Long entidadId = request.getIdEntidadFinanciera();

        // Obtener saldo con estado VIGENTE con bloqueo para evitar condiciones de carrera
        Saldo saldo = saldoRepository
                .findTopByEntidadFinancieraIdAndEstadoForUpdate(entidadId, EstadoSaldo.ACTIVO)
                .orElseThrow(() -> new SaldoNotFoundException(
                        String.format("No existe un saldo con estado '%s' para la entidad financiera %d", EstadoSaldo.ACTIVO, entidadId)));

        BigDecimal monto = request.getMontoOperacion();
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
                        String.format("Tipo de operación no válido: %s", request.getTipoOperacion()));
        }

        saldo.setMontoDisponible(nuevoMonto);
        saldoRepository.save(saldo);

        // Registrar operación
        Operacion entity = mapper.toEntity(request);
        entity.setEstadoOperacion("completada");
        entity.setActivo(true);
        entity = repository.save(entity);

        log.info("Operación creada: {}", entity.getId());
        return mapper.toResponse(entity);
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
    public PageResponse<OperacionResponse> findAll(Pageable pageable) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDelDia = LocalDateTime.of(hoy, LocalTime.MIN);
        LocalDateTime finDelDia = LocalDateTime.of(hoy, LocalTime.MAX);

        Page<Operacion> page = repository.findByFechaOperacionBetweenAndActivo(inicioDelDia, finDelDia, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByTipo(String tipoOperacion, Pageable pageable) {
        Page<Operacion> page = repository.findByTipoOperacionAndActivo(tipoOperacion, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByEntidadFinanciera(Long idEntidadFinanciera, Pageable pageable) {
        Page<Operacion> page = repository.findByIdEntidadFinancieraAndActivo(idEntidadFinanciera, true, pageable);
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
    public PageResponse<OperacionResponse> findByEstado(String estadoOperacion, Pageable pageable) {
        Page<Operacion> page = repository.findByEstadoOperacionAndActivo(estadoOperacion, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
        Page<Operacion> page = repository.findByFechaOperacionBetweenAndActivo(inicio, fin, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OperacionResponse> findByTipoAndEntidad(String tipoOperacion, Long idEntidadFinanciera, Pageable pageable) {
        Page<Operacion> page = repository.findByTipoOperacionAndIdEntidadFinancieraAndActivo(tipoOperacion, idEntidadFinanciera, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    public OperacionResponse update(Long id, OperacionRequest request) {
        Operacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Operación no encontrada: %d", id)));

        entity.setTipoOperacion(request.getTipoOperacion());
        entity.setMontoOperacion(request.getMontoOperacion());
        entity.setDescripcionOperacion(request.getDescripcionOperacion());
        entity.setNumeroReferencia(request.getNumeroReferencia());
        entity.setIdEntidadFinanciera(request.getIdEntidadFinanciera());
        entity.setUsuarioId(request.getUsuarioId());

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
