package com.aitamh.agent.core.common.vo;

import com.aitamh.agent.core.common.exception.BusinessException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;

/**
 * Value Object que representa el monto de una operación.
 * Valida que el monto sea positivo y encapsula la lógica de validación.
 */
@EqualsAndHashCode
@ToString
public class MontoOperacion {

    private final BigDecimal valor;

    public MontoOperacion(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser positivo");
        }
        this.valor = valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Factory method para crear un MontoOperacion desde un double
     */
    public static MontoOperacion of(double monto) {
        return new MontoOperacion(BigDecimal.valueOf(monto));
    }

    /**
     * Factory method para crear un MontoOperacion desde un BigDecimal
     */
    public static MontoOperacion of(BigDecimal monto) {
        return new MontoOperacion(monto);
    }
}

