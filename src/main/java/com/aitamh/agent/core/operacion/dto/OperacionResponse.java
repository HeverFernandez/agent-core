package com.aitamh.agent.core.operacion.dto;

import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
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
    private String tipo;
    private BigDecimal monto;
    private BigDecimal comision;
    private String descripcion;
    private String numeroReferencia;
    private LocalDateTime fecha;
    private Long idEntidad;
    private String entidad;
    private String servicio;
    private Long usuarioId;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
