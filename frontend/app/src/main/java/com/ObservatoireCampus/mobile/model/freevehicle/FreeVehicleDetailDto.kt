package com.ObservatoireCampus.mobile.model.freevehicle

data class FreeVehicleDetailDto(
    val bikeId: String,
    val vehicleTypeId: String,
    val isReserved: Boolean?,
    val isDisabled: Boolean?,
    val rentalUriAndroid: String?,
    val rentalUriIos: String?,
    val pricingPlanId: String?,
    val currentRangeMeters: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val lastReported: String?
)