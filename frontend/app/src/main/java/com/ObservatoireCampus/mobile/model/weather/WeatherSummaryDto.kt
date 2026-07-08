package com.ObservatoireCampus.mobile.model.weather

data class WeatherSummaryDto(
    val latitude: Double?,
    val longitude: Double?,
    val current: CurrentWeatherDto?,
    val hourlyToday: List<HourlyWeatherPointDto> = emptyList(),
    val pastDaily: List<DailyWeatherDto> = emptyList(),
    val forecastDaily: List<DailyWeatherDto> = emptyList()
)