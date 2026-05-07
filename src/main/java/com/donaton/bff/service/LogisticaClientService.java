package com.donaton.bff.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticaClientService {

    private final WebClient.Builder webClientBuilder;
    private static final String LOGISTICA_URL = "http://localhost:8082/api/v1/envios";

    @CircuitBreaker(name = "logisticaCB", fallbackMethod = "fallbackListarEnvios")
    public List<Map<String, Object>> listarEnvios() {
        log.info("Llamando al microservicio de logistica...");
        // Hacemos la petición GET al microservicio real de forma síncrona para simplificar
        return webClientBuilder.build()
                .get()
                .uri(LOGISTICA_URL)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }

    // El plan B: Si el ms-logistica falla, devolvemos una lista vacía o un mensaje cacheado
    public List<Map<String, Object>> fallbackListarEnvios(Throwable t) {
        log.error("El microservicio de logística falló. Ejecutando Fallback. Error: {}", t.getMessage());
        return Collections.singletonList(
                Map.of(
                        "error", "Servicio logístico temporalmente no disponible",
                        "estado", "FALLBACK"
                )
        );
    }
}