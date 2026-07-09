package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.parking.ParkingCountDto
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import com.ObservatoireCampus.mobile.model.parking.ParkingStatusDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ParkingApi {
    @GET("api/parking/positions")
    suspend fun getAllPositions(): List<ParkingPositionDto>

    @GET("api/parking/count-by-type")
    suspend fun getCountByType(): List<ParkingCountDto>

    @GET("api/parking/{id}/status")
    suspend fun getParkingStatus(@Path("id") id: Long): ParkingStatusDto
}