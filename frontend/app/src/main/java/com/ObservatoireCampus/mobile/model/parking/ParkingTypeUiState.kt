package com.ObservatoireCampus.mobile.model.parking

data class ParkingTypeUiState(
    val taType: String,
    val count: Long,
    val visible: Boolean = false
)