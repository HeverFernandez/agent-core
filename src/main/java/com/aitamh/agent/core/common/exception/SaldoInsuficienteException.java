package com.aitamh.agent.core.common.exception;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando el saldo es insuficiente para realizar una operación.
 */
public class SaldoInsuficienteException extends BusinessException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }

    public SaldoInsuficienteException(Long entidadFinancieraId, BigDecimal disponible, BigDecimal solicitado) {
        super(String.format("Saldo insuficiente para realizar la operación. Entidad: %d, Saldo disponible: %s, monto solicitado: %s",
                entidadFinancieraId, disponible, solicitado));
    }
}

