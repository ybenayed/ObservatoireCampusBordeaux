package com.smartcampus.backend.dto.freevehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleTypeFVDTO {
    private Long id;
    private String vehicleTypeId;
    private String formFactor;
    private String propulsionType;
    private String name;
    private Integer maxRangeMeters;
}