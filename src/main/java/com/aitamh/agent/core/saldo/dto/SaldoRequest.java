package com.aitamh.agent.core.saldo.dto;

import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de solicitud para crear o actualizar un Saldo.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class    SaldoRequest {

    @NotNull(message = "La entidad financiera a asignar saldo es requerido")
    private Long entidadId;

    @NotNull(message = "El monto inicial es requerido")
    @Positive(message = "El monto inicial debe ser positivo")
    private BigDecimal montoInicial;

    private String usuarioAsignador;
}

