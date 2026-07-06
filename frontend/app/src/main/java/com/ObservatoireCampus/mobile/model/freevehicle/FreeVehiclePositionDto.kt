package com.ObservatoireCampus.mobile.model.freevehicle

data class FreeVehiclePositionDto(
    val bikeId: String,
    val vehicleTypeId: String,
    val latitude: Double,
    val longitude: Double
)

data class VehicleTypeCountDto(
    val vehicleTypeId: String,
    val name: String,
    val count: Long
)