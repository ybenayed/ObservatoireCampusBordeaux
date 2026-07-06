package com.smartcampus.backend.mapper.freevehicle;

import com.smartcampus.backend.dto.freevehicle.VehicleTypeFVDTO;
import com.smartcampus.backend.entity.freevehicle.VehicleTypeFV;
import org.springframework.stereotype.Component;

@Component
public class VehicleTypeFVMapper {

    public VehicleTypeFVDTO toDTO(VehicleTypeFV type) {
        return VehicleTypeFVDTO.builder()
                .id(type.getId())
                .vehicleTypeId(type.getVehicleTypeId())
                .formFactor(type.getFormFactor())
                .propulsionType(type.getPropulsionType())
                .name(type.getName())
                .maxRangeMeters(type.getMaxRangeMeters())
                .build();
    }
}