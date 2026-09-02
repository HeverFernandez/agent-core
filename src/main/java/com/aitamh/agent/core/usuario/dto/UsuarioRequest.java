package com.aitamh.agent.core.usuario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para crear o actualizar un Usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo debe ser válido")
    private String correoElectronico;

    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;

    @NotBlank(message = "El rol es requerido")
    private String rol;

    @NotNull(message = "El estado es requerido")
    private Boolean estado;
}

