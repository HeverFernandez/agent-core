package com.aitamh.agent.core.saldo.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.saldo.dto.SaldoRequest;
import com.aitamh.agent.core.saldo.dto.SaldoResponse;
import com.aitamh.agent.core.saldo.entity.Saldo;
import com.aitamh.agent.core.saldo.enums.EstadoSaldo;
import com.aitamh.agent.core.saldo.mapper.SaldoMapper;
import com.aitamh.agent.core.saldo.repository.SaldoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para Saldo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SaldoServiceImpl implements SaldoService {

    private final SaldoRepository repository;
    private final SaldoMapper mapper;

    @Override
    public SaldoResponse create(SaldoRequest request) {
        validateSaldoRequest(request);
        validateUniqueActiveOrBlockedSaldo(request.getEntidadFinancieraId());

        Saldo entity = mapper.toEntity(request);
        entity.setMontoDisponible(request.getMontoInicial());
        entity.setEstado(EstadoSaldo.ACTIVO);
        entity = repository.save(entity);

        log.info("Saldo creado: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SaldoResponse findById(Long id) {
        Saldo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Saldo no encontrado: %d", id)));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SaldoResponse> findAll(Pageable pageable) {
        Page<Saldo> page = repository.findByEstado(EstadoSaldo.ACTIVO, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SaldoResponse> findByEntidadFinanciera(Long entidadFinancieraId, Pageable pageable) {
        Page<Saldo> page = repository.findByEntidadFinancieraId(entidadFinancieraId, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SaldoResponse> findByEstado(String estado, Pageable pageable) {
        EstadoSaldo estadoSaldo = EstadoSaldo.valueOf(estado.toUpperCase());
        Page<Saldo> page = repository.findByEstado(estadoSaldo, pageable);
        return buildPageResponse(page);
    }

    @Override
    public SaldoResponse update(Long id, SaldoRequest request) {
        Saldo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Saldo no encontrado: %d", id)));

        validateUniqueActiveOrBlockedSaldo(request.getEntidadFinancieraId());
        validateSaldoRequest(request);
        mapper.updateEntityFromRequest(request, entity);
        entity = repository.save(entity);

        log.info("Saldo actualizado: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        Saldo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Saldo no encontrado: %d", id)));

        entity.setEstado(EstadoSaldo.ANULADO);
        repository.save(entity);

        log.info("Saldo eliminado: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaldoResponse> findByEntidadFinancieraAndEstado(Long entidadFinancieraId, String estado) {
        EstadoSaldo estadoSaldo = EstadoSaldo.valueOf(estado.toUpperCase());
        return repository.findByEntidadFinancieraIdAndEstado(entidadFinancieraId, estadoSaldo)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deductBalance(Long saldoId, BigDecimal monto) {
        Saldo saldo = repository.findById(saldoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Saldo no encontrado: %d", saldoId)));

        if (saldo.getMontoDisponible().compareTo(monto) < 0) {
            throw new BusinessException("Saldo insuficiente para realizar la operación");
        }

        saldo.setMontoDisponible(saldo.getMontoDisponible().subtract(monto));
        repository.save(saldo);

        log.info("Saldo deducido: {} de {}", monto, saldoId);
    }

    private PageResponse<SaldoResponse> buildPageResponse(Page<Saldo> page) {
        return PageResponse.<SaldoResponse>builder()
                .content(page.getContent().stream().map(mapper::toResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    private void validateSaldoRequest(SaldoRequest request) {
        if (request == null || request.getMontoInicial() == null) {
            throw new BusinessException("El monto inicial es requerido");
        }
        if (request.getMontoInicial().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto inicial debe ser positivo");
        }
    }

    private void validateUniqueActiveOrBlockedSaldo(Long entidadFinancieraId) {
        List<EstadoSaldo> estadosBloqueados = Arrays.asList(EstadoSaldo.ACTIVO, EstadoSaldo.BLOQUEADO);

        boolean exists = repository.existsByEntidadFinancieraIdAndEstadoIn(entidadFinancieraId, estadosBloqueados);

        if (exists) {
            throw new BusinessException(
                    String.format("Ya existe un saldo activo o bloqueado para la entidad financiera %d", entidadFinancieraId));
        }
    }
}
