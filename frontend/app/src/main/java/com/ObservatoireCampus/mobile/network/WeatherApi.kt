package com.ObservatoireCampus.mobile.network

import com.ObservatoireCampus.mobile.model.weather.CurrentWeatherDto
import com.ObservatoireCampus.mobile.model.weather.DailyWeatherDto
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import com.ObservatoireCampus.mobile.model.weather.WeatherAtDto
import com.ObservatoireCampus.mobile.model.weather.WeatherRequestDto
import com.ObservatoireCampus.mobile.model.weather.WeatherSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WeatherApi {
    @GET("api/weather/summary")
    suspend fun getSummary(): WeatherSummaryDto

    @GET("api/weather/current")
    suspend fun getCurrent(): CurrentWeatherDto

    @GET("api/weather/hourly/today")
    suspend fun getHourlyToday(): List<HourlyWeatherPointDto>

    @GET("api/weather/forecast")
    suspend fun getForecast(): List<DailyWeatherDto>

    @GET("api/weather/past")
    suspend fun getPast(): List<DailyWeatherDto>

    @POST("api/weather/at")
    suspend fun getWeatherAt(@Body request: WeatherRequestDto): WeatherAtDto
}