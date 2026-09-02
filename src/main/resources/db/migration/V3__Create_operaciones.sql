-- Flyway Migration V3: Crear tabla operaciones
CREATE TABLE IF NOT EXISTS operaciones (
    id BIGSERIAL PRIMARY KEY,
    tipo_operacion VARCHAR(50) NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    descripcion_operacion TEXT,
    numero_referencia VARCHAR(50) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    id_entidad_financiera BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado_operacion VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_operaciones_entidad_financiera FOREIGN KEY (id_entidad_financiera)
        REFERENCES entidades_financieras(id) ON DELETE RESTRICT,
    CONSTRAINT fk_operaciones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE INDEX idx_operaciones_tipo ON operaciones(tipo_operacion);
CREATE INDEX idx_operaciones_entidad_financiera ON operaciones(id_entidad_financiera);
CREATE INDEX idx_operaciones_usuario ON operaciones(usuario_id);
CREATE INDEX idx_operaciones_numero_referencia ON operaciones(numero_referencia);

