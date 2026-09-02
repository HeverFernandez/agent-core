package com.aitamh.agent.core.entidadfinanciera.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Interfaz de servicio para EntidadFinanciera.
 */
public interface EntidadFinancieraService {

    EntidadFinancieraResponse create(EntidadFinancieraRequest request);

    EntidadFinancieraResponse findById(Long id);

    PageResponse<EntidadFinancieraResponse> findAll(Pageable pageable);

    PageResponse<EntidadFinancieraResponse> findByTipo(String tipoEntidad, Pageable pageable);

    EntidadFinancieraResponse update(Long id, EntidadFinancieraRequest request);

    void delete(Long id);

    List<EntidadFinancieraResponse> getAllActive();
}

