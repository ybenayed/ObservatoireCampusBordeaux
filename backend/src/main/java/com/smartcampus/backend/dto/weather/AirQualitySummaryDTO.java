package com.smartcampus.backend.dto.weather;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AirQualitySummaryDTO {

    private Double latitude;
    private Double longitude;

    private CurrentAirQualityDTO current;

    private List<HourlyAirQualityPointDTO> hourlyToday;

    private List<DailyAirQualityDTO> pastDaily;

    private List<DailyAirQualityDTO> forecastDaily;
}