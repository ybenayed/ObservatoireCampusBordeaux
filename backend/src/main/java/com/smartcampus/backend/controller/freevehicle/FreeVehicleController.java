package com.smartcampus.backend.controller.freevehicle;

import com.smartcampus.backend.dto.freevehicle.FreeVehicleDTO;
import com.smartcampus.backend.dto.freevehicle.FreeVehicleFVDTO;
import com.smartcampus.backend.dto.freevehicle.FreeVehiclePositionDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeCountDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVDTO;
import com.smartcampus.backend.service.freevehicle.FreeVehicleFVService;
import com.smartcampus.backend.service.freevehicle.FreeVehicleInfoService;
import com.smartcampus.backend.service.freevehicle.FreeVehiclePositionCacheService;
import com.smartcampus.backend.service.freevehicle.VehicleTypeFVService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/freeVehicle")
@CrossOrigin(origins = "*")
public class FreeVehicleController {

    private final VehicleTypeFVService vehicleTypeFVService;         // STATIQUE (types)
    private final FreeVehicleFVService freeVehicleFVService;         // STATIQUE (catalogue vehicules)
    private final FreeVehicleInfoService freeVehicleInfoService;     // FUSION (statique + dynamique)
    private final FreeVehiclePositionCacheService freeVehiclePositionCacheService; // Dynamique (positions live, cache 10s) 


    // ─── GROUPE 1 - Import / Admin (statique)

    @PostMapping("/import-types")
    public ResponseEntity<Map<String, Integer>> importVehicleTypes() {
        log.info(">> Import types de vehicules depuis API RideYeGo");
        return ResponseEntity.ok(Map.of("imported", vehicleTypeFVService.importTypesFromApi()));
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importVehicles() {
        log.info(">> Import catalogue vehicules libre-service depuis API RideYeGo");
        return ResponseEntity.ok(Map.of("imported", freeVehicleFVService.importVehiclesFromApi()));
    }

    // ─── GROUPE 2 - API Front (lecture seule, statique)

    @GetMapping("/types")
    public ResponseEntity<List<VehicleTypeFVDTO>> getAllTypes() {
        return ResponseEntity.ok(vehicleTypeFVService.getAllTypes());
    }

    @GetMapping
    public ResponseEntity<List<FreeVehicleFVDTO>> getAllVehicles(
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(freeVehicleFVService.getVehiclesByType(type));
        }
        return ResponseEntity.ok(freeVehicleFVService.getAllVehicles());
    }

    // ─── GROUPE 3 - Temps reel (dynamique, jamais persiste)

    /**
     * GET /api/freeVehicle/positions              -> toutes les positions
     * GET /api/freeVehicle/positions?type=xxx_id  -> positions filtrees par type
     */
    @GetMapping("/positions")
    public ResponseEntity<List<FreeVehiclePositionDTO>> getPositions(
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(freeVehiclePositionCacheService.getPositionsByType(type));
        }
        return ResponseEntity.ok(freeVehiclePositionCacheService.getAllPositions());
    }


    // ─── GROUPE 4 - Fusion statique + dynamique

    /**
     * GET /api/freeVehicle/{bikeId} -> infos catalogue (BDD) + position live (API)
     */
    @GetMapping("/{bikeId}")
    public ResponseEntity<FreeVehicleDTO> getVehicleInfo(@PathVariable String bikeId) {
        FreeVehicleDTO dto = freeVehicleInfoService.getVehicleInfo(bikeId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }


    /**
     * GET /api/freeVehicle/types/count -> chaque type + nombre de vehicules correspondant
     */
    @GetMapping("/types/count")
    public ResponseEntity<List<VehicleTypeCountDTO>> getVehicleCountByType() {
        return ResponseEntity.ok(vehicleTypeFVService.getVehicleCountByType());
    }
}