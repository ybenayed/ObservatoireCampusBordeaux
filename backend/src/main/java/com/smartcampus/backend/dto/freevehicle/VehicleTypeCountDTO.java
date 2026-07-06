package com.smartcampus.backend.dto.freevehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleTypeCountDTO {
    private String vehicleTypeId;
    private String name;
    private Long count;
}