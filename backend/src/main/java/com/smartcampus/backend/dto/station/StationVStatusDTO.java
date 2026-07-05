package com.smartcampus.backend.dto.station;

import lombok.*;

// Partie dynamique seule (jamais persistee)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationVStatusDTO {
    private String stationId;
    private Integer velosDisponibles;
    private Integer velosClassiques;
    private Integer velosElectriques;
    private Integer placesDisponibles;
    private Boolean enService;      // isRenting && isReturning
    private String derniereMaj;     // last_reported
}