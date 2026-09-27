package com.aitamh.agent.core.saldo.entity;

import com.aitamh.agent.core.saldo.enums.EstadoSaldo;
import jakarta.persistence.*;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Saldo asignado a una EntidadFinanciera.
 */
@Entity
@Table(
        name = "saldos",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_saldo_entidad",
                columnNames = "entidad_id"
        )
        }
       )
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Saldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "entidad_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_saldo_entidad"
            )
    )
    private Entidad entidad;

    @Column(name = "monto_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "monto_disponible", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoDisponible;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Enumerated(EnumType.STRING)
    private EstadoSaldo estado;

    @Column(name = "usuario_asignador", length = 100)
    private String usuarioAsignador;

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
        fechaAsignacion=LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

