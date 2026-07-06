package com.ObservatoireCampus.mobile.repository.freevehicle

import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehiclePositionDto
import com.ObservatoireCampus.mobile.model.freevehicle.VehicleTypeCountDto
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class FreeVehicleRepository {
    suspend fun getTypesCount(): List<VehicleTypeCountDto> =
        RetrofitClient.freeVehicleApi.getTypesCount()

    suspend fun getPositions(type: String? = null): List<FreeVehiclePositionDto> =
        RetrofitClient.freeVehicleApi.getPositions(type)

    suspend fun getVehicleDetail(bikeId: String): FreeVehicleDetailDto =
        RetrofitClient.freeVehicleApi.getVehicleDetail(bikeId)
}