package com.ObservatoireCampus.mobile.model.weather

/**
 * Reponse de /api/air-quality/at.
 * Si "time" n'a pas ete envoye dans la requete : seuls les champs *Avg sont remplis
 * (moyenne du jour). Si "time" a ete envoye : pm2_5/pm10/ozone/nitrogenDioxide et
 * europeanAqi/category/description/icon refletent l'heure precise.
 */
data class AirQualityAtDto(
    val latitude: Double?,
    val longitude: Double?,
    val date: String?,
    val time: String?,
    val pm2_5Avg: Double?,
    val pm10Avg: Double?,
    val ozoneAvg: Double?,
    val nitrogenDioxideAvg: Double?,
    val pm2_5: Double?,
    val pm10: Double?,
    val ozone: Double?,
    val nitrogenDioxide: Double?,
    val europeanAqi: Int?,
    val category: String?,
    val description: String?,
    val icon: String?
)