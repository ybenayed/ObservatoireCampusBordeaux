package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.weather.AirQualityAtDto
import com.ObservatoireCampus.mobile.model.weather.AirQualitySummaryDto
import com.ObservatoireCampus.mobile.model.weather.CurrentAirQualityDto
import com.ObservatoireCampus.mobile.model.weather.DailyAirQualityDto
import com.ObservatoireCampus.mobile.model.weather.HourlyAirQualityPointDto
import com.ObservatoireCampus.mobile.model.weather.WeatherRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AirQualityApi {
    @GET("api/air-quality/summary")
    suspend fun getSummary(): AirQualitySummaryDto

    @GET("api/air-quality/current")
    suspend fun getCurrent(): CurrentAirQualityDto

    @GET("api/air-quality/hourly/today")
    suspend fun getHourlyToday(): List<HourlyAirQualityPointDto>

    @GET("api/air-quality/forecast")
    suspend fun getForecast(): List<DailyAirQualityDto>

    @GET("api/air-quality/past")
    suspend fun getPast(): List<DailyAirQualityDto>

    @POST("api/air-quality/at")
    suspend fun getAirQualityAt(@Body request: WeatherRequestDto): AirQualityAtDto
}