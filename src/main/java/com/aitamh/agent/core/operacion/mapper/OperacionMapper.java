package com.aitamh.agent.core.operacion.mapper;

import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.operacion.dto.OperacionResponse;
import com.aitamh.agent.core.operacion.entity.Operacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para conversiones entre Entity y DTOs de Operación.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OperacionMapper {

    @Mapping(source = "entidadFinanciera.denominacion", target = "entidadDenominacion")
    OperacionResponse toResponse(Operacion entity);

    Operacion toEntity(OperacionRequest request);
}
