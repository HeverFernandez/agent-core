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

//    @Mapping(source = "idEntidad", target = "entidadBanco.id")
    @Mapping(target = "entidad", source = "entidadBanco.denominacion")
    @Mapping(target = "servicio", source = "entidadServicio.denominacion")
    OperacionResponse toResponse(Operacion entity);

    @Mapping(target = "entidadBanco", ignore = true)
    @Mapping(target = "entidadServicio", ignore = true)
    Operacion toEntity(OperacionRequest request);
}
