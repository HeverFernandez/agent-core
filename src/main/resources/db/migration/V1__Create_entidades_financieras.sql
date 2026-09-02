-- Flyway Migration V1: Crear tabla entidades_financieras
CREATE TABLE IF NOT EXISTS entidades_financieras (
    id BIGSERIAL PRIMARY KEY,
    tipo_entidad VARCHAR(50) NOT NULL,
    denominacion VARCHAR(255) NOT NULL,
    descripcion TEXT,
    codigo_entidad VARCHAR(50) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_entidades_financieras_codigo ON entidades_financieras(codigo_entidad);
CREATE INDEX idx_entidades_financieras_tipo ON entidades_financieras(tipo_entidad);

