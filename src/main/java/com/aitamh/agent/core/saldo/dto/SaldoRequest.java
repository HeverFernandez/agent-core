package com.aitamh.agent.core.saldo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de solicitud para crear o actualizar un Saldo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaldoRequest {

    @NotNull(message = "El ID de la entidad financiera es requerido")
    private Long entidadFinancieraId;

    @NotNull(message = "El monto inicial es requerido")
    @Positive(message = "El monto inicial debe ser positivo")
    private BigDecimal montoInicial;

    private String usuarioAsignador;
}

