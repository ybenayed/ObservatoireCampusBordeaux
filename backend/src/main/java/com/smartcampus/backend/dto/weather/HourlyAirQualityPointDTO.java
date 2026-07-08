package com.smartcampus.backend.dto.weather;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HourlyAirQualityPointDTO {
    private String time;
    private Double pm2_5;
    private Double pm10;
    private Double ozone;
    private Double nitrogenDioxide;
    private Integer europeanAqi;
    private String category;
    private String description;
    private String icon;
}