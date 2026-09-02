package com.aitamh.agent.core.operacion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO de solicitud para crear una Operación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperacionRequest {

    @NotBlank(message = "El tipo de operación es requerido")
    private String tipoOperacion;

    @NotNull(message = "El monto de operación es requerido")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal montoOperacion;

    private BigDecimal comision;

    @NotBlank(message = "La descripción es requerida")
    private String descripcionOperacion;

    @NotBlank(message = "El número de referencia es requerido")
    private String numeroReferencia;

    @NotNull(message = "El ID de la entidad financiera es requerido")
    private Long idEntidadFinanciera;

    @NotNull(message = "El ID de usuario es requerido")
    private Long usuarioId;
}

