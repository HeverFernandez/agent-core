package com.aitamh.agent.core.common.exception;

/**
 * Excepción lanzada cuando el tipo de operación o sus datos son inválidos.
 */
public class OperacionInvalidaException extends BusinessException {

    public OperacionInvalidaException(String message) {
        super(message);
    }
}

