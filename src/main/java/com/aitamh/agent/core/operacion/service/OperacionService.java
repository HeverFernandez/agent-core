package com.aitamh.agent.core.operacion.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.operacion.dto.OperacionResponse;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

/**
 * Interfaz de servicio para Operación.
 */
public interface OperacionService {

    OperacionResponse create(OperacionRequest request);

    OperacionResponse findById(Long id);

    PageResponse<OperacionResponse> findAll(String tipoOperacion, String estadoOperacion, Long entidad, String finicio, String ffin, Pageable pageable);;

    PageResponse<OperacionResponse> findByUsuario(Long usuarioId, Pageable pageable);

    PageResponse<OperacionResponse> findByTipoAndEntidad(String tipoOperacion, Long idEntidadFinanciera, Pageable pageable);

    OperacionResponse update(Long id, OperacionRequest request);

    void delete(Long id);
}

