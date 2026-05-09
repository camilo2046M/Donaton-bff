package com.donaton.bff.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para el BFF de Donaton.
 * Permite que el frontend React (Vite en localhost:5173) consuma
 * los endpoints /api/bff/** sin que el browser bloquee las respuestas.
 *
 * Si en producción el frontend está en otro dominio, cambia
 * allowedOrigins() por la URL real (ej. "https://donaton.cl").
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173",   // Vite dev server
                        "http://localhost:3000"    // fallback Create React App
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}