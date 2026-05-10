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


    @Value("${donaton.ms.logistica.url:http://localhost:8082/api/envios}")
    private String logisticaUrl;

    public LogisticaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackEnvios")
    public Object obtenerEnvios() {

        return restTemplate.getForObject(logisticaUrl, List.class);
    }


    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackCrearEnvio")
    public Object crearEnvio(Map<String, Object> body) {

        String url = logisticaUrl + "/procesar/medicamento";
        return restTemplate.postForObject(url, body, List.class);
    }

    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackActualizarEstado")
    public Object actualizarEstado(Long id, String estado) {
        String url = logisticaUrl + "/" + id + "/estado?nuevoEstado=" + estado;
        restTemplate.patchForObject(url, null, Map.class);

        return Map.of("id", id, "estado", estado, "mensaje", "Sincronización enviada");
    }

    public Object fallbackEnvios(Throwable t) {
        return List.of(Map.of("error", "Servicio logístico no disponible (GET)", "estado", "FALLBACK"));
    }

    public Object fallbackCrearEnvio(Map<String, Object> body, Throwable t) {
        return Map.of("error", "No se pudo registrar el envío (POST).", "estado", "FALLBACK");
    }

    public Object fallbackActualizarEstado(Long id, String estado, Throwable t) {
        return Map.of("error", "No se pudo actualizar el estado (PATCH).", "estado", "FALLBACK");
    }
}