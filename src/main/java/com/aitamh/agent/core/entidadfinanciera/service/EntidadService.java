package com.aitamh.agent.core.entidadfinanciera.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Interfaz de servicio para EntidadFinanciera.
 */
public interface EntidadService {

    EntidadResponse create(EntidadRequest request);

    EntidadResponse findById(Long id);

    PageResponse<EntidadResponse> findByTipo(String tipoEntidad, String searchTerm, Pageable pageable);

    EntidadResponse update(Long id, EntidadRequest request);

    void delete(Long id);

    List<EntidadResponse> getAllActive(String tipo);
}

