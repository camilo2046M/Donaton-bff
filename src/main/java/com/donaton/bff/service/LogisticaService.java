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
        // Tu MS de logística usa /listar para obtener todos
        return restTemplate.getForObject(logisticaUrl + "/listar", List.class);
    }

    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackCrearEnvio")
    public Object crearEnvio(Map<String, Object> body) {
        return restTemplate.postForObject(logisticaUrl + "/procesar/medicamento", body, Map.class);
    }


    @CircuitBreaker(name = "logistica", fallbackMethod = "fallbackActualizarEstado")
    public Object actualizarEstado(Long id, String estado) {
        // Construimos la URL: .../envios/{id}/estado?nuevoEstado=VALOR
        String url = logisticaUrl + "/" + id + "/estado?nuevoEstado=" + estado;

        // PATCH en RestTemplate requiere un pequeño truco o usar postForObject si el MS lo permite,
        // pero aquí lo enviamos como un PATCH vacío ya que el dato va en la URL.
        restTemplate.patchForObject(url, null, Map.class);

        return Map.of("id", id, "estado", estado, "mensaje", "Sincronización enviada");
    }

    // --- MÉTODOS FALLBACK (Se mantienen igual para no afectar al frontend) ---

    public Object fallbackEnvios(Throwable t) {
        return List.of(Map.of("error", "Servicio logístico no disponible", "estado", "FALLBACK"));
    }

    public Object fallbackCrearEnvio(Map<String, Object> body, Throwable t) {
        return Map.of("error", "No se pudo registrar el envío.", "estado", "FALLBACK");
    }

    public Object fallbackActualizarEstado(Long id, String estado, Throwable t) {
        return Map.of("error", "No se pudo actualizar el estado.", "estado", "FALLBACK");
    }
}