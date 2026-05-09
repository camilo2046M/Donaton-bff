package com.donaton.bff.controller;

import com.donaton.bff.service.NecesidadesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador BFF para el módulo de Necesidades.
 * Expone los endpoints que consume el frontend React y delega
 * al microservicio de necesidades vía NecesidadesService.
 */
@RestController
@RequestMapping("/api/bff/necesidades")
public class NecesidadesController {

    private final NecesidadesService necesidadesService;

    public NecesidadesController(NecesidadesService necesidadesService) {
        this.necesidadesService = necesidadesService;
    }

    /**
     * GET /api/bff/necesidades
     * Lista todas las necesidades reportadas (panel admin).
     */
    @GetMapping
    public ResponseEntity<?> getNecesidades() {
        return ResponseEntity.ok(necesidadesService.obtenerNecesidades());
    }

    /**
     * POST /api/bff/necesidades
     * Registra una nueva necesidad ciudadana.
     * Body esperado: { "recursoNecesitado": "...", "cantidad": 100,
     *                  "unidad": "...", "ubicacionGeografica": "..." }
     */
    @PostMapping
    public ResponseEntity<?> crearNecesidad(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(necesidadesService.crearNecesidad(body));
    }

    /**
     * PATCH /api/bff/necesidades/{id}/atender
     * Marca una necesidad como ATENDIDA.
     */
    @PatchMapping("/{id}/atender")
    public ResponseEntity<?> atenderNecesidad(@PathVariable Long id) {
        return ResponseEntity.ok(necesidadesService.atenderNecesidad(id));
    }
}