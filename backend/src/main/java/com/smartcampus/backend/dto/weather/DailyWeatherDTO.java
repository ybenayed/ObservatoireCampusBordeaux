package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyWeatherDTO {
    private LocalDate date;
    private Double temperatureMax;
    private Double temperatureMin;
    private Integer weathercode;
    private String description; // NOUVEAU - toujours variante "jour" (resume de la journee)
    private String icon;        // NOUVEAU
    private Double precipitationSum;
    private Double windspeedMax;
}