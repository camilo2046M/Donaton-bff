package com.donaton.bff.controller;

import com.donaton.bff.service.LogisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bff/logistica")
@CrossOrigin(origins = "*")
public class LogisticaController {

    private final LogisticaService logisticaService;

    public LogisticaController(LogisticaService logisticaService) {
        this.logisticaService = logisticaService;
    }

    @GetMapping("/envios")
    public ResponseEntity<?> getEnvios() {
        Object respuesta = logisticaService.obtenerEnvios();
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/envios")
    public ResponseEntity<?> crearEnvio(@RequestBody Map<String, Object> body) {
        Object respuesta = logisticaService.crearEnvio(body);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/envios/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");
        Object respuesta = logisticaService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(respuesta);
    }
}