package com.smartcampus.backend.entity.freevehicle;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_type_fv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTypeFV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vehicleTypeId;

    private String formFactor;
    private String propulsionType;
    private String name;
    private Integer maxRangeMeters;
}