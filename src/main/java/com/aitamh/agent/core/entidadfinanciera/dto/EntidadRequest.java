package com.aitamh.agent.core.entidadfinanciera.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO de solicitud para crear o actualizar una EntidadFinanciera.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntidadRequest {

    @NotBlank(message = "El tipo de entidad es requerido")
    private String tipoEntidad;

    @NotBlank(message = "La denominación es requerida")
    private String denominacion;

    private String descripcion;

}

