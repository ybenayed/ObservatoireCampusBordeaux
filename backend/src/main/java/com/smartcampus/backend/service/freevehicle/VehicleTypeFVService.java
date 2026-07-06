package com.smartcampus.backend.service.freevehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeCountDTO;
import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVDTO;
import com.smartcampus.backend.entity.freevehicle.VehicleTypeFV;
import com.smartcampus.backend.mapper.freevehicle.VehicleTypeFVMapper;
import com.smartcampus.backend.repository.freevehicle.FreeVehicleFVRepository;
import com.smartcampus.backend.repository.freevehicle.VehicleTypeFVRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleTypeFVService {

    private final VehicleTypeFVRepository vehicleTypeFVRepository;
    private final FreeVehicleFVRepository freeVehicleFVRepository; // pour le comptage
    private final VehicleTypeFVMapper vehicleTypeFVMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String VEHICLE_TYPES_URL =
        "https://services.rideyego.com/gbfs/2-2/bordeaux/fr/vehicle_types";

    public List<VehicleTypeFVDTO> getAllTypes() {
        return vehicleTypeFVRepository.findAll().stream().map(vehicleTypeFVMapper::toDTO).toList();
    }

    // ─── NOUVEAU : chaque type + nombre de vehicules correspondant
    public List<VehicleTypeCountDTO> getVehicleCountByType() {
        // 1. on recupere le nombre de vehicules par type (table FreeVehicleFV)
        Map<String, Long> countsByType = new HashMap<>();
        for (Object[] row : freeVehicleFVRepository.countVehiclesGroupedByType()) {
            String vehicleTypeId = (String) row[0];
            Long count = (Long) row[1];
            countsByType.put(vehicleTypeId, count);
        }

        // 2. on parcourt tous les types connus (table VehicleTypeFV) et on associe le count (0 si absent)
        return vehicleTypeFVRepository.findAll().stream()
                .map(type -> VehicleTypeCountDTO.builder()
                        .vehicleTypeId(type.getVehicleTypeId())
                        .name(type.getName())
                        .count(countsByType.getOrDefault(type.getVehicleTypeId(), 0L))
                        .build())
                .toList();
    }

    public int importTypesFromApi() {
        int imported = 0;
        String rawJson = restTemplate.getForObject(VEHICLE_TYPES_URL, String.class);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode types = root.path("data").path("vehicle_types");

            for (JsonNode type : types) {
                String vehicleTypeId = type.path("vehicle_type_id").asText(null);
                if (vehicleTypeId == null || vehicleTypeFVRepository.existsByVehicleTypeId(vehicleTypeId)) {
                    continue;
                }

                vehicleTypeFVRepository.save(buildTypeFromRecord(type));
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur import types de vehicules", e);
        }

        log.info("Import types de vehicules termine : {} nouveaux types", imported);
        return imported;
    }

    private VehicleTypeFV buildTypeFromRecord(JsonNode type) {
        Integer maxRange = type.hasNonNull("max_range_meters") ? type.get("max_range_meters").asInt() : null;

        return VehicleTypeFV.builder()
                .vehicleTypeId(type.path("vehicle_type_id").asText(null))
                .formFactor(type.path("form_factor").asText(null))
                .propulsionType(type.path("propulsion_type").asText(null))
                .name(type.path("name").asText(null))
                .maxRangeMeters(maxRange)
                .build();
    }
}