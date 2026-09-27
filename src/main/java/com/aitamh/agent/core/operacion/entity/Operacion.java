package com.aitamh.agent.core.operacion.entity;

import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Operación (retiro, depósito, pago de servicio).
 */
@Entity
@Table(name = "operaciones",
        indexes = {
                @Index(
                        name = "idx_operacion_tipo",
                        columnList = "tipo_operacion"
                ),
                @Index(
                        name = "idx_operacion_banco",
                        columnList = "entidad_banco_id"
                ),
                @Index(
                        name = "idx_operacion_servicio",
                        columnList = "entidad_servicio_id"
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoOperacion tipo; // retiro, deposito, pago de servicio

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "comision", precision = 15, scale = 2)
    private BigDecimal comision;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "numero_referencia", nullable = false, length = 50)
    private String numeroReferencia;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "entidad_banco_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_operacion_entidad_banco"
            )
    )
    private Entidad entidadBanco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entidad_servicio_id",
            foreignKey = @ForeignKey(
                    name = "fk_operacion_entidad_servicio"
            )
    )
    private Entidad entidadServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_id",
            foreignKey = @ForeignKey(
                    name = "fk_operacion_usuario"
            )
    )
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 100)
    private EstadoOperacion estado; // pendiente, completada, anulada, fallida

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

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
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        fecha = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
