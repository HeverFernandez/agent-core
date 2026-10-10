package com.aitamh.agent.core.operacion.validator;

import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.common.exception.OperacionInvalidaException;
import com.aitamh.agent.core.common.exception.SaldoInsuficienteException;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.entidadfinanciera.enums.EstadoEntidad;
import com.aitamh.agent.core.entidadfinanciera.enums.TipoEntidad;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validador para la solicitud de creación/actualización de operaciones.
 * Valida todos los datos de entrada del request.
 * Sigue el Single Responsibility Principle: responsable únicamente de validar requests.
 */
@Component
@RequiredArgsConstructor
public class OperacionRequestValidator {

    /**
     * Valida la solicitud completa de operación
     */
    public void validarSolicitud(OperacionRequest request) {
        if (request == null) {
            throw new BusinessException("La solicitud de operación no puede ser nula");
        }

        validarTipoOperacion(request.getTipo());
        validarMontoOperacion(request.getMonto());

        if (request.getComision() != null) {
            validarMontoComision(request.getComision());
        }

        if (request.getNumeroReferencia() != null && !request.getNumeroReferencia().trim().isEmpty()) {
            validarNumeroReferencia(request.getNumeroReferencia());
        }
    }

    /**
     * Valida el tipo de operación
     */
    public void validarTipoOperacion(String tipoOperacion) {
        if (tipoOperacion == null || tipoOperacion.trim().isEmpty()) {
            throw new OperacionInvalidaException("El tipo de operación no puede estar vacío");
        }

        TipoOperacion tipo = TipoOperacion.fromString(tipoOperacion.trim());
        if (tipo == null) {
            throw new OperacionInvalidaException(
                    String.format("Tipo de operación no válido: %s. Valores permitidos: DEPOSITO, RETIRO, PAGO_SERVICIO",
                            tipoOperacion)
            );
        }
    }

    /**
     * Valida el monto de la operación
     */
    public void validarMontoOperacion(BigDecimal monto) {
        if (monto == null) {
            throw new BusinessException("El monto de la operación no puede ser nulo");
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    String.format("El monto debe ser positivo. Monto ingresado: %.2f", monto)
            );
        }

        if (monto.scale() > 2) {
            throw new BusinessException("El monto no puede tener más de 2 decimales");
        }
    }

    /**
     * Valida el monto de la comisión
     */
    public void validarMontoComision(BigDecimal comision) {
        if (comision.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    String.format("La comisión no puede ser negativa. Comisión ingresada: %.2f", comision)
            );
        }

        if (comision.scale() > 2) {
            throw new BusinessException("La comisión no puede tener más de 2 decimales");
        }
    }

    /**
     * Valida el número de referencia
     */
    public void validarNumeroReferencia(String numeroReferencia) {
        if (numeroReferencia.trim().isEmpty()) {
            throw new OperacionInvalidaException("El número de referencia no puede estar vacío");
        }

        if (numeroReferencia.length() > 50) {
            throw new OperacionInvalidaException("El número de referencia no puede exceder 50 caracteres");
        }
    }

    /**
     * Valida la entidad bancaria
     */
    public void validarEntidadBancaria(Entidad entidad) {
        if (entidad == null) {
            throw new EntityNotFoundException("Entidad bancaria no encontrada");
        }

        if (entidad.getTipoEntidad() != TipoEntidad.BANCO) {
            throw new BusinessException(
                    String.format("La entidad '%s' no es un banco. Tipo: %s",
                            entidad.getDenominacion(), entidad.getTipoEntidad())
            );
        }

        if (!entidad.isActivo()) {
            throw new BusinessException(
                    String.format("El banco '%s' se encuentra inactivo", entidad.getDenominacion())
            );
        }

        if (entidad.getEstado() != EstadoEntidad.ACTIVA) {
            throw new BusinessException(
                    String.format("El banco '%s' no se encuentra habilitado. Estado: %s",
                            entidad.getDenominacion(), entidad.getEstado())
            );
        }
    }

    /**
     * Valida la entidad de servicio para operaciones de pago
     */
    public void validarEntidadServicio(Entidad entidad) {
        if (entidad == null) {
            throw new EntityNotFoundException("Entidad de servicio no encontrada");
        }

        if (entidad.getTipoEntidad() != TipoEntidad.SERVICIO) {
            throw new BusinessException(
                    String.format("La entidad '%s' no es un servicio. Tipo: %s",
                            entidad.getDenominacion(), entidad.getTipoEntidad())
            );
        }

        if (!entidad.isActivo()) {
            throw new BusinessException(
                    String.format("El servicio '%s' se encuentra inactivo", entidad.getDenominacion())
            );
        }
    }

    /**
     * Valida que el usuario exista y esté activo
     */
    public void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        if (!usuario.getActivo()) {
            throw new BusinessException(
                    String.format("El usuario '%s %s' se encuentra inactivo",
                            usuario.getNombre(), usuario.getApellido())
            );
        }
    }

    /**
     * Valida que exista saldo suficiente para retiros y pagos
     */
    public void validarSaldoSuficiente(BigDecimal montoDisponible, BigDecimal montoSolicitado, Long entidadId) {
        if (montoDisponible == null || montoSolicitado == null) {
            throw new BusinessException("Monto disponible o solicitado es nulo");
        }

        if (montoDisponible.compareTo(montoSolicitado) < 0) {
            throw new SaldoInsuficienteException(
                    entidadId,
                    montoDisponible,
                    montoSolicitado
            );
        }
    }
}


