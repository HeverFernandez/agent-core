package com.aitamh.agent.core.saldo.service;

import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.saldo.dto.SaldoRequest;
import com.aitamh.agent.core.saldo.dto.SaldoResponse;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz de servicio para Saldo.
 */
public interface SaldoService {

    SaldoResponse create(SaldoRequest request);

    SaldoResponse findById(Long id);

    PageResponse<SaldoResponse> findAll(Pageable pageable);

    PageResponse<SaldoResponse> findByEntidadFinanciera(Long entidadFinancieraId, Pageable pageable);

    PageResponse<SaldoResponse> findByEstado(String estado, Pageable pageable);

    SaldoResponse update(Long id, SaldoRequest request);

    void delete(Long id);

    List<SaldoResponse> findByEntidadFinancieraAndEstado(Long entidadFinancieraId, String estado);

    void deductBalance(Long saldoId, BigDecimal monto);
}

