package com.ObservatoireCampus.mobile.model.weather

data class DailyAirQualityDto(
    val date: String,
    val pm2_5Avg: Double?,
    val pm10Avg: Double?,
    val ozoneAvg: Double?,
    val nitrogenDioxideAvg: Double?,
    val europeanAqiMax: Int?,
    val category: String?,
    val description: String?,
    val icon: String?
)