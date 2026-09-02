package com.aitamh.agent.core.saldo.controller;

import com.aitamh.agent.core.common.dto.ApiResponse;
import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.saldo.dto.SaldoRequest;
import com.aitamh.agent.core.saldo.dto.SaldoResponse;
import com.aitamh.agent.core.saldo.service.SaldoService;
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
 * Controlador REST para Saldo.
 */
@Tag(name = "Saldo", description = "API para gestionar Saldos")
@RestController
@RequestMapping("/api/saldos")
@RequiredArgsConstructor
public class SaldoController {

    private final SaldoService service;

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Operation(summary = "Crear un nuevo Saldo")
    @PostMapping
    public ResponseEntity<ApiResponse<SaldoResponse>> create(
            @Valid @RequestBody SaldoRequest request) {
        SaldoResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Saldo creado exitosamente"));
    }

    @Operation(summary = "Obtener un Saldo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaldoResponse>> findById(@PathVariable Long id) {
        SaldoResponse response = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Saldo obtenido"));
    }

    @Operation(summary = "Listar todos los Saldos")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SaldoResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<SaldoResponse> response = service.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Saldos"));
    }

    @Operation(summary = "Listar Saldos por Entidad Financiera")
    @GetMapping("/entidad/{entidadFinancieraId}")
    public ResponseEntity<ApiResponse<PageResponse<SaldoResponse>>> findByEntidadFinanciera(
            @PathVariable Long entidadFinancieraId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<SaldoResponse> response = service.findByEntidadFinanciera(entidadFinancieraId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Saldos por Entidad"));
    }

    @Operation(summary = "Listar Saldos por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<PageResponse<SaldoResponse>>> findByEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<SaldoResponse> response = service.findByEstado(estado, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Saldos por estado"));
    }

    @Operation(summary = "Obtener Saldos por Entidad Financiera y estado")
    @GetMapping("/entidad/{entidadFinancieraId}/estado/{estado}")
    public ResponseEntity<ApiResponse<List<SaldoResponse>>> findByEntidadFinancieraAndEstado(
            @PathVariable Long entidadFinancieraId,
            @PathVariable String estado) {
        List<SaldoResponse> response = service.findByEntidadFinancieraAndEstado(entidadFinancieraId, estado);
        return ResponseEntity.ok(ApiResponse.success(response, "Saldos obtenidos"));
    }

    @Operation(summary = "Actualizar un Saldo")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SaldoResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SaldoRequest request) {
        SaldoResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Saldo actualizado"));
    }

    @Operation(summary = "Eliminar un Saldo")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Saldo eliminado"));
    }
}

