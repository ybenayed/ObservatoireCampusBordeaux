package com.ObservatoireCampus.mobile.model.parking

data class ParkingPositionDto(
    val id: Long,
    val ident: String,
    val nom: String,
    val taType: String,
    val latitude: Double?,
    val longitude: Double?
)