package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehiclePositionDto
import com.ObservatoireCampus.mobile.model.freevehicle.VehicleTypeCountDto
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehicleDetailDto
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeVehicleApi {
    @GET("api/freeVehicle/types/count")
    suspend fun getTypesCount(): List<VehicleTypeCountDto>

    @GET("api/freeVehicle/positions")
    suspend fun getPositions(@Query("type") type: String? = null): List<FreeVehiclePositionDto>

    @GET("api/freeVehicle/{bikeId}")
    suspend fun getVehicleDetail(@Path("bikeId") bikeId: String): FreeVehicleDetailDto
}