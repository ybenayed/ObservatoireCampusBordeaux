package com.ObservatoireCampus.mobile.repository.weather

import com.ObservatoireCampus.mobile.model.weather.AirQualityAtDto
import com.ObservatoireCampus.mobile.model.weather.AirQualitySummaryDto
import com.ObservatoireCampus.mobile.model.weather.CurrentAirQualityDto
import com.ObservatoireCampus.mobile.model.weather.DailyAirQualityDto
import com.ObservatoireCampus.mobile.model.weather.HourlyAirQualityPointDto
import com.ObservatoireCampus.mobile.model.weather.WeatherRequestDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class AirQualityRepository {
    suspend fun getSummary(): AirQualitySummaryDto = RetrofitClient.airQualityApi.getSummary()
    suspend fun getCurrent(): CurrentAirQualityDto = RetrofitClient.airQualityApi.getCurrent()
    suspend fun getHourlyToday(): List<HourlyAirQualityPointDto> = RetrofitClient.airQualityApi.getHourlyToday()
    suspend fun getForecast(): List<DailyAirQualityDto> = RetrofitClient.airQualityApi.getForecast()
    suspend fun getPast(): List<DailyAirQualityDto> = RetrofitClient.airQualityApi.getPast()
    suspend fun getAirQualityAt(request: WeatherRequestDto): AirQualityAtDto =
        RetrofitClient.airQualityApi.getAirQualityAt(request)
}