package com.smartcampus.backend.entity.freevehicle;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "free_vehicle_fv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeVehicleFV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bikeId;

    private String vehicleTypeId;
    private Boolean isReserved;
    private Boolean isDisabled;
    private String rentalUriAndroid;
    private String rentalUriIos;
    private String pricingPlanId;
    private Integer currentRangeMeters;
}