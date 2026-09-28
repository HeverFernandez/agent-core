package com.aitamh.agent.core.saldo.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.entidadfinanciera.repository.EntidadRepository;
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

import static com.aitamh.agent.core.saldo.constants.SaldoConstants.EXIST_SALDO;

/**
 * Implementación del servicio para Saldo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SaldoServiceImpl implements SaldoService {

    private final SaldoRepository repository;
    private final EntidadRepository entidadRepository;
    private final SaldoMapper mapper;

    @Override
    public SaldoResponse create(SaldoRequest request) {
        validateSaldoRequest(request);

        Entidad entidad = entidadRepository.findById(request.getEntidadId())
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada"));

        validateUniqueActiveOrBlockedSaldo(request.getEntidadId());

        Saldo saldo = mapper.toEntity(request);
        saldo.setEntidad(entidad);
        saldo.setMontoDisponible(request.getMontoInicial());
        saldo.setEstado(EstadoSaldo.ACTIVO);
        saldo = repository.save(saldo);

        log.info("Saldo creado: {}", saldo.getId());
        return mapper.toResponse(saldo);
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
    public PageResponse<SaldoResponse> findAll(String entidad, String estado, Pageable pageable) {
        EstadoSaldo estadoSaldo = EstadoSaldo.valueOf(estado.toUpperCase());

        if (entidad != null && !entidad.isBlank()) {
            String entidadBusqueda = entidad.trim();
            if (entidadBusqueda.length() < 3) {
                throw new BusinessException("El término de búsqueda de entidad debe tener al menos 3 caracteres");
            }

            Page<Saldo> page = repository.findByEntidadAndEstado(entidadBusqueda, estadoSaldo, pageable);
            return buildPageResponse(page);
        }

        Page<Saldo> page = repository.findByEstado(estadoSaldo, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SaldoResponse> findByEntidadFinanciera(Long entidadId, Pageable pageable) {
        Entidad entidad = entidadRepository.findById(entidadId)
                .orElseThrow(() -> new EntityNotFoundException("No existe entidad con el id: " + entidadId));

        Page<Saldo> page = repository.findByEntidad(entidad, pageable);

        return buildPageResponse(page);
    }

    @Override
    public SaldoResponse update(Long id, SaldoRequest request) {
        Saldo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Saldo no encontrado: %d", id)));

        validateUniqueActiveOrBlockedSaldo(request.getEntidadId());
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

        boolean exists = repository.existsByEntidad_IdAndEstadoIn(entidadFinancieraId, estadosBloqueados);

        if (exists) {
            throw new BusinessException(
                    String.format(EXIST_SALDO, entidadFinancieraId));
        }
    }
}
