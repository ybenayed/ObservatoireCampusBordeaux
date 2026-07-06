package com.ObservatoireCampus.mobile.repository.station

import com.ObservatoireCampus.mobile.model.station.StationVDetailDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class StationVRepository {
    suspend fun getPositions(): List<StationVPositionDto> =
        RetrofitClient.stationVApi.getPositions()

    suspend fun getDetail(stationId: String): StationVDetailDto =
        RetrofitClient.stationVApi.getDetail(stationId)
}