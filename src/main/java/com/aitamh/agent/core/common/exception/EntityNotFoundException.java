package com.aitamh.agent.core.common.exception;

/**
 * Excepción lanzada cuando una entidad solicitada no es encontrada.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

