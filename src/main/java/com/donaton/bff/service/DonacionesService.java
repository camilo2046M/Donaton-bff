package com.donaton.bff.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DonacionesService {

    private final RestTemplate restTemplate;

    @Value("${donaton.ms.donaciones.url}")
    private String donacionesUrl;

    public DonacionesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "donacionesCB", fallbackMethod = "fallbackListar")
    public Object listarDonaciones() {
        return restTemplate.getForObject(donacionesUrl + "/donaciones", List.class);
    }

    @CircuitBreaker(name = "donacionesCB", fallbackMethod = "fallbackCrear")
    public Object crearDonacion(Map<String, Object> body) {
        return restTemplate.postForObject(donacionesUrl + "/donaciones", body, Map.class);
    }

    @CircuitBreaker(name = "donacionesCB", fallbackMethod = "fallbackCompletar")
    public Object completarDonacion(Long id) {
        String url = donacionesUrl + "/donaciones/" + id + "/completar";
        restTemplate.patchForObject(url, null, Map.class);
        return Map.of("id", id, "estado", "COMPLETADA");
    }

    @SuppressWarnings("unused")
    public Object fallbackListar(Throwable t) {
        return List.of(Map.of(
                "error", "Servicio de donaciones temporalmente no disponible",
                "estado", "FALLBACK"
        ));
    }

    @SuppressWarnings("unused")
    public Object fallbackCrear(Map<String, Object> body, Throwable t) {
        return Map.of("error", "No se pudo registrar la donación.", "estado", "FALLBACK");
    }

    @SuppressWarnings("unused")
    public Object fallbackCompletar(Long id, Throwable t) {
        return Map.of("error", "No se pudo completar la donación.", "estado", "FALLBACK");
    }
}