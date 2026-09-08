package com.aitamh.agent.core.entidadfinanciera.utils;

import static com.aitamh.agent.core.entidadfinanciera.constants.EntidadFinancieraConstants.TIPO_BANCO;
import static com.aitamh.agent.core.entidadfinanciera.constants.EntidadFinancieraConstants.TIPO_SERVICIO;

import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraRequest;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;


@Component
public class Util {

    private static final int MIN = 1000;
    private static final int MAX = 9999;

    private final SecureRandom random = new SecureRandom();

    public String generaCodigoEntidad(EntidadFinancieraRequest request, Predicate<String> existeCodigo) {

        String codEntidad;
        if(request.getTipoEntidad().equalsIgnoreCase(TIPO_BANCO)) {
            codEntidad = "BANK";
        } else if(request.getTipoEntidad().equalsIgnoreCase(TIPO_SERVICIO)) {
            codEntidad = "SERV";
        } else {
            throw new BusinessException("Tipo de entidad no válido");
        }

        String codigo;

        do {
            int numero = random.nextInt(MIN, MAX + 1);
            codigo = codEntidad + numero;
        } while (existeCodigo.test(codigo));
        return codigo;
    }
}
