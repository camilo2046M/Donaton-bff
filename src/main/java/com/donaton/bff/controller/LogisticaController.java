package com.donaton.bff.controller;


import com.donaton.bff.service.LogisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador BFF para el módulo de Logística.
 * Expone los endpoints que consume el frontend React y delega
 * al microservicio de logística vía LogisticaService.
 */
@RestController
@RequestMapping("/api/bff/logistica")
public class LogisticaController {

    private final LogisticaService logisticaService;

    public LogisticaController(LogisticaService logisticaService) {
        this.logisticaService = logisticaService;
    }

    /**
     * GET /api/bff/logistica/envios
     * El frontend llama a este endpoint para listar todos los envíos.
     * Si el MS de logística está caído, el Circuit Breaker retorna el fallback.
     */
    @GetMapping("/envios")
    public ResponseEntity<?> getEnvios() {
        return ResponseEntity.ok(logisticaService.obtenerEnvios());
    }

    /**
     * POST /api/bff/logistica/envios
     * Registra un nuevo envío. El body llega desde el PlanForm del frontend.
     * Body esperado: { "centroAcopioOrigen": "...", "destino": "...", "tipoTransporte": "..." }
     */
    @PostMapping("/envios")
    public ResponseEntity<?> crearEnvio(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(logisticaService.crearEnvio(body));
    }

    /**
     * PATCH /api/bff/logistica/envios/{id}/estado
     * Actualiza el estado de un envío existente.
     */
    @PatchMapping("/envios/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(logisticaService.actualizarEstado(id, body.get("estado")));
    }
}