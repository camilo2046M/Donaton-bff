package com.donaton.bff.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio que actúa como cliente HTTP hacia el MS de Necesidades.
 */
@Service
public class NecesidadesService {

    private final RestTemplate restTemplate;

    @Value("${donaton.ms.necesidades.url}")
    private String necesidadesUrl;

    public NecesidadesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "necesidades", fallbackMethod = "fallbackNecesidades")
    public Object obtenerNecesidades() {
        return restTemplate.getForObject(necesidadesUrl + "/necesidades", List.class);
    }

    @SuppressWarnings("unused")
    public Object fallbackNecesidades(Throwable t) {
        return List.of(Map.of(
                "error", "Servicio de necesidades temporalmente no disponible",
                "estado", "FALLBACK"
        ));
    }

    @CircuitBreaker(name = "necesidades", fallbackMethod = "fallbackCrearNecesidad")
    public Object crearNecesidad(Map<String, Object> body) {
        return restTemplate.postForObject(necesidadesUrl + "/necesidades", body, Map.class);
    }

    @SuppressWarnings("unused")
    public Object fallbackCrearNecesidad(Map<String, Object> body, Throwable t) {
        return Map.of("error", "No se pudo registrar la necesidad. Intenta nuevamente.", "estado", "FALLBACK");
    }

    @CircuitBreaker(name = "necesidades", fallbackMethod = "fallbackAtender")
    public Object atenderNecesidad(Long id) {
        String url = necesidadesUrl + "/necesidades/" + id + "/atender";
        restTemplate.patchForObject(url, null, Map.class);
        return Map.of("id", id, "estado", "ATENDIDA");
    }

    @SuppressWarnings("unused")
    public Object fallbackAtender(Long id, Throwable t) {
        return Map.of("error", "No se pudo marcar como atendida.", "estado", "FALLBACK");
    }
}
