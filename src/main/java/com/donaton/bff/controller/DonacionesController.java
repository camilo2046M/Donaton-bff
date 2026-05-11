package com.donaton.bff.controller;

import com.donaton.bff.service.DonacionesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bff/donaciones")
public class DonacionesController {

    private final DonacionesService donacionesService;

    public DonacionesController(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    /** GET /api/bff/donaciones */
    @GetMapping
    public ResponseEntity<?> listarDonaciones() {
        return ResponseEntity.ok(donacionesService.listarDonaciones());
    }

    /** POST /api/bff/donaciones */
    @PostMapping
    public ResponseEntity<?> crearDonacion(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(donacionesService.crearDonacion(body));
    }

    /** PATCH /api/bff/donaciones/{id}/completar */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<?> completar(@PathVariable Long id) {
        return ResponseEntity.ok(donacionesService.completarDonacion(id));
    }
}