package com.aitamh.agent.core.saldo.mapper;

import com.aitamh.agent.core.saldo.dto.SaldoRequest;
import com.aitamh.agent.core.saldo.dto.SaldoResponse;
import com.aitamh.agent.core.saldo.entity.Saldo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para conversiones entre Entity y DTOs de Saldo.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SaldoMapper {

    SaldoResponse toResponse(Saldo entity);

    Saldo toEntity(SaldoRequest request);

    void updateEntityFromRequest(SaldoRequest request, @MappingTarget Saldo entity);
}

