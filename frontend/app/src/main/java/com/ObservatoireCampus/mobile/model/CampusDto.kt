package com.ObservatoireCampus.mobile.model


data class CampusDto(
    val id: Long,
    val name: String,
    val city: String,
    val centerLat: Double,
    val centerLng: Double,
    val perimeterMeters: Double,
    val polygonCoordinates: List<List<Double>>, // [[lng, lat], ...]
    val importedAt: String
)