package com.aitamh.agent.core.reporte.service;

public interface ReporteService {

    byte[] exportarOperacionesExcel(
            String tipoOperacion,
            String estadoOperacion,
            Long entidad,
            String finicio,
            String ffin
    );

    byte[] exportarOperacionesPdf(
            String tipoOperacion,
            String estadoOperacion,
            Long entidad,
            String finicio,
            String ffin
    );
}
