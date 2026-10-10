package com.aitamh.agent.core.operacion.validator;

import com.aitamh.agent.core.common.exception.OperacionInvalidaException;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validador para los parámetros de filtro de operaciones.
 * Valida y convierte los parámetros de filtro enviados en las requests.
 * Sigue el Single Responsibility Principle: responsable únicamente de validar filtros.
 */
@Component
public class OperacionFiltroValidator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Valida y obtiene el tipo de operación desde el parámetro.
     * Maneja especialmente el caso "Todos" que significa sin filtro.
     *
     * @param tipoOperacion Parámetro de tipo de operación (ej: "DEPOSITO", "Todos", etc)
     * @return TipoOperacion enum o null si es "Todos" o vacío
     * @throws OperacionInvalidaException si el valor no es válido
     */
    public TipoOperacion validarYObtenerTipoOperacion(String tipoOperacion) {
        if (tipoOperacion == null || tipoOperacion.trim().isEmpty()) {
            return null;
        }

        if ("Todos".equalsIgnoreCase(tipoOperacion.trim())) {
            return null; // Sin filtro de tipo
        }

        try {
            return TipoOperacion.valueOf(tipoOperacion.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException(
                    String.format("Tipo de operación no válido: %s. Valores permitidos: DEPOSITO, RETIRO, PAGO_SERVICIO, Todos",
                            tipoOperacion)
            );
        }
    }

    /**
     * Valida y obtiene el estado de operación desde el parámetro.
     *
     * @param estadoOperacion Parámetro de estado (ej: "COMPLETADA", "PENDIENTE", etc)
     * @return EstadoOperacion enum
     * @throws OperacionInvalidaException si el valor no es válido
     */
    public EstadoOperacion validarYObtenerEstadoOperacion(String estadoOperacion) {
        if (estadoOperacion == null || estadoOperacion.trim().isEmpty()) {
            throw new OperacionInvalidaException("El estado de operación no puede estar vacío");
        }

        try {
            return EstadoOperacion.valueOf(estadoOperacion.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException(
                    String.format("Estado de operación no válido: %s. Valores permitidos: PENDIENTE, COMPLETADA, ANULADA, FALLIDA",
                            estadoOperacion)
            );
        }
    }

    /**
     * Valida y obtiene la fecha de inicio desde el parámetro.
     * Formato esperado: yyyy-MM-ddTHH:mm:ss (ISO 8601)
     *
     * @param finicio Parámetro de fecha inicial
     * @return LocalDateTime o null si es vacío/nulo
     * @throws OperacionInvalidaException si el formato es inválido
     */
    public LocalDateTime validarYObtenerFechaInicio(String finicio) {
        if (finicio == null || finicio.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(finicio.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new OperacionInvalidaException(
                    String.format("Formato de fecha inicial inválido: %s. Use formato ISO 8601: yyyy-MM-ddTHH:mm:ss (ej: 2026-09-21T00:00:00)",
                            finicio)
            );
        }
    }

    /**
     * Valida y obtiene la fecha de fin desde el parámetro.
     * Formato esperado: yyyy-MM-ddTHH:mm:ss (ISO 8601)
     *
     * @param ffin Parámetro de fecha fin
     * @return LocalDateTime o null si es vacío/nulo
     * @throws OperacionInvalidaException si el formato es inválido
     */
    public LocalDateTime validarYObtenerFechaFin(String ffin) {
        if (ffin == null || ffin.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(ffin.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new OperacionInvalidaException(
                    String.format("Formato de fecha final inválido: %s. Use formato ISO 8601: yyyy-MM-ddTHH:mm:ss (ej: 2026-09-21T23:59:59)",
                            ffin)
            );
        }
    }

    /**
     * Valida que el rango de fechas sea válido.
     * La fecha final no puede ser anterior a la fecha inicial.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @throws OperacionInvalidaException si el rango es inválido
     */
    public void validarRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new OperacionInvalidaException(
                    String.format("Rango de fechas inválido. Fecha final (%s) no puede ser anterior a fecha inicial (%s)",
                            fechaFin, fechaInicio)
            );
        }
    }

    /**
     * Valida el ID de la entidad bancaria.
     *
     * @param idEntidad ID de la entidad
     * @throws OperacionInvalidaException si el valor es inválido
     */
    public void validarEntidadId(Long idEntidad) {
        if (idEntidad != null && idEntidad < 0) {
            throw new OperacionInvalidaException(
                    String.format("El ID de entidad no puede ser negativo: %d", idEntidad)
            );
        }
    }
}

