package com.aitamh.agent.core.reporte.controller;

import com.aitamh.agent.core.reporte.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador REST para Reporte de Operaciones.
 *
 * Genera reportes de operaciones en formato Excel y PDF reutilizando
 */
@Tag(name = "Reporte", description = "API para generar reportes de operaciones")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    /**
     * Exportar operaciones en formato Excel o PDF.
     *
     * @param formato excel o pdf (case-insensitive)
     * @param tipo default "Todos" (DEPOSITO, RETIRO, PAGO_SERVICIO, Todos)
     * @param estado default "COMPLETADA" (PENDIENTE, COMPLETADA, ANULADA, FALLIDA)
     * @param entidad ID de la entidad (optional)
     * @param finicio fecha inicio ISO 8601 (optional)
     * @param ffin fecha fin ISO 8601 (optional)
     * @return archivo binario descargable con nombre dinámico
     */
    @Operation(summary = "Exportar operaciones filtradas en Excel o PDF")
    @GetMapping("/operaciones/{formato}")
    public ResponseEntity<byte[]> exportarOperaciones(
            @PathVariable String formato,
            @RequestParam(defaultValue = "Todos") String tipo,
            @RequestParam(defaultValue = "COMPLETADA") String estado,
            @RequestParam(required = false) Long entidad,
            @RequestParam(required = false) String finicio,
            @RequestParam(required = false) String ffin) {

        if (!formato.equalsIgnoreCase("excel") && !formato.equalsIgnoreCase("pdf")) {
            return ResponseEntity.badRequest().build();
        }

        byte[] contenido;
        String contentType;
        String nombreArchivo;
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = ahora.format(formatter);

        if (formato.equalsIgnoreCase("excel")) {
            contenido = service.exportarOperacionesExcel(
                    tipo, estado, entidad, finicio, ffin);
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            nombreArchivo = "operaciones_" + timestamp + ".xlsx";
        } else {
            contenido = service.exportarOperacionesPdf(
                    tipo, estado, entidad, finicio, ffin);
            contentType = "application/pdf";
            nombreArchivo = "operaciones_" + timestamp + ".pdf";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(contenido);
    }
}
