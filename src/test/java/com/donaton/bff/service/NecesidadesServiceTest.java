package com.donaton.bff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NecesidadesServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NecesidadesService necesidadesService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(necesidadesService, "necesidadesUrl", "http://localhost:8083/api");
    }

    @Test
    void cuandoObtenerNecesidades_entoncesRetornaLista() {
        List<Map<String, Object>> mockRespuesta = List.of(Map.of("id", 1, "descripcion", "Ropa"));

        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(mockRespuesta);

        Object resultado = necesidadesService.obtenerNecesidades();

        assertNotNull(resultado);
        assertEquals(mockRespuesta, resultado);
    }

    @Test
    void cuandoCrearNecesidad_entoncesRetornaMapa() {
        Map<String, Object> body = Map.of("descripcion", "Comida");
        Map<String, Object> mockRespuesta = Map.of("id", 2, "estado", "PENDIENTE");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(mockRespuesta);

        Object resultado = necesidadesService.crearNecesidad(body);

        assertNotNull(resultado);
        assertEquals(mockRespuesta, resultado);
    }

    @Test
    void cuandoAtenderNecesidad_entoncesRetornaExito() {
        Map<String, Object> mockRespuesta = Map.of("id", 1L, "estado", "ATENDIDA");

        when(restTemplate.patchForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(mockRespuesta);
        Object resultado = necesidadesService.atenderNecesidad(1L);
        assertNotNull(resultado);
        assertTrue(resultado.toString().contains("ATENDIDA"));
    }

    @Test
    void verificarFallbacks() {
        Throwable t = new RuntimeException("Error");
        assertNotNull(necesidadesService.fallbackNecesidades(t));
        assertNotNull(necesidadesService.fallbackCrearNecesidad(Map.of(), t));
        assertNotNull(necesidadesService.fallbackAtender(1L, t));
    }
}