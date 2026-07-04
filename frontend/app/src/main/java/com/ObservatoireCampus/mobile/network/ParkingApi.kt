package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.parking.ParkingCountDto
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import retrofit2.http.GET

interface ParkingApi {
    @GET("api/parking/positions")
    suspend fun getAllPositions(): List<ParkingPositionDto>

    @GET("api/parking/count-by-type")
    suspend fun getCountByType(): List<ParkingCountDto>
}