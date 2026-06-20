package com.donaton.bff.controller;

import com.donaton.bff.service.LogisticaService;
import com.donaton.bff.service.NecesidadesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bff/dashboard")
@CrossOrigin(origins = "*") // Alineado con tus otras configuraciones
public class DashboardController {

    private final LogisticaService logisticaService;
    private final NecesidadesService necesidadesService;

    // Inyectamos los servicios que ya tienen la lógica y los fallbacks de Resilience4j
    public DashboardController(LogisticaService logisticaService, NecesidadesService necesidadesService) {
        this.logisticaService = logisticaService;
        this.necesidadesService = necesidadesService;
    }

    /**
     * Endpoint agregador: Único llamado desde el frontend
     * GET /api/bff/dashboard/resumen
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> getDashboardResumen() {

        // 1. Llamar al servicio de Logística (con CircuitBreaker)
        Object envios = logisticaService.obtenerEnvios();

        // 2. Llamar al servicio de Necesidades (con CircuitBreaker)
        Object necesidades = necesidadesService.obtenerNecesidades();

        // 3. Consolidar la respuesta
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("envios", envios);
        resumen.put("necesidades", necesidades);

        // 4. Retornar JSON único al frontend
        return ResponseEntity.ok(resumen);
    }
}