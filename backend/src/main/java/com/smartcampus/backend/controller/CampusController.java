package com.smartcampus.backend.controller;

import com.smartcampus.backend.dto.CampusDTO;
import com.smartcampus.backend.service.CampusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campus")
@CrossOrigin(origins = "*") // à restreindre en prod
public class CampusController {

    private final CampusService campusService;

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 1 — Import / Admin
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /api/campus/import/local
     *
     * Importe le campus à partir du fichier campus-perimeter.json
     * (polygone unique précis, dessiné à la main).
     * Idempotent.
     */
    @PostMapping("/import/local")
    public ResponseEntity<CampusDTO> importCampusLocal() {
        log.info(">> Import campus depuis fichier local");
        CampusDTO dto = campusService.importCampusFromLocalFile();
        return ResponseEntity.ok(dto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GROUPE 2 — API Front (lecture seule)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/campus
     *
     * Retourne tous les campus enregistrés avec leurs polygones.
     * Utilisé par le front pour afficher les contours sur la carte.
     */
    @GetMapping
    public ResponseEntity<List<CampusDTO>> getAllCampus() {
        return ResponseEntity.ok(campusService.getAllCampus());
    }

    /**
     * GET /api/campus/{id}
     *
     * Retourne un campus précis avec son polygone complet.
     * Le front utilise polygonCoordinates pour dessiner sur Leaflet / Mapbox.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampusDTO> getCampusById(@PathVariable Long id) {
        return ResponseEntity.ok(campusService.getCampusById(id));
    }
}