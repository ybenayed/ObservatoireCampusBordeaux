package com.ObservatoireCampus.mobile.model.station

data class StationTerPositionDto(
    val id: Long,
    val navitiaId: String,
    val nom: String,
    val latitude: Double,
    val longitude: Double
)

data class PassageTerDto(
    val ligne: String?,
    val modeCommercial: String?,   // "TER", "TGV INOUI"...
    val direction: String?,
    val destination: String?,
    val heureTheorique: String?,
    val heurePrevue: String?,
    val retardSecondes: Long?,
    val tempsReel: Boolean
)