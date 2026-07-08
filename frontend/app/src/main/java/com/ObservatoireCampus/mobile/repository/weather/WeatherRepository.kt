package com.ObservatoireCampus.mobile.repository.weather

import com.ObservatoireCampus.mobile.model.weather.CurrentWeatherDto
import com.ObservatoireCampus.mobile.model.weather.DailyWeatherDto
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import com.ObservatoireCampus.mobile.model.weather.WeatherAtDto
import com.ObservatoireCampus.mobile.model.weather.WeatherRequestDto
import com.ObservatoireCampus.mobile.model.weather.WeatherSummaryDto
import com.ObservatoireCampus.mobile.network.RetrofitClient

class WeatherRepository {
    suspend fun getSummary(): WeatherSummaryDto = RetrofitClient.weatherApi.getSummary()
    suspend fun getCurrent(): CurrentWeatherDto = RetrofitClient.weatherApi.getCurrent()
    suspend fun getHourlyToday(): List<HourlyWeatherPointDto> = RetrofitClient.weatherApi.getHourlyToday()
    suspend fun getForecast(): List<DailyWeatherDto> = RetrofitClient.weatherApi.getForecast()
    suspend fun getPast(): List<DailyWeatherDto> = RetrofitClient.weatherApi.getPast()
    suspend fun getWeatherAt(request: WeatherRequestDto): WeatherAtDto =
        RetrofitClient.weatherApi.getWeatherAt(request)
}