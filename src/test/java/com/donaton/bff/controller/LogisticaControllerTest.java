package com.donaton.bff.controller;

import com.donaton.bff.service.LogisticaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogisticaController.class) // Solo carga la capa web
public class LogisticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogisticaService logisticaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cuandoGetEnvios_entoncesRetornaStatus200() throws Exception {
        when(logisticaService.obtenerEnvios()).thenReturn(List.of());

        mockMvc.perform(get("/api/bff/logistica/envios"))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoCrearEnvio_entoncesRetornaStatus200() throws Exception {
        Map<String, Object> body = Map.of("item", "medicamento");
        when(logisticaService.crearEnvio(anyMap())).thenReturn(Map.of("id", 1));

        mockMvc.perform(post("/api/bff/logistica/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoActualizarEstado_entoncesRetornaStatus200() throws Exception {
        Map<String, String> body = Map.of("estado", "EN_CAMINO");
        when(logisticaService.actualizarEstado(anyLong(), anyString()))
                .thenReturn(Map.of("mensaje", "Actualizado"));

        mockMvc.perform(patch("/api/bff/logistica/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }
}