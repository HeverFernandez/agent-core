//package com.aitamh.agent.core.reporte.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import java.time.LocalDateTime;
//
///**
// * Entidad que representa un Reporte.
// */
//@Entity
//@Table(name = "reportes")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Reporte {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "tipo_reporte", nullable = false, length = 50)
//    private String tipoReporte; // operaciones, saldos, usuarios
//
//    @Column(name = "fecha_generacion", nullable = false)
//    private LocalDateTime fechaGeneracion;
//
//    @Column(name = "filtros_aplicados", columnDefinition = "TEXT")
//    private String filtrosAplicados;
//
//    @Column(name = "datos_reporte", columnDefinition = "TEXT")
//    private String datosReporte;
//
//    @Column(name = "usuario_id")
//    private Long usuarioId;
//
//    @Column(name = "activo", nullable = false)
//    private Boolean activo = true;
//
//    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "created_by", length = 100)
//    private String createdBy;
//
//    @Column(name = "updated_by", length = 100)
//    private String updatedBy;
//
//    @PrePersist
//    protected void onCreate() {
//        if (createdAt == null) {
//            createdAt = LocalDateTime.now();
//        }
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//}
//
