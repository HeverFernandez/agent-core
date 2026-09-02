package com.aitamh.agent.core.usuario.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.usuario.dto.UsuarioRequest;
import com.aitamh.agent.core.usuario.dto.UsuarioResponse;
import com.aitamh.agent.core.usuario.entity.Usuario;
import com.aitamh.agent.core.usuario.mapper.UsuarioMapper;
import com.aitamh.agent.core.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para Usuario.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    @Override
    public UsuarioResponse create(UsuarioRequest request) {
        validateUniqueCorreo(request.getCorreoElectronico(), null);

        Usuario entity = mapper.toEntity(request);
        entity.setActivo(true);
        entity = repository.save(entity);

        log.info("Usuario creado: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Usuario no encontrado: %d", id)));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findByCorreo(String correoElectronico) {
        Usuario entity = repository.findByCorreoElectronico(correoElectronico)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Usuario no encontrado con correo: %s", correoElectronico)));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> findAll(Pageable pageable) {
        Page<Usuario> page = repository.findByActivo(true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> findByRol(String rol, Pageable pageable) {
        Page<Usuario> page = repository.findByRolAndActivo(rol, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> findByEstado(Boolean estado, Pageable pageable) {
        Page<Usuario> page = repository.findByEstadoAndActivo(estado, true, pageable);
        return buildPageResponse(page);
    }

    @Override
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Usuario no encontrado: %d", id)));

        if (!entity.getCorreoElectronico().equals(request.getCorreoElectronico())) {
            validateUniqueCorreo(request.getCorreoElectronico(), id);
        }

        mapper.updateEntityFromRequest(request, entity);
        entity = repository.save(entity);

        log.info("Usuario actualizado: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    public void delete(Long id) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Usuario no encontrado: %d", id)));

        entity.setActivo(false);
        repository.save(entity);

        log.info("Usuario eliminado: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findByRolAndActivos(String rol) {
        return repository.findByRolAndActivoAndEstado(rol, true, true)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private PageResponse<UsuarioResponse> buildPageResponse(Page<Usuario> page) {
        return PageResponse.<UsuarioResponse>builder()
                .content(page.getContent().stream().map(mapper::toResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    private void validateUniqueCorreo(String correoElectronico, Long excludeId) {
        repository.findByCorreoElectronico(correoElectronico).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("El correo electrónico ya está registrado");
            }
        });
    }
}

