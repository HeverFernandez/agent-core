package com.aitamh.agent.core.entidadfinanciera.enums;

import lombok.Getter;

/**
 * Enum que representa los tipos de entidades financieras permitidas.
 */
@Getter
public enum TipoEntidad {
    BANCO("Banco"),
    SERVICIO("Servicio"),
    TODOS("Todos");

    private final String descripcion;

    TipoEntidad(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Valida si un valor string corresponde a un tipo de entidad válido.
     *
     * @param valor valor a validar (case-insensitive)
     * @return true si el valor es válido, false en caso contrario
     */
    public static boolean isValido(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }
        try {
            TipoEntidad.valueOf(valor.toUpperCase());
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * Obtiene el enum a partir de un string (case-insensitive).
     *
     * @param valor valor a convertir
     * @return el enum correspondiente
     * @throws IllegalArgumentException si el valor no es válido
     */
    public static TipoEntidad fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de entidad no puede estar vacío");
        }
        try {
            return TipoEntidad.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Tipo de entidad no válido: %s. Valores permitidos: BANCO, SERVICIO", valor), e);
        }
    }
}

