package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StationPositionTBDTO {
    private Long id;
    private String stopId;
    private String nom;
    private String mode;
    private Double latitude;
    private Double longitude;
}