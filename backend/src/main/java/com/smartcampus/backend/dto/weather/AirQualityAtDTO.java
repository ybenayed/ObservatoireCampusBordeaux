package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AirQualityAtDTO {

    private Double latitude;
    private Double longitude;
    private LocalDate date;
    private LocalTime time;

    // Resume du jour demande (moyennes)
    private Double pm2_5Avg;
    private Double pm10Avg;
    private Double ozoneAvg;
    private Double nitrogenDioxideAvg;

    // Point horaire demande (rempli seulement si "time" est fourni et disponible)
    private Double pm2_5;
    private Double pm10;
    private Double ozone;
    private Double nitrogenDioxide;

    // Indice + traduction (icone / signification)
    private Integer europeanAqi;
    private String category;
    private String description;
    private String icon;
}