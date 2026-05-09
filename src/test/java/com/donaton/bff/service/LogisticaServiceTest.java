package com.donaton.bff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogisticaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LogisticaService logisticaService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(logisticaService, "logisticaUrl", "http://localhost:8082/api/envios");
    }

    @Test
    void cuandoObtenerEnvios_entoncesRetornaLista() {
        // Mock de la respuesta
        List<Map<String, Object>> mockRespuesta = List.of(Map.of("id", 1, "estado", "ENTREGADO"));

        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(mockRespuesta);
        Object resultado = logisticaService.obtenerEnvios();

        assertNotNull(resultado);
        assertEquals(mockRespuesta, resultado);
    }

    @Test
    void cuandoCrearEnvio_entoncesRetornaObjeto() {
        Map<String, Object> body = Map.of("item", "medicamento");
        List<Object> mockRespuesta = List.of(Map.of("mensaje", "Creado"));

        when(restTemplate.postForObject(anyString(), any(), eq(List.class)))
                .thenReturn(mockRespuesta);
        Object resultado = logisticaService.crearEnvio(body);

        assertNotNull(resultado);
        assertEquals(mockRespuesta, resultado);
    }

    @Test
    void cuandoFalla_entoncesEjecutaFallbackEnvios() {
        Throwable t = new RuntimeException("Error de red");
        Object resultado = logisticaService.fallbackEnvios(t);
        assertNotNull(resultado);
        assertTrue(resultado instanceof List);
        assertTrue(((List<?>) resultado).get(0).toString().contains("FALLBACK"));
    }
}