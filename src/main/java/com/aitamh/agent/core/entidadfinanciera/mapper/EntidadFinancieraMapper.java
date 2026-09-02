package com.aitamh.agent.core.entidadfinanciera.mapper;

import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraResponse;
import com.aitamh.agent.core.entidadfinanciera.entity.EntidadFinanciera;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para conversiones entre Entity y DTOs de EntidadFinanciera.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntidadFinancieraMapper {

    EntidadFinancieraResponse toResponse(EntidadFinanciera entity);

    EntidadFinanciera toEntity(EntidadFinancieraRequest request);

    void updateEntityFromRequest(EntidadFinancieraRequest request, @MappingTarget EntidadFinanciera entity);
}

