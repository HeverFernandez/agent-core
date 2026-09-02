package com.aitamh.agent.core.entidadfinanciera.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;

@Component
public class Util {

    private static final int MIN = 1000;
    private static final int MAX = 9999;

    private final SecureRandom random = new SecureRandom();

    public String generaCodigoEntidad(String prefijo, Predicate<String> existeCodigo) {

        String codigo;

        do {
            int numero = random.nextInt(MIN, MAX + 1);
            codigo = prefijo + numero;
        } while (existeCodigo.test(codigo));
        return codigo;
    }
}
