package com.ObservatoireCampus.mobile.repository.station

import com.ObservatoireCampus.mobile.model.station.PassageDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class StationTBRepository {
    suspend fun getPositions(mode: String? = null): List<StationTBPositionDto> =
        RetrofitClient.stationTBApi.getPositions(mode)

    suspend fun getPassages(stopId: String): List<PassageDto> =
        RetrofitClient.stationTBApi.getPassages(stopId)
}