package com.donaton.bff.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LogisticaService {

    private final RestTemplate restTemplate;

    // Lee la URL base desde application.yaml: http://localhost:8082/api/v1
    @Value("${donaton.ms.logistica.url}")
    private String logisticaUrl;

    public LogisticaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * GET http://localhost:8082/api/v1/envios
     * Corresponde a @GetMapping en EnvioController del MS
     */
    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackEnvios")
    public Object obtenerEnvios() {
        return restTemplate.getForObject(logisticaUrl + "/envios", List.class);
    }

    /**
     * POST http://localhost:8082/api/v1/envios
     * Corresponde a @PostMapping en EnvioController del MS
     */
    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackCrearEnvio")
    public Object crearEnvio(Map<String, Object> body) {
        return restTemplate.postForObject(logisticaUrl + "/envios", body, Map.class);
    }

    /**
     * PATCH http://localhost:8082/api/v1/envios/{id}/estado
     */
    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackActualizarEstado")
    public Object actualizarEstado(Long id, String estado) {
        String url = logisticaUrl + "/envios/" + id + "/estado";
        restTemplate.patchForObject(url, Map.of("estado", estado), Map.class);
        return Map.of("id", id, "estado", estado);
    }

    @SuppressWarnings("unused")
    public Object fallbackEnvios(Throwable t) {
        return List.of(Map.of(
                "error", "Servicio logístico temporalmente no disponible",
                "estado", "FALLBACK"
        ));
    }

    @SuppressWarnings("unused")
    public Object fallbackCrearEnvio(Map<String, Object> body, Throwable t) {
        return Map.of("error", "No se pudo registrar el envío. Intenta nuevamente.", "estado", "FALLBACK");
    }

    @SuppressWarnings("unused")
    public Object fallbackActualizarEstado(Long id, String estado, Throwable t) {
        return Map.of("error", "No se pudo actualizar el estado.", "estado", "FALLBACK");
    }
}