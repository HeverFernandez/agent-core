package com.aitamh.agent.core.common.vo;

import com.aitamh.agent.core.common.exception.BusinessException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Value Object que representa el número de referencia de una operación.
 * Valida que el número sea válido y encapsula la lógica de validación.
 */
@EqualsAndHashCode
@ToString
public class NumeroReferencia {

    private final String referencia;
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 20;

    public NumeroReferencia(String referencia) {
        if (referencia == null || referencia.trim().isEmpty()) {
            throw new BusinessException("El número de referencia no puede estar vacío");
        }

        String trimmed = referencia.trim().toUpperCase();

        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new BusinessException(
                    String.format("El número de referencia debe tener entre %d y %d caracteres",
                    MIN_LENGTH, MAX_LENGTH)
            );
        }

        this.referencia = trimmed;
    }

    public String getReferencia() {
        return referencia;
    }

    /**
     * Factory method para crear un NumeroReferencia
     */
    public static NumeroReferencia of(String referencia) {
        return new NumeroReferencia(referencia);
    }
}

