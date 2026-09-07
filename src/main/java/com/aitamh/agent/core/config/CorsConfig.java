package com.aitamh.agent.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing) para permitir
 * que proyectos Frontend (como Angular en puerto 4200) accedan a los endpoints
 * de esta API sin restricciones de origen.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configura CORS a nivel de todos los endpoints de la aplicación.
     * Permite que Angular en http://localhost:4200 acceda a todos los recursos.
     *
     * @param registry - registro de configuración CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Orígenes permitidos
                .allowedOrigins(
                        "http://localhost:4200",      // Angular local (desarrollo)
                        "http://127.0.0.1:4200",      // Variante alternativa
                        "http://localhost:3000",      // Otros posibles puertos de desarrollo
                        "http://localhost:5173",       // Vite (si usas Vite en lugar de Angular CLI)
                        "https://heverfernandez.github.io"
                )
                // Métodos HTTP permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD")
                // Headers permitidos en la solicitud
                .allowedHeaders("*")
                // Headers que el servidor puede devolver al cliente
                .exposedHeaders("Authorization", "Content-Type", "X-Total-Count", "X-Page-Number", "X-Page-Size")
                // Credenciales (cookies, autenticación) permitidas
                .allowCredentials(true)
                // Tiempo máximo que el navegador cachea la respuesta preflight (en segundos)
                .maxAge(3600);
    }

    /**
     * Bean alternativo: configura CORS a través de CorsConfigurationSource.
     * Útil si requieres una configuración más granular o si usas Spring Security.
     *
     * @return CorsConfigurationSource configurado
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",      // Angular local (desarrollo)
                "http://127.0.0.1:4200",      // Variante alternativa
                "http://localhost:3000",      // Otros posibles puertos de desarrollo
                "http://localhost:5173"       // Vite
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Headers permitidos en la solicitud
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // Headers que el servidor expone al cliente
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",        // Para paginación
                "X-Page-Number",        // Para paginación
                "X-Page-Size"           // Para paginación
        ));

        // Permitir credenciales (cookies, autenticación)
        configuration.setAllowCredentials(true);

        // Tiempo máximo de cacheo de la respuesta preflight (en segundos)
        configuration.setMaxAge(3600L);

        // Aplicar configuración a todas las rutas de la API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

