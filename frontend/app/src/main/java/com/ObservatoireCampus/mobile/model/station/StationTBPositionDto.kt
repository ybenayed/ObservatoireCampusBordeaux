package com.ObservatoireCampus.mobile.model.station
data class StationTBPositionDto(
    val id: Long,
    val stopId: String,
    val nom: String,
    val mode: String,       // "TRAM" ou "BUS"
    val latitude: Double,
    val longitude: Double
)

data class PassageDto(
    val ligne: String?,
    val direction: String?,
    val destination: String?,
    val heureTheorique: String?,
    val heurePrevue: String?,
    val retardSecondes: Long?
)