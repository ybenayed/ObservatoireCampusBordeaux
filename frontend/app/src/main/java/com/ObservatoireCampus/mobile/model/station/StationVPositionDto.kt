package com.ObservatoireCampus.mobile.model.station

data class StationVPositionDto(
    val id: Long,
    val stationId: String,
    val nom: String,
    val latitude: Double,
    val longitude: Double
)

data class StationVDetailDto(
    val stationId: String,
    val nom: String?,
    val adresse: String?,
    val capacite: Int?,
    val latitude: Double,
    val longitude: Double,
    val velosDisponibles: Int?,
    val velosClassiques: Int?,
    val velosElectriques: Int?,
    val placesDisponibles: Int?,
    val enService: Boolean?,
    val derniereMaj: String?
)