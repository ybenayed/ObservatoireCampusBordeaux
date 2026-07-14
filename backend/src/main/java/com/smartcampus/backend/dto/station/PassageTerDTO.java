package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PassageTerDTO {
    private String ligne;          // ex: TER, code de ligne
    private String modeCommercial; // ex: "TER", "TGV INOUI" si la gare a du trafic mixte
    private String direction;      // ex: "Bordeaux Saint-Jean (Bordeaux)"
    private String destination;    // headsign affiche en gare
    private String heureTheorique; // base_departure_date_time
    private String heurePrevue;    // departure_date_time (temps reel si different)
    private Long retardSecondes;   // null si pas de retard / pas de temps reel
    private boolean tempsReel;     // true si data_freshness == "realtime"
}