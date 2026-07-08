package com.ObservatoireCampus.mobile.viewmodel.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ObservatoireCampus.mobile.repository.weather.AirQualityRepository
import com.ObservatoireCampus.mobile.repository.weather.WeatherRepository

class WeatherViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val airQualityRepository: AirQualityRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(weatherRepository, airQualityRepository) as T
    }
}