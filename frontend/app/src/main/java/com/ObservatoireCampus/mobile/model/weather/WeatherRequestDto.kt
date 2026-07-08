package com.ObservatoireCampus.mobile.model.weather

/**
 * Utilise pour interroger /api/weather/at ET /api/air-quality/at (meme forme cote backend).
 */
data class WeatherRequestDto(
    val latitude: Double,
    val longitude: Double,
    val date: String,        // format "yyyy-MM-dd"
    val time: String? = null // format "HH:mm:ss"
)