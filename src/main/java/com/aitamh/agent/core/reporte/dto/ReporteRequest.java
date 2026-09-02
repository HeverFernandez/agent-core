//package com.aitamh.agent.core.reporte.dto;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import java.time.LocalDateTime;
//
///**
// * DTO de solicitud para crear un Reporte.
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ReporteRequest {
//
//    @NotBlank(message = "El tipo de reporte es requerido")
//    private String tipoReporte;
//
//    private String filtrosAplicados;
//
//    private String datosReporte;
//
//    @NotNull(message = "El ID de usuario es requerido")
//    private Long usuarioId;
//}
//
