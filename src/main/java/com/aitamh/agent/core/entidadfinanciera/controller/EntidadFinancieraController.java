package com.aitamh.agent.core.entidadfinanciera.controller;

import com.aitamh.agent.core.common.dto.ApiResponse;
import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraRequest;
import com.aitamh.agent.core.entidadfinanciera.dto.EntidadFinancieraResponse;
import com.aitamh.agent.core.entidadfinanciera.service.EntidadFinancieraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para EntidadFinanciera.
 */
@Tag(name = "EntidadFinanciera", description = "API para gestionar Entidades Financieras")
@RestController
@RequestMapping("/api/entidades-financieras")
@RequiredArgsConstructor
public class EntidadFinancieraController {

    private final EntidadFinancieraService service;

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Operation(summary = "Crear una nueva EntidadFinanciera")
    @PostMapping
    public ResponseEntity<ApiResponse<EntidadFinancieraResponse>> create(
            @Valid @RequestBody EntidadFinancieraRequest request) {
        EntidadFinancieraResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "EntidadFinanciera creada exitosamente"));
    }

    @Operation(summary = "Obtener una EntidadFinanciera por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EntidadFinancieraResponse>> findById(@PathVariable Long id) {
        EntidadFinancieraResponse response = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "EntidadFinanciera obtenida"));
    }

    @Operation(summary = "Listar EntidadesFinancieras por tipo")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EntidadFinancieraResponse>>> findByTipo(
            @RequestParam(defaultValue = "TODOS") String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<EntidadFinancieraResponse> response = service.findByTipo(tipo, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de EntidadesFinancieras por tipo"));
    }

    @Operation(summary = "Obtener todas las EntidadesFinancieras activas")
    @GetMapping("/all/active")
    public ResponseEntity<ApiResponse<List<EntidadFinancieraResponse>>> getAllActive() {
        List<EntidadFinancieraResponse> response = service.getAllActive();
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de EntidadesFinancieras activas"));
    }

    @Operation(summary = "Actualizar una EntidadFinanciera")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EntidadFinancieraResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody EntidadFinancieraRequest request) {
        EntidadFinancieraResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "EntidadFinanciera actualizada"));
    }

    @Operation(summary = "Eliminar una EntidadFinanciera")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "EntidadFinanciera eliminada"));
    }
}

