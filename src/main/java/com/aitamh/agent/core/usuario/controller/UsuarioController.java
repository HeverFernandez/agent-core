package com.aitamh.agent.core.usuario.controller;

import com.aitamh.agent.core.common.dto.ApiResponse;
import com.aitamh.agent.core.common.dto.PageResponse;
import com.aitamh.agent.core.usuario.dto.UsuarioRequest;
import com.aitamh.agent.core.usuario.dto.UsuarioResponse;
import com.aitamh.agent.core.usuario.service.UsuarioService;
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
 * Controlador REST para Usuario.
 */
@Tag(name = "Usuario", description = "API para gestionar Usuarios")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Operation(summary = "Crear un nuevo Usuario")
    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> create(
            @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Usuario creado exitosamente"));
    }

    @Operation(summary = "Obtener un Usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> findById(@PathVariable Long id) {
        UsuarioResponse response = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario obtenido"));
    }

    @Operation(summary = "Obtener un Usuario por correo electrónico")
    @GetMapping("/correo/{correoElectronico}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> findByCorreo(@PathVariable String correoElectronico) {
        UsuarioResponse response = service.findByCorreo(correoElectronico);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario obtenido"));
    }

    @Operation(summary = "Listar todos los Usuarios")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UsuarioResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<UsuarioResponse> response = service.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Usuarios"));
    }

    @Operation(summary = "Listar Usuarios por rol")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioResponse>>> findByRol(
            @PathVariable String rol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<UsuarioResponse> response = service.findByRol(rol, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Usuarios por rol"));
    }

    @Operation(summary = "Listar Usuarios por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioResponse>>> findByEstado(
            @PathVariable Boolean estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE + "") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<UsuarioResponse> response = service.findByEstado(estado, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado de Usuarios por estado"));
    }

    @Operation(summary = "Obtener Usuarios activos por rol")
    @GetMapping("/rol/{rol}/activos")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> findByRolAndActivos(@PathVariable String rol) {
        List<UsuarioResponse> response = service.findByRolAndActivos(rol);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuarios activos obtenidos"));
    }

    @Operation(summary = "Actualizar un Usuario")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario actualizado"));
    }

    @Operation(summary = "Eliminar un Usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado"));
    }
}

