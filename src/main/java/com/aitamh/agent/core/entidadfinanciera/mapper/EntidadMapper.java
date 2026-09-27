package com.aitamh.agent.core.entidadfinanciera.mapper;

import com.aitamh.agent.core.entidadfinanciera.dto.EntidadRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadResponse;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para conversiones entre Entity y DTOs de EntidadFinanciera.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntidadMapper {

    EntidadResponse toResponse(Entidad entity);

    Entidad toEntity(EntidadRequest request);

    void updateEntityFromRequest(EntidadRequest request, @MappingTarget Entidad entity);
}

