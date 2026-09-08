package com.aitamh.agent.core.entidadfinanciera.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraResponse;
import com.aitamh.agent.core.entidadfinanciera.entity.EntidadFinanciera;
import com.aitamh.agent.core.entidadfinanciera.enums.TipoEntidad;
import com.aitamh.agent.core.entidadfinanciera.mapper.EntidadFinancieraMapper;
import com.aitamh.agent.core.entidadfinanciera.repository.EntidadFinancieraRepository;
import com.aitamh.agent.core.entidadfinanciera.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para EntidadFinanciera.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EntidadFinancieraServiceImpl implements EntidadFinancieraService {

    private final EntidadFinancieraRepository repository;
    private final EntidadFinancieraMapper mapper;
    private final Util util;

    @Override
    public EntidadFinancieraResponse create(EntidadFinancieraRequest request) {
        // Validar que el tipo de entidad sea válido
        if (!TipoEntidad.isValido(request.getTipoEntidad())) {
            throw new BusinessException(
                    String.format("Tipo de entidad no válido: %s. Valores permitidos: BANCO, SERVICIO", request.getTipoEntidad()));
        }

        // Validar unicidad de tipoEntidad + denominacion
        repository.findByTipoEntidadAndDenominacion(request.getTipoEntidad(), request.getDenominacion())
                .ifPresent(existing -> {
                    throw new BusinessException(
                            String.format("Ya existe una entidad financiera con tipo '%s' y denominación '%s'",
                                    request.getTipoEntidad(), request.getDenominacion()));
                });

        String codigo = util.generaCodigoEntidad(
                request, repository::existsByCodigoEntidad);

        EntidadFinanciera entity = mapper.toEntity(request);
        entity.setCodigoEntidad(codigo);
        entity.setActivo(true);
        entity.setEstado(true);
        entity = repository.save(entity);

        log.info("EntidadFinanciera creada: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public EntidadFinancieraResponse findById(Long id) {
        EntidadFinanciera entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("EntidadFinanciera no encontrada: %d", id)));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EntidadFinancieraResponse> findAll(Pageable pageable) {
        Page<EntidadFinanciera> page = repository.findByActivo(true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EntidadFinancieraResponse> findByTipo(String tipoEntidad, Pageable pageable) {
        if (!TipoEntidad.isValido(tipoEntidad)) {
            throw new BusinessException(
                    String.format("Tipo de entidad no válido: %s. Valores permitidos: BANCO, SERVICIO", tipoEntidad));
        }

        Page<EntidadFinanciera> page = repository.findByTipoEntidadAndActivo(tipoEntidad, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    public EntidadFinancieraResponse update(Long id, EntidadFinancieraRequest request) {
        EntidadFinanciera entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("EntidadFinanciera no encontrada: %d", id)));

        // Validar que el tipo de entidad sea válido
        if (!TipoEntidad.isValido(request.getTipoEntidad())) {
            throw new BusinessException(
                    String.format("Tipo de entidad no válido: %s. Valores permitidos: BANCO, SERVICIO", request.getTipoEntidad()));
        }

        // Validar unicidad de tipoEntidad + denominacion, excluyendo la entidad actual
        repository.findByTipoEntidadAndDenominacion(request.getTipoEntidad(), request.getDenominacion())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException(
                                String.format("Ya existe otra entidad financiera con tipo '%s' y denominación '%s'",
                                        request.getTipoEntidad(), request.getDenominacion()));
                    }
                });

        mapper.updateEntityFromRequest(request, entity);
        entity = repository.save(entity);

        log.info("EntidadFinanciera actualizada: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        EntidadFinanciera entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("EntidadFinanciera no encontrada: %d", id)));

        entity.setActivo(false);
        repository.save(entity);

        log.info("EntidadFinanciera eliminada: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntidadFinancieraResponse> getAllActive() {
        return repository.findByActivoAndEstado(true, true)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private PageResponse<EntidadFinancieraResponse> buildPageResponse(Page<EntidadFinanciera> page) {
        return PageResponse.<EntidadFinancieraResponse>builder()
                .content(page.getContent().stream().map(mapper::toResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    private void validateUniqueCodigoEntidad(String codigoEntidad, Long excludeId) {
        repository.findByCodigoEntidad(codigoEntidad).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("El código de entidad ya existe");
            }
        });
    }
}

