package com.smartcampus.backend.dto.freevehicle;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FreeVehicleFVDTO {
    private Long id;
    private String bikeId;
    private String vehicleTypeId;
    private Boolean isReserved;
    private Boolean isDisabled;
    private String rentalUriAndroid;
    private String rentalUriIos;
    private String pricingPlanId;
    private Integer currentRangeMeters;
}