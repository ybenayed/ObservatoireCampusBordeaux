package com.ObservatoireCampus.mobile.model.weather

data class AirQualitySummaryDto(
    val latitude: Double?,
    val longitude: Double?,
    val current: CurrentAirQualityDto?,
    val hourlyToday: List<HourlyAirQualityPointDto> = emptyList(),
    val pastDaily: List<DailyAirQualityDto> = emptyList(),
    val forecastDaily: List<DailyAirQualityDto> = emptyList()
)