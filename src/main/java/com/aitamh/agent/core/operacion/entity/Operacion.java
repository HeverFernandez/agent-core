package com.aitamh.agent.core.operacion.entity;

import com.aitamh.agent.core.entidadfinanciera.entity.EntidadFinanciera;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Operación (retiro, depósito, pago de servicio).
 */
@Entity
@Table(name = "operaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_operacion", nullable = false, length = 50)
    private String tipoOperacion; // retiro, deposito, pago de servicio

    @Column(name = "monto_operacion", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoOperacion;

    @Column(name = "comision", precision = 15, scale = 2)
    private BigDecimal comision;

    @Column(name = "descripcion_operacion", columnDefinition = "TEXT")
    private String descripcionOperacion;

    @Column(name = "numero_referencia", nullable = false, length = 50)
    private String numeroReferencia;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDateTime fechaOperacion;

    @Column(name = "id_entidad_financiera", nullable = false)
    private Long idEntidadFinanciera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entidad_financiera", referencedColumnName = "id", insertable = false, updatable = false)
    private EntidadFinanciera entidadFinanciera;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "estado_operacion", nullable = false, length = 50)
    private String estadoOperacion; // pendiente, completada, anulada, fallida

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
        fechaOperacion = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
