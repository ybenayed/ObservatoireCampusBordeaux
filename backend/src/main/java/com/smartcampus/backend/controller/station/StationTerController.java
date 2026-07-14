package com.smartcampus.backend.controller.station;

import com.smartcampus.backend.dto.station.PassageTerDTO;
import com.smartcampus.backend.dto.station.StationTerDTO;
import com.smartcampus.backend.dto.station.StationPositionTerDTO;
import com.smartcampus.backend.service.station.PassageTerService;
import com.smartcampus.backend.service.station.StationTerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stationTer")
@CrossOrigin(origins = "*")
public class StationTerController {

    private final StationTerService stationTerService;   // STATIQUE
    private final PassageTerService passageTerService;    // DYNAMIQUE

    // ─── GROUPE 1 - Import / Admin

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importStations() {
        log.info(">> Import gares TER depuis API Navitia/SNCF");
        return ResponseEntity.ok(Map.of("imported", stationTerService.importStationsFromApi()));
    }

    // ─── GROUPE 2 - API Front (lecture seule, statique, aucun appel Navitia)

    @GetMapping
    public ResponseEntity<List<StationTerDTO>> getAllStationsTer() {
        return ResponseEntity.ok(stationTerService.getAllStations());
    }

    @GetMapping("/positions")
    public ResponseEntity<List<StationPositionTerDTO>> getPositions() {
        return ResponseEntity.ok(stationTerService.getAllPositions());
    }

    // ─── GROUPE 3 - Temps reel (dynamique, cache, jamais persiste)

    /**
     * GET /api/stationTer/passages?navitiaId=stop_area:SNCF:87581009
     */
    @GetMapping("/passages")
    public ResponseEntity<List<PassageTerDTO>> getNextPassages(@RequestParam String navitiaId) {
        return ResponseEntity.ok(passageTerService.getNextPassages(navitiaId));
    }
}