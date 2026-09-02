//package com.aitamh.agent.core.reporte.controller;
//
//import com.aitamh.agent.core.common.dto.ApiResponse;
//import com.aitamh.agent.core.common.dto.PageResponse;
//import com.aitamh.agent.core.reporte.dto.ReporteRequest;
//import com.aitamh.agent.core.reporte.dto.ReporteResponse;
//import com.aitamh.agent.core.reporte.service.ReporteService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
///**
// * Controlador REST para Reporte.
// */
//@Tag(name = "Reporte", description = "API para gestionar Reportes")
//@RestController
//@RequestMapping("/api/reportes")
//@RequiredArgsConstructor
//public class ReporteController {
//
//    private final ReporteService service;
//
//    private static final int MAX_PAGE_SIZE = 100;
//    private static final int DEFAULT_PAGE_SIZE = 20;
//
//    @Operation(summary = "Crear un nuevo Reporte")
//    @PostMapping
//    public ResponseEntity<ApiResponse<ReporteResponse>> create(
//            @Valid @RequestBody ReporteRequest request) {
//        ReporteResponse response = service.create(request);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success(response, "Reporte creado exitosamente"));
//    }
//
//    @Operation(summary = "Obtener un Reporte por ID")
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<ReporteResponse>> findById(@PathVariable Long id) {
//        ReporteResponse response = service.findById(id);
//        return ResponseEntity.ok(ApiResponse.success(response, "Reporte obtenido"));
//    }
//
//    @Operation(summary = "Listar todos los Reportes")
//    @GetMapping
//    public ResponseEntity<ApiResponse<PageResponse<ReporteResponse>>> findAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
//
//        size = Math.min(size, MAX_PAGE_SIZE);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        PageResponse<ReporteResponse> response = service.findAll(pageable);
//        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Reportes"));
//    }
//
//    @Operation(summary = "Listar Reportes por tipo")
//    @GetMapping("/tipo/{tipoReporte}")
//    public ResponseEntity<ApiResponse<PageResponse<ReporteResponse>>> findByTipo(
//            @PathVariable String tipoReporte,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
//
//        size = Math.min(size, MAX_PAGE_SIZE);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        PageResponse<ReporteResponse> response = service.findByTipo(tipoReporte, pageable);
//        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Reportes por tipo"));
//    }
//
//    @Operation(summary = "Listar Reportes por Usuario")
//    @GetMapping("/usuario/{usuarioId}")
//    public ResponseEntity<ApiResponse<PageResponse<ReporteResponse>>> findByUsuario(
//            @PathVariable Long usuarioId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
//
//        size = Math.min(size, MAX_PAGE_SIZE);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        PageResponse<ReporteResponse> response = service.findByUsuario(usuarioId, pageable);
//        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Reportes por Usuario"));
//    }
//
//    @Operation(summary = "Listar Reportes por rango de fechas")
//    @GetMapping("/fecha-range")
//    public ResponseEntity<ApiResponse<PageResponse<ReporteResponse>>> findByFechaBetween(
//            @RequestParam String inicio,
//            @RequestParam String fin,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//        LocalDateTime fechaInicio = LocalDateTime.parse(inicio, formatter);
//        LocalDateTime fechaFin = LocalDateTime.parse(fin, formatter);
//
//        size = Math.min(size, MAX_PAGE_SIZE);
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        PageResponse<ReporteResponse> response = service.findByFechaBetween(fechaInicio, fechaFin, pageable);
//        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Reportes por rango de fechas"));
//    }
//
//    @Operation(summary = "Actualizar un Reporte")
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<ReporteResponse>> update(
//            @PathVariable Long id,
//            @Valid @RequestBody ReporteRequest request) {
//        ReporteResponse response = service.update(id, request);
//        return ResponseEntity.ok(ApiResponse.success(response, "Reporte actualizado"));
//    }
//
//    @Operation(summary = "Eliminar un Reporte")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
//        service.delete(id);
//        return ResponseEntity.ok(ApiResponse.success(null, "Reporte eliminado"));
//    }
//}
//
