package com.smartcampus.backend.service.freevehicle;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcampus.backend.dto.freevehicle.FreeVehicleDTO;
import com.smartcampus.backend.entity.freevehicle.FreeVehicleFV;
import com.smartcampus.backend.repository.freevehicle.FreeVehicleFVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FreeVehicleInfoService {

    private final FreeVehicleFVRepository freeVehicleFVRepository;           // STATIQUE (BDD)
    private final FreeVehiclePositionCacheService freeVehiclePositionCacheService; // DYNAMIQUE (cache 10s)

    public FreeVehicleDTO getVehicleInfo(String bikeId) {
        FreeVehicleFV vehicle = freeVehicleFVRepository.findByBikeId(bikeId).orElse(null);
        if (vehicle == null) return null;

        JsonNode live = freeVehiclePositionCacheService.getRawVehicle(bikeId);

        Double lat = (live != null && live.hasNonNull("lat")) ? live.get("lat").asDouble() : null;
        Double lon = (live != null && live.hasNonNull("lon")) ? live.get("lon").asDouble() : null;
        String lastReported = (live != null && live.hasNonNull("last_reported"))
                ? Instant.ofEpochSecond(live.get("last_reported").asLong()).toString()
                : null;

        return FreeVehicleDTO.builder()
                .bikeId(vehicle.getBikeId())
                .vehicleTypeId(vehicle.getVehicleTypeId())
                .isReserved(vehicle.getIsReserved())
                .isDisabled(vehicle.getIsDisabled())
                .rentalUriAndroid(vehicle.getRentalUriAndroid())
                .rentalUriIos(vehicle.getRentalUriIos())
                .pricingPlanId(vehicle.getPricingPlanId())
                .currentRangeMeters(vehicle.getCurrentRangeMeters())
                .latitude(lat)
                .longitude(lon)
                .lastReported(lastReported)
                .build();
    }
}