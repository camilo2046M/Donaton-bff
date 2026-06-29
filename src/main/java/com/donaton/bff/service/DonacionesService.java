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

    @Value("${donaton.ms.donaciones.url:http://localhost:8081}")
    private String donacionesUrl;

    public DonacionesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getEndpointUrl() {
        return donacionesUrl + "/api/v1/donaciones";
    }

    @CircuitBreaker(name = "donacionesCB", fallbackMethod = "fallbackListar")
    public Object listarDonaciones() {
        return restTemplate.getForObject(getEndpointUrl() + "/listar", List.class); // Apuntando al /listar de tu controlador
    }

    @CircuitBreaker(name = "donacionesCB", fallbackMethod = "fallbackCrear")
    public Object crearDonacion(Map<String, Object> body) {
        // Tu controlador usa request params, no un RequestBody.
        // Esto requiere una adaptación para enviar los datos por URL o cambiar el controlador.
        // Por ahora, asumimos que el body se envía como parámetros o que el controlador se adaptó a @RequestBody
        // Si el controlador sigue usando @RequestParam, este postForObject fallará porque Spring espera un JSON.
        return restTemplate.postForObject(getEndpointUrl() + "/crear", body, Map.class);
    }

    @CircuitBreaker(name = "donacionesCB", fallbackMethod = "fallbackCompletar")
    public Object completarDonacion(Long id) {
        String url = getEndpointUrl() + "/" + id + "/completar";
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