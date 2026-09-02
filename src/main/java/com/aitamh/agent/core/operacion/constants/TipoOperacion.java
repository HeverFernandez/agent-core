package com.aitamh.agent.core.operacion.constants;

/**
 * Enum con los tipos de operación soportados.
 */
public enum TipoOperacion {
    DEPOSITO,
    RETIRO,
    PAGO_SERVICIO;

    public static TipoOperacion fromString(String value) {
        if (value == null) return null;
        try {
            return TipoOperacion.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

