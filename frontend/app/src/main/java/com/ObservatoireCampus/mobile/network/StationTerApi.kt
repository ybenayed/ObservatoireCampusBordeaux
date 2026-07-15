package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.station.PassageTerDto
import com.ObservatoireCampus.mobile.model.station.StationTerPositionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StationTerApi {
    @GET("api/stationTer/positions")
    suspend fun getPositions(): List<StationTerPositionDto>

    @GET("api/stationTer/passages")
    suspend fun getPassages(@Query("navitiaId") navitiaId: String): List<PassageTerDto>
}