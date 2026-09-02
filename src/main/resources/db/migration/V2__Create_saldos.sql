-- Flyway Migration V2: Crear tabla saldos
CREATE TABLE IF NOT EXISTS saldos (
    id BIGSERIAL PRIMARY KEY,
    entidad_financiera_id BIGINT NOT NULL,
    monto_inicial NUMERIC(15,2) NOT NULL,
    monto_disponible NUMERIC(15,2) NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL,
    fecha_vencimiento TIMESTAMP,
    estado VARCHAR(50) NOT NULL,
    usuario_asignador VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_saldos_entidad_financiera FOREIGN KEY (entidad_financiera_id)
        REFERENCES entidades_financieras(id) ON DELETE RESTRICT
);

CREATE INDEX idx_saldos_entidad_financiera ON saldos(entidad_financiera_id);
CREATE INDEX idx_saldos_fecha_asignacion ON saldos(fecha_asignacion);

