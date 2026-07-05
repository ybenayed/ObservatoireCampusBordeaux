package com.smartcampus.backend.controller.station;

import com.smartcampus.backend.dto.station.StationVDTO;
import com.smartcampus.backend.dto.station.StationPositionVDTO;
import com.smartcampus.backend.dto.station.StationVDetailDTO;
import com.smartcampus.backend.service.station.StationVService;
import com.smartcampus.backend.service.station.StationVStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stationV")
@CrossOrigin(origins = "*")
public class StationVController {

    private final StationVService stationVService;             // STATIQUE
    private final StationVStatusService stationVStatusService; // DYNAMIQUE

    // ─── GROUPE 1 - Import / Admin

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importStations() {
        log.info(">> Import stations velo depuis API GBFS");
        return ResponseEntity.ok(Map.of("imported", stationVService.importStationsFromApi()));
    }

    // ─── GROUPE 2 - API Front (lecture seule, statique)

    @GetMapping
    public ResponseEntity<List<StationVDTO>> getAllStationsV() {
        return ResponseEntity.ok(stationVService.getAllStations());
    }

    @GetMapping("/positions")
    public ResponseEntity<List<StationPositionVDTO>> getPositions() {
        return ResponseEntity.ok(stationVService.getAllPositions());
    }

    // ─── GROUPE 3 - Info d'une station donnee (statique + dynamique jointes)

    /**
     * GET /api/stationV/1
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<StationVDetailDTO> getStationDetail(@PathVariable String stationId) {
        return stationVStatusService.getStationDetail(stationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}