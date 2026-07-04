package com.smartcampus.backend.dto.parking;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingPositionDTO {
    private Long id;
    private String ident;
    private String nom;
    private String taType;
    private Double latitude;
    private Double longitude;
}