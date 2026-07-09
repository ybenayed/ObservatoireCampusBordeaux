package com.ObservatoireCampus.mobile.repository

import com.ObservatoireCampus.mobile.model.parking.ParkingCountDto
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import com.ObservatoireCampus.mobile.model.parking.ParkingStatusDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class ParkingRepository {
    suspend fun getAllPositions(): List<ParkingPositionDto> =
        RetrofitClient.parkingApi.getAllPositions()

    suspend fun getCountByType(): List<ParkingCountDto> =
        RetrofitClient.parkingApi.getCountByType()

    suspend fun getParkingStatus(id: Long): ParkingStatusDto =
        RetrofitClient.parkingApi.getParkingStatus(id)
}