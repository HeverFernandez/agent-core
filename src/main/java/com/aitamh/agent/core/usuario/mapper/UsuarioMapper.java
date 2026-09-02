package com.aitamh.agent.core.usuario.mapper;

import com.aitamh.agent.core.usuario.dto.UsuarioRequest;
import com.aitamh.agent.core.usuario.dto.UsuarioResponse;
import com.aitamh.agent.core.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para conversiones entre Entity y DTOs de Usuario.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario entity);

    Usuario toEntity(UsuarioRequest request);

    void updateEntityFromRequest(UsuarioRequest request, @MappingTarget Usuario entity);
}

