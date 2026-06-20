package com.donaton.bff.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NecesidadesService {

    private final RestTemplate restTemplate;

    // Trae la URL base del microservicio (ej: http://localhost:8083)
    @Value("${donaton.ms.necesidades.url}")
    private String necesidadesUrl;

    public NecesidadesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Método auxiliar interno para garantizar que la ruta coincida con el @RequestMapping del microservicio nativo
    private String getEndpointUrl() {
        return necesidadesUrl + "/api/v1/necesidades";
    }

    @CircuitBreaker(name = "necesidades", fallbackMethod = "fallbackNecesidades")
    public Object obtenerNecesidades() {
        // CORREGIDO: Ahora llamará correctamente a http://localhost:8083/api/v1/necesidades
        return restTemplate.getForObject(getEndpointUrl(), List.class);
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
        // CORREGIDO: POST a http://localhost:8083/api/v1/necesidades
        return restTemplate.postForObject(getEndpointUrl(), body, Map.class);
    }

    @SuppressWarnings("unused")
    public Object fallbackCrearNecesidad(Map<String, Object> body, Throwable t) {
        return Map.of("error", "No se pudo registrar la necesidad. Intenta nuevamente.", "estado", "FALLBACK");
    }

    @CircuitBreaker(name = "necesidades", fallbackMethod = "fallbackAtender")
    public Object atenderNecesidad(Long id) {
        // CORREGIDO: PATCH a http://localhost:8083/api/v1/necesidades/{id}/atender
        String url = getEndpointUrl() + "/" + id + "/atender";
        restTemplate.patchForObject(url, null, Map.class);
        return Map.of("id", id, "estado", "ATENDIDA", "mensaje", "Sincronizado desde BFF");
    }

    @SuppressWarnings("unused")
    public Object fallbackAtender(Long id, Throwable t) {
        return Map.of("error", "No se pudo marcar como atendida.", "estado", "FALLBACK");
    }
}