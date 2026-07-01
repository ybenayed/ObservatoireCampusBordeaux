package com.smartcampus.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampusDTO {

    private Long id;
    private String name;
    private String city;

    private Double centerLat;
    private Double centerLng;

    private Double perimeterMeters;

    // GeoJSON-style : liste de [lng, lat] pour le front (Leaflet / Mapbox)
    private List<double[]> polygonCoordinates;

    private LocalDateTime importedAt;
}