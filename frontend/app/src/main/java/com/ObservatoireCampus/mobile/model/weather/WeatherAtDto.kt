package com.ObservatoireCampus.mobile.model.weather

data class WeatherAtDto(
    val latitude: Double?,
    val longitude: Double?,
    val date: String?,
    val time: String?,
    val temperatureMax: Double?,
    val temperatureMin: Double?,
    val precipitationSum: Double?,
    val windspeedMax: Double?,
    val temperature: Double?,
    val precipitationProbability: Int?,
    val weathercode: Int?,
    val description: String?,
    val icon: String?
)