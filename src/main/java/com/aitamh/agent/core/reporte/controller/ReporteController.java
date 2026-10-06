package com.aitamh.agent.core.reporte.controller;

import com.aitamh.agent.core.reporte.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para Reporte.
 */
@Tag(name = "Reporte", description = "API para gestionar Reportes")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @Operation(summary = "Exportar operaciones en Excel con filtros")
    @GetMapping("/operaciones/excel")
    public ResponseEntity<byte[]> exportarOperacionesExcel(
            @RequestParam(defaultValue = "Todos") String tipo,
            @RequestParam(defaultValue = "COMPLETADA") String estado,
            @RequestParam(required = false) Long entidad,
            @RequestParam(required = false) String finicio,
            @RequestParam(required = false) String ffin) {

        byte[] content = service.exportarOperacionesExcel(tipo, estado, entidad, finicio, ffin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "operaciones.xlsx");

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Operation(summary = "Exportar operaciones en PDF con filtros")
    @GetMapping("/operaciones/pdf")
    public ResponseEntity<byte[]> exportarOperacionesPdf(
            @RequestParam(defaultValue = "Todos") String tipoOperacion,
            @RequestParam(defaultValue = "COMPLETADA") String estadoOperacion,
            @RequestParam(required = false) Long entidad,
            @RequestParam(required = false) String finicio,
            @RequestParam(required = false) String ffin) {

        byte[] content = service.exportarOperacionesPdf(tipoOperacion, estadoOperacion, entidad, finicio, ffin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "operaciones.pdf");

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
