package com.ObservatoireCampus.mobile.repository.station

import com.ObservatoireCampus.mobile.model.station.PassageTerDto
import com.ObservatoireCampus.mobile.model.station.StationTerPositionDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class StationTerRepository {
    suspend fun getPositions(): List<StationTerPositionDto> =
        RetrofitClient.stationTerApi.getPositions()

    suspend fun getPassages(navitiaId: String): List<PassageTerDto> =
        RetrofitClient.stationTerApi.getPassages(navitiaId)
}