package com.aitamh.agent.core.operacion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Operación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperacionResponse {

    private Long id;
    private String tipoOperacion;
    private BigDecimal montoOperacion;
    private BigDecimal comision;
    private String descripcionOperacion;
    private String numeroReferencia;
    private LocalDateTime fechaOperacion;
    private Long idEntidadFinanciera;
    private String entidadDenominacion;
    private Long usuarioId;
    private String estadoOperacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
