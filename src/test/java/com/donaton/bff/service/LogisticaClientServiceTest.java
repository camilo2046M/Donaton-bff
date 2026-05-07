package com.donaton.bff.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LogisticaClientServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private LogisticaClientService logisticaClientService;

    @Test
    void testFallbackListarEnvios() {
        // Arrange
        RuntimeException excepcionSimulada = new RuntimeException("Conexión rechazada");

        // Act
        List<Map<String, Object>> resultadoFallback = logisticaClientService.fallbackListarEnvios(excepcionSimulada);

        // Assert
        assertNotNull(resultadoFallback);
        assertEquals(1, resultadoFallback.size());
        assertEquals("FALLBACK", resultadoFallback.get(0).get("estado"));
        assertEquals("Servicio logístico temporalmente no disponible", resultadoFallback.get(0).get("error"));
    }
}