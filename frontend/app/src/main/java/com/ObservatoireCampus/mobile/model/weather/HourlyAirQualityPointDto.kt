package com.ObservatoireCampus.mobile.model.weather

data class HourlyAirQualityPointDto(
    val time: String,
    val pm2_5: Double?,
    val pm10: Double?,
    val ozone: Double?,
    val nitrogenDioxide: Double?,
    val europeanAqi: Int?,
    val category: String?,
    val description: String?,
    val icon: String?
)