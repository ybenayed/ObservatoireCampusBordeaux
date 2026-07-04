package com.smartcampus.backend.controller.station;

import com.smartcampus.backend.dto.station.PassageTBDTO;
import com.smartcampus.backend.dto.station.StationTBDTO;
import com.smartcampus.backend.dto.station.StationPositionTBDTO;
import com.smartcampus.backend.service.station.PassageTBService;
import com.smartcampus.backend.service.station.StationTBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stationTB")
@CrossOrigin(origins = "*")
public class StationTBController {

    private final StationTBService stationTBService;   // STATIQUE
    private final PassageTBService passageTBService;    // DYNAMIQUE

    // ─── GROUPE 1 - Import / Admin

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importStations() {
        log.info(">> Import stations bus/tram depuis API Mecatran");
        return ResponseEntity.ok(Map.of("imported", stationTBService.importStationsFromApi()));
    }

    // ─── GROUPE 2 - API Front (lecture seule, statique)

    @GetMapping
    public ResponseEntity<List<StationTBDTO>> getAllStationsTB() {
        return ResponseEntity.ok(stationTBService.getAllStations());
    }

    /**
     * GET /api/stationTB/positions            -> toutes les stations
     * GET /api/stationTB/positions?mode=TRAM  -> uniquement tram
     * GET /api/stationTB/positions?mode=BUS   -> uniquement bus
     */
    @GetMapping("/positions")
    public ResponseEntity<List<StationPositionTBDTO>> getPositions(
            @RequestParam(required = false) String mode) {
        if (mode != null) {
            return ResponseEntity.ok(stationTBService.getPositionsByMode(mode.toUpperCase()));
        }
        return ResponseEntity.ok(stationTBService.getAllPositions());
    }

    // ─── GROUPE 3 - Temps reel (dynamique, jamais persiste)

    /**
     * GET /api/stationTB/passages?stopId=bordeaux:StopPoint:BP:3729:LOC
     */
    @GetMapping("/passages")
    public ResponseEntity<List<PassageTBDTO>> getNextPassages(@RequestParam String stopId) {
        return ResponseEntity.ok(passageTBService.getNextPassages(stopId));
    }
}