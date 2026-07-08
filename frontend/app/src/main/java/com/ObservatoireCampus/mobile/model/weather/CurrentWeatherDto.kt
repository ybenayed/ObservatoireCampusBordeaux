package com.ObservatoireCampus.mobile.model.weather

data class CurrentWeatherDto(
    val temperature: Double?,
    val windspeed: Double?,
    val winddirection: Int?,
    val weathercode: Int?,
    val description: String?,
    val icon: String?,
    val time: String?,
    val isDay: Int?
)