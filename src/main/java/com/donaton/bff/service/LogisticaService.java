package com.donaton.bff.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio que actúa como cliente HTTP hacia el MS de Logística.
 * El Circuit Breaker "logistica" está configurado en application.properties.
 * Si el MS cae, se activa el método fallback correspondiente.
 */
@Service
public class LogisticaService {

    private final RestTemplate restTemplate;

    @Value("${donaton.ms.logistica.url}")
    private String logisticaUrl;

    public LogisticaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtiene todos los envíos desde el MS de Logística.
     * Fallback: retorna el JSON de contingencia que espera el frontend.
     */
    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackEnvios")
    public Object obtenerEnvios() {
        return restTemplate.getForObject(logisticaUrl + "/envios", List.class);
    }

    @SuppressWarnings("unused")
    public Object fallbackEnvios(Throwable t) {
        return List.of(Map.of(
                "error", "Servicio logístico temporalmente no disponible",
                "estado", "FALLBACK"
        ));
    }

    /**
     * Crea un nuevo envío en el MS de Logística.
     */
    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackCrearEnvio")
    public Object crearEnvio(Map<String, Object> body) {
        return restTemplate.postForObject(logisticaUrl + "/envios", body, Map.class);
    }

    @SuppressWarnings("unused")
    public Object fallbackCrearEnvio(Map<String, Object> body, Throwable t) {
        return Map.of(
                "error", "No se pudo registrar el envío. Intenta nuevamente.",
                "estado", "FALLBACK"
        );
    }

    /**
     * Actualiza el estado de un envío en el MS de Logística.
     */
    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackActualizarEstado")
    public Object actualizarEstado(Long id, String estado) {
        String url = logisticaUrl + "/envios/" + id + "/estado";
        restTemplate.patchForObject(url, Map.of("estado", estado), Map.class);
        return Map.of("id", id, "estado", estado);
    }

    @SuppressWarnings("unused")
    public Object fallbackActualizarEstado(Long id, String estado, Throwable t) {
        return Map.of("error", "No se pudo actualizar el estado.", "estado", "FALLBACK");
    }
}