package com.ObservatoireCampus.mobile.model.weather

data class HourlyWeatherPointDto(
    val time: String,
    val temperature: Double?,
    val weathercode: Int?,
    val description: String?,
    val icon: String?,
    val precipitationProbability: Int?
)