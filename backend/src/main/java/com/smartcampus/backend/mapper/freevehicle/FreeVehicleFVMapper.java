package com.smartcampus.backend.mapper.freevehicle;

import com.smartcampus.backend.dto.freevehicle.FreeVehicleFVDTO;
import com.smartcampus.backend.entity.freevehicle.FreeVehicleFV;
import org.springframework.stereotype.Component;

@Component
public class FreeVehicleFVMapper {

    public FreeVehicleFVDTO toDTO(FreeVehicleFV vehicle) {
        return FreeVehicleFVDTO.builder()
                .id(vehicle.getId())
                .bikeId(vehicle.getBikeId())
                .vehicleTypeId(vehicle.getVehicleTypeId())
                .isReserved(vehicle.getIsReserved())
                .isDisabled(vehicle.getIsDisabled())
                .rentalUriAndroid(vehicle.getRentalUriAndroid())
                .rentalUriIos(vehicle.getRentalUriIos())
                .pricingPlanId(vehicle.getPricingPlanId())
                .currentRangeMeters(vehicle.getCurrentRangeMeters())
                .build();
    }
}