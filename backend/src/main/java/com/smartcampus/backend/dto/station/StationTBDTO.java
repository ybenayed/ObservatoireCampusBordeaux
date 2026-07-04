package com.smartcampus.backend.dto.station;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationTBDTO {
    private Long id;
    private String stopId;
    private String nom;
    private String stopAreaRef;
    private String mode;
    private Double latitude;
    private Double longitude;
    private List<String> lines;
}