package com.aitamh.agent.core.common.exception;

/**
 * Excepción lanzada cuando no existe un saldo activo para una entidad financiera.
 * Extiende EntityNotFoundException para mapearse a 404 Not Found en la API.
 */
public class SaldoNotFoundException extends EntityNotFoundException {

    public SaldoNotFoundException(String message) {
        super(message);
    }

    public SaldoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}



