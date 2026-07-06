package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.station.StationVDetailDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StationVApi {
    @GET("api/stationV/positions")
    suspend fun getPositions(): List<StationVPositionDto>

    @GET("api/stationV/{stationId}")
    suspend fun getDetail(@Path("stationId") stationId: String): StationVDetailDto
}