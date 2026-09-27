package com.aitamh.agent.core.entidadfinanciera.entity;

import com.aitamh.agent.core.entidadfinanciera.enums.EstadoEntidad;
import com.aitamh.agent.core.entidadfinanciera.enums.TipoEntidad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Entidad Financiera (banco o servicio).
 */
@Entity
@Table(name = "entidades_financieras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidad", nullable = false, length = 50)
    private TipoEntidad tipoEntidad; // banco, servicio

    @Column(name = "denominacion", nullable = false, length = 255)
    private String denominacion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "codigo_entidad", nullable = false, length = 50, unique = true)
    private String codigoEntidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEntidad estado = EstadoEntidad.ACTIVA;

    //Para la eliminación lógica
    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

