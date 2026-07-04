package com.smartcampus.backend.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PassageTBDTO {
    private String ligne;
    private String direction;
    private String destination;
    private String heureTheorique; // AimedArrivalTime
    private String heurePrevue;    // ExpectedArrivalTime (peut etre null)
    private Long retardSecondes;   // ExpectedArrivalTime - AimedArrivalTime, null si pas de temps reel dispo
}