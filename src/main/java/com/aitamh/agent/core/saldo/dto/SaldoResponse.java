package com.aitamh.agent.core.saldo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Saldo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaldoResponse {

    private Long id;
    private Long entidadFinancieraId;
    private String entidadDenominacion;
    private BigDecimal montoInicial;
    private BigDecimal montoDisponible;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaVencimiento;
    private String estado;
    private String usuarioAsignador;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

