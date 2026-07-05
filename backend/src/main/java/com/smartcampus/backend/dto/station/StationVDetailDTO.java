package com.smartcampus.backend.dto.station;

import lombok.*;

// Jointure statique + dynamique pour une station donnee
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationVDetailDTO {
    // statique
    private String stationId;
    private String nom;
    private String adresse;
    private Integer capacite;
    private Double latitude;
    private Double longitude;
    // dynamique
    private Integer velosDisponibles;
    private Integer velosClassiques;
    private Integer velosElectriques;
    private Integer placesDisponibles;
    private Boolean enService;
    private String derniereMaj;
}