package com.aitamh.agent.core.entidadfinanciera.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadResponse;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.entidadfinanciera.enums.EstadoEntidad;
import com.aitamh.agent.core.entidadfinanciera.enums.TipoEntidad;
import com.aitamh.agent.core.entidadfinanciera.mapper.EntidadMapper;
import com.aitamh.agent.core.entidadfinanciera.repository.EntidadRepository;
import com.aitamh.agent.core.entidadfinanciera.utils.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.aitamh.agent.core.entidadfinanciera.constants.EntidadConstants.*;

/**
 * Implementación del servicio para EntidadFinanciera.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EntidadServiceImpl implements EntidadService {

    private final EntidadRepository repository;
    private final EntidadMapper mapper;
    private final Util util;

    @Override
    public EntidadResponse create(EntidadRequest request) {
        // Validar que el tipo de entidad sea válido
        if (TipoEntidad.isValido(request.getTipoEntidad())) {
            throw new BusinessException(
                    String.format(TIPOS_PERMITIDOS, request.getTipoEntidad()));
        }
        TipoEntidad tipoEntidad = TipoEntidad.valueOf(request.getTipoEntidad().trim().toUpperCase());
        // Validar unicidad de tipoEntidad + denominacion
        repository.findByTipoEntidadAndDenominacion(tipoEntidad, request.getDenominacion())
                .ifPresent(existing -> {
                    throw new BusinessException(
                            String.format(EXIST_ENTIDAD,
                                    request.getTipoEntidad(), request.getDenominacion()));
                });

        String codigo = util.generaCodigoEntidad(
                request, repository::existsByCodigoEntidad);

        Entidad entity = mapper.toEntity(request);
        entity.setCodigoEntidad(codigo);
        entity.setActivo(true);
        entity.setEstado(EstadoEntidad.ACTIVA);

        if (entity.getTipoEntidad() == TipoEntidad.BANCO) {
            entity = repository.save(entity);
        } else {
            entity.setCodigoEntidad(null);
        }

        log.info("EntidadFinanciera creada: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public EntidadResponse findById(Long id) {
        Entidad entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(NOT_FOUND_ENTIDAD, id)));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntidadResponse> getAllActive(String tipo) {
        if (TipoEntidad.isValido(tipo)) {
            throw new BusinessException(
                    String.format(TIPOS_PERMITIDOS, tipo));
        }
        TipoEntidad tipoEntidad = TipoEntidad.valueOf(tipo.trim().toUpperCase());

        return repository.findByTipoEntidadAndActivoAndEstado(tipoEntidad, true, EstadoEntidad.ACTIVA)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<EntidadResponse> findAll(Pageable pageable) {
        Page<Entidad> page = repository.findByActivo(true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EntidadResponse> findByTipo(String tipoEntidad, String searchTerm, Pageable pageable) {
        String term = (searchTerm == null) ? "" : searchTerm.trim();
        // If a searchTerm is provided, validate minimum length and perform search
        if (!term.isEmpty()) {
            if (term.length() < 3) {
                throw new BusinessException("El término de búsqueda debe tener al menos 3 caracteres");
            }

            // If tipoEntidad == TODOS -> search across all active entities
            if ("TODOS".equalsIgnoreCase(tipoEntidad)) {
                Page<Entidad> page = repository.searchAllByDenominacionOrCodigo(term, pageable);
                return buildPageResponse(page);
            }

            // Validate tipoEntidad when not TODOS
            if (TipoEntidad.isValido(tipoEntidad)) {
                throw new BusinessException(
                        String.format(TIPOS_PERMITIDOS, tipoEntidad));
            }

            TipoEntidad entidadTipo = TipoEntidad.valueOf(tipoEntidad.trim().toUpperCase());
            Page<Entidad> page = repository.searchByTipoAndDenominacionOrCodigo(entidadTipo, term, pageable);
            return buildPageResponse(page);
        }

        // No search term: default behavior
        if ("TODOS".equalsIgnoreCase(tipoEntidad)) {
            return findAll(pageable);
        }

        if (TipoEntidad.isValido(tipoEntidad)) {
            throw new BusinessException(
                    String.format(TIPOS_PERMITIDOS, tipoEntidad));
        }
        TipoEntidad entidadTipo = TipoEntidad.valueOf(tipoEntidad.trim().toUpperCase());

        Page<Entidad> page = repository.findByTipoEntidadAndActivo(entidadTipo, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    public EntidadResponse update(Long id, EntidadRequest request) {
        Entidad entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(NOT_FOUND_ENTIDAD, id)));

        // Validar que el tipo de entidad sea válido
        if (TipoEntidad.isValido(request.getTipoEntidad())) {
            throw new BusinessException(
                    String.format(TIPOS_PERMITIDOS, request.getTipoEntidad()));
        }

        TipoEntidad entidadTipo = TipoEntidad.valueOf(request.getTipoEntidad().trim().toUpperCase());
        // Validar unicidad de tipoEntidad + denominacion, excluyendo la entidad actual
        repository.findByTipoEntidadAndDenominacion(entidadTipo, request.getDenominacion())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException(
                                String.format(EXIST_ENTIDAD,
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
        Entidad entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(NOT_FOUND_ENTIDAD, id)));

        entity.setActivo(false);
        repository.save(entity);

        log.info("EntidadFinanciera eliminada: {}", id);
    }

    private PageResponse<EntidadResponse> buildPageResponse(Page<Entidad> page) {
        return PageResponse.<EntidadResponse>builder()
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

