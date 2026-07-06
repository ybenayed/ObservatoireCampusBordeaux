package com.smartcampus.backend.service.freevehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.freevehicle.FreeVehicleFVDTO;
import com.smartcampus.backend.entity.freevehicle.FreeVehicleFV;
import com.smartcampus.backend.mapper.freevehicle.FreeVehicleFVMapper;
import com.smartcampus.backend.repository.freevehicle.FreeVehicleFVRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeVehicleFVService {

    private final FreeVehicleFVRepository freeVehicleFVRepository;
    private final FreeVehicleFVMapper freeVehicleFVMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String FREE_BIKE_STATUS_URL =
        "https://services.rideyego.com/gbfs/2-2/bordeaux/fr/free_bike_status";

    // ─── LECTURE

    public List<FreeVehicleFVDTO> getAllVehicles() {
        return freeVehicleFVRepository.findAll().stream().map(freeVehicleFVMapper::toDTO).toList();
    }

    public List<FreeVehicleFVDTO> getVehiclesByType(String vehicleTypeId) {
        return freeVehicleFVRepository.findByVehicleTypeId(vehicleTypeId).stream()
                .map(freeVehicleFVMapper::toDTO)
                .toList();
    }

    // ─── IMPORT DEPUIS L'API (idempotent) - seules les infos "catalogue" sont persistees

    public int importVehiclesFromApi() {
        int imported = 0;
        String rawJson = restTemplate.getForObject(FREE_BIKE_STATUS_URL, String.class);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode bikes = root.path("data").path("bikes");

            for (JsonNode bike : bikes) {
                String bikeId = bike.path("bike_id").asText(null);
                if (bikeId == null || freeVehicleFVRepository.existsByBikeId(bikeId)) {
                    continue; // idempotent
                }

                freeVehicleFVRepository.save(buildVehicleFromRecord(bike));
                imported++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur import vehicules libre-service", e);
        }

        log.info("Import vehicules termine : {} nouveaux vehicules", imported);
        return imported;
    }

    private FreeVehicleFV buildVehicleFromRecord(JsonNode bike) {
        JsonNode rentalUris = bike.path("rental_uris");

        return FreeVehicleFV.builder()
                .bikeId(bike.path("bike_id").asText(null))
                .vehicleTypeId(bike.path("vehicle_type_id").asText(null))
                .isReserved(asBooleanOrNull(bike.path("is_reserved")))
                .isDisabled(asBooleanOrNull(bike.path("is_disabled")))
                .rentalUriAndroid(rentalUris.path("android").asText(null))
                .rentalUriIos(rentalUris.path("ios").asText(null))
                .pricingPlanId(bike.path("pricing_plan_id").asText(null))
                .currentRangeMeters(bike.hasNonNull("current_range_meters") ? bike.get("current_range_meters").asInt() : null)
                .build();
    }

    // is_reserved / is_disabled peuvent arriver en boolean ou en 0/1 selon les providers
    private Boolean asBooleanOrNull(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        if (node.isBoolean()) return node.asBoolean();
        if (node.isNumber()) return node.asInt() != 0;
        return null;
    }
}