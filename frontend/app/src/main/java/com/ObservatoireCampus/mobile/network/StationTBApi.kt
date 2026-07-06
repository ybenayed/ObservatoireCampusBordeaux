package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.station.PassageDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StationTBApi {
    @GET("api/stationTB/positions")
    suspend fun getPositions(@Query("mode") mode: String? = null): List<StationTBPositionDto>

    @GET("api/stationTB/passages")
    suspend fun getPassages(@Query("stopId") stopId: String): List<PassageDto>
}