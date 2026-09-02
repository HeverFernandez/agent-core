package com.aitamh.agent.core.saldo.enums;

/*
* Enum que representa los posibles estados de un saldo.
* ACTIVO: El saldo está disponible para operaciones.
* BLOQUEADO: El saldo está bloqueado y no se pueden realizar operaciones.
* CERRADO: El saldo ha sido cerrado y no se pueden realizar operaciones.
* ANULADO: El saldo ha sido anulado y no se pueden realizar operaciones. Saldo invalido
 */
public enum EstadoSaldo {
    ACTIVO,
    BLOQUEADO,
    CERRADO,
    ANULADO
}
