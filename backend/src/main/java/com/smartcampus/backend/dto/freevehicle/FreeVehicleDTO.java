package com.smartcampus.backend.dto.freevehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FreeVehicleDTO {
    // ─── statique (BDD)
    private String bikeId;
    private String vehicleTypeId;
    private Boolean isReserved;
    private Boolean isDisabled;
    private String rentalUriAndroid;
    private String rentalUriIos;
    private String pricingPlanId;
    private Integer currentRangeMeters;

    // ─── dynamique (API live)
    private Double latitude;
    private Double longitude;
    private String lastReported;
}