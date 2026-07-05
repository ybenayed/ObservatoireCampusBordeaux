package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StationPositionVDTO {
    private Long id;
    private String stationId;
    private String nom;
    private Double latitude;
    private Double longitude;
}