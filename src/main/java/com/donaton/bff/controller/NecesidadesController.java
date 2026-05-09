package com.donaton.bff.controller;

import com.donaton.bff.service.NecesidadesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/bff/necesidades")
public class NecesidadesController {

    private final NecesidadesService necesidadesService;

    public NecesidadesController(NecesidadesService necesidadesService) {
        this.necesidadesService = necesidadesService;
    }


    @GetMapping
    public ResponseEntity<?> getNecesidades() {
        return ResponseEntity.ok(necesidadesService.obtenerNecesidades());
    }


    @PostMapping
    public ResponseEntity<?> crearNecesidad(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(necesidadesService.crearNecesidad(body));
    }


    @PatchMapping("/{id}/atender")
    public ResponseEntity<?> atenderNecesidad(@PathVariable Long id) {
        return ResponseEntity.ok(necesidadesService.atenderNecesidad(id));
    }
}