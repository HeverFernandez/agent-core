package com.aitamh.agent.core.entidadfinanciera.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO de solicitud para crear o actualizar una EntidadFinanciera.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntidadFinancieraRequest {

    @NotBlank(message = "El tipo de entidad es requerido")
    private String tipoEntidad;

    @NotBlank(message = "La denominación es requerida")
    private String denominacion;

    private String descripcion;

//    @NotBlank(message = "El código de entidad es requerido")
//    private String codigoEntidad;

//    @NotNull(message = "El estado es requerido")
//    private Boolean estado;

}

