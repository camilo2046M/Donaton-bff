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

    @Value("${donaton.ms.logistica.url:http://localhost:8082}")
    private String logisticaUrl;

    public LogisticaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Usamos un método auxiliar como en NecesidadesService para mantener el estándar
    private String getEndpointUrl() {
        return logisticaUrl + "/api/v1/envios";
    }

    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackEnvios")
    public Object obtenerEnvios() {
        return restTemplate.getForObject(getEndpointUrl(), List.class);
    }

    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackCrearEnvio")
    public Object crearEnvio(Map<String, Object> body) {
        // Obtenemos el tipo desde el body, asumiendo que el controlador de logística espera un {tipo}
        // Ajusta esto si el body no tiene la llave "tipo" o si la ruta es diferente.
        String url = getEndpointUrl();
        return restTemplate.postForObject(url, body, List.class);
    }

    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackActualizarEstado")
    public Object actualizarEstado(Long id, String estado) {
        String url = getEndpointUrl() + "/" + id + "/estado?nuevoEstado=" + estado;
        restTemplate.patchForObject(url, null, Map.class);
        return Map.of("id", id, "estado", estado, "mensaje", "Sincronización enviada");
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