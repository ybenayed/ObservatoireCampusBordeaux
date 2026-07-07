package com.smartcampus.backend.dto.weather;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CurrentWeatherDTO {
    private Double temperature;
    private Double windspeed;
    private Integer winddirection;
    private Integer weathercode;
    private String description; // deduit du weathercode + is_day
    private String icon;
    private String time;
    private Integer isDay;
}