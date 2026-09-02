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

    PageResponse<OperacionResponse> findAll(Pageable pageable);

    PageResponse<OperacionResponse> findByTipo(String tipoOperacion, Pageable pageable);

    PageResponse<OperacionResponse> findByEntidadFinanciera(Long idEntidadFinanciera, Pageable pageable);

    PageResponse<OperacionResponse> findByUsuario(Long usuarioId, Pageable pageable);

    PageResponse<OperacionResponse> findByEstado(String estadoOperacion, Pageable pageable);

    PageResponse<OperacionResponse> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    PageResponse<OperacionResponse> findByTipoAndEntidad(String tipoOperacion, Long idEntidadFinanciera, Pageable pageable);

    OperacionResponse update(Long id, OperacionRequest request);

    void delete(Long id);
}

