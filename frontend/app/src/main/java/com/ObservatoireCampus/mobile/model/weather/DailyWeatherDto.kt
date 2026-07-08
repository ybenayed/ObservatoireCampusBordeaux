package com.ObservatoireCampus.mobile.model.weather

data class DailyWeatherDto(
    val date: String,
    val temperatureMax: Double?,
    val temperatureMin: Double?,
    val weathercode: Int?,
    val description: String?,
    val icon: String?,
    val precipitationSum: Double?,
    val windspeedMax: Double?
)