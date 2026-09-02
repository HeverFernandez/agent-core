package com.aitamh.agent.core.usuario.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.usuario.dto.UsuarioRequest;
import com.aitamh.agent.core.usuario.dto.UsuarioResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Interfaz de servicio para Usuario.
 */
public interface UsuarioService {

    UsuarioResponse create(UsuarioRequest request);

    UsuarioResponse findById(Long id);

    UsuarioResponse findByCorreo(String correoElectronico);

    PageResponse<UsuarioResponse> findAll(Pageable pageable);

    PageResponse<UsuarioResponse> findByRol(String rol, Pageable pageable);

    PageResponse<UsuarioResponse> findByEstado(Boolean estado, Pageable pageable);

    UsuarioResponse update(Long id, UsuarioRequest request);

    void delete(Long id);

    List<UsuarioResponse> findByRolAndActivos(String rol);
}

