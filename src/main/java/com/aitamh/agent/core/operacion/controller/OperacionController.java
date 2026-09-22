package com.aitamh.agent.core.operacion.controller;

import com.aitamh.agent.core.common.dto.ApiResponse;
import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.operacion.dto.OperacionRequest;
import com.aitamh.agent.core.operacion.dto.OperacionResponse;
import com.aitamh.agent.core.operacion.service.OperacionService;
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

/**
 * Controlador REST para Operación.
 */
@Tag(name = "Operacion", description = "API para gestionar Operaciones")
@RestController
@RequestMapping("/api/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService service;

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Operation(summary = "Crear una nueva Operación")
    @PostMapping
    public ResponseEntity<ApiResponse<OperacionResponse>> create(
            @Valid @RequestBody OperacionRequest request) {
        OperacionResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Operación registrada exitosamente"));
    }

    @Operation(summary = "Obtener una Operación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OperacionResponse>> findById(@PathVariable Long id) {
        OperacionResponse response = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Operación obtenida"));
    }

    @Operation(summary = "Listar Operaciones")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OperacionResponse>>> findAll(
            @RequestParam(defaultValue = "Todos") String tipoOperacion,
            @RequestParam(defaultValue = "COMPLETADA") String estadoOperacion,
            @RequestParam(required = false) Long entidad,
            @RequestParam(required = false) String finicio,
            @RequestParam(required = false) String ffin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<OperacionResponse> response = service.findAll(tipoOperacion, estadoOperacion, entidad, finicio, ffin, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Operaciones"));
    }

    @Operation(summary = "Listar Operaciones por Usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<PageResponse<OperacionResponse>>> findByUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<OperacionResponse> response = service.findByUsuario(usuarioId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Operaciones por Usuario"));
    }

    @Operation(summary = "Listar Operaciones por tipo y Entidad Financiera")
    @GetMapping("/filtro")
    public ResponseEntity<ApiResponse<PageResponse<OperacionResponse>>> findByTipoAndEntidad(
            @RequestParam String tipoOperacion,
            @RequestParam Long idEntidadFinanciera,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<OperacionResponse> response = service.findByTipoAndEntidad(tipoOperacion, idEntidadFinanciera, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Operaciones filtrado"));
    }

    @Operation(summary = "Actualizar una Operación")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OperacionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody OperacionRequest request) {
        OperacionResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Operación actualizada"));
    }

    @Operation(summary = "Eliminar una Operación")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Operación eliminada"));
    }
}

