package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationTerDTO {
    private Long id;
    private String navitiaId;
    private String nom;
    private Double latitude;
    private Double longitude;
    private Double distanceCentreMetres;
}