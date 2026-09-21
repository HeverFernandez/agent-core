package com.aitamh.agent.core.operacion.constants;

public enum EstadoOperacion {
    PENDIENTE,
    COMPLETADA,
    ANULADA,
    FALLIDA;

    public static EstadoOperacion fromString(String value) {
        if (value == null) return null;
        try {
            return EstadoOperacion.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
