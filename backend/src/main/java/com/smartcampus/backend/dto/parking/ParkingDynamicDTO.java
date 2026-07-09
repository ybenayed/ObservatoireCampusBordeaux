package com.smartcampus.backend.dto.parking;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingDynamicDTO {
    private String ident;
    private String etat;
    private Integer libre;      // <- reste "libre" cote DTO/front, mais lu depuis "libres" JSON
    private Integer totalTempsReel; // capacite reellement en service (differe de npTotal statique)
    private Boolean connecte;   // capteur connecte (0/1 -> booleen)
    private OffsetDateTime mdate;
    private OffsetDateTime fetchedAt;
}