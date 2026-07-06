package com.smartcampus.backend.dto.freevehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FreeVehiclePositionDTO {
    private String bikeId;
    private String vehicleTypeId;
    private Double latitude;
    private Double longitude;
}