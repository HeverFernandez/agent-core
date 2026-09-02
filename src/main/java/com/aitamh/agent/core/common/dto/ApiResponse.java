package com.aitamh.agent.core.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta genérica para todas las operaciones HTTP de la API.
 * Proporciona un formato estándar con estado, mensaje y datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;
    private String timestamp;

    /**
     * Constructor para respuesta exitosa.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Constructor para respuesta con error.
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }
}

