package com.aitamh.agent.core.entidadfinanciera.dto;

import com.aitamh.agent.core.entidadfinanciera.enums.EstadoEntidad;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para EntidadFinanciera.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntidadResponse {

    private Long id;
    private String tipoEntidad;
    private String denominacion;
    private String descripcion;
    private String codigoEntidad;
    private EstadoEntidad estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

