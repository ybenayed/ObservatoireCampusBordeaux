package com.ObservatoireCampus.mobile.viewmodel.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ObservatoireCampus.mobile.model.weather.AirQualityAtDto
import com.ObservatoireCampus.mobile.model.weather.CurrentWeatherDto
import com.ObservatoireCampus.mobile.model.weather.HourlyWeatherPointDto
import com.ObservatoireCampus.mobile.model.weather.WeatherRequestDto
import com.ObservatoireCampus.mobile.repository.weather.AirQualityRepository
import com.ObservatoireCampus.mobile.repository.weather.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Coordonnees fixes du campus (memes que le cache backend cote Spring).
private const val CAMPUS_LAT = 44.808
private const val CAMPUS_LON = -0.595

/**
 * Etat + logique de l'ecran Meteo :
 * - selectedDate / selectedHourIndex : ce que l'utilisateur consulte (fleches jour/heure)
 * - hourlyPoints : 24 points (icone + temperature) pour la date selectionnee -> alimente la courbe
 * - airQualityDaily : moyenne du jour (affichee par defaut)
 * - airQualityHour : valeurs de l'heure cliquee (affichees des qu'une heure est selectionnee)
 */
class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val airQualityRepository: AirQualityRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _currentWeather = MutableStateFlow<CurrentWeatherDto?>(null)
    val currentWeather: StateFlow<CurrentWeatherDto?> = _currentWeather.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _hourlyPoints = MutableStateFlow<List<HourlyWeatherPointDto>>(emptyList())
    val hourlyPoints: StateFlow<List<HourlyWeatherPointDto>> = _hourlyPoints.asStateFlow()

    private val _selectedHourIndex = MutableStateFlow(LocalTime.now().hour)
    val selectedHourIndex: StateFlow<Int> = _selectedHourIndex.asStateFlow()

    private val _airQualityDaily = MutableStateFlow<AirQualityAtDto?>(null)
    val airQualityDaily: StateFlow<AirQualityAtDto?> = _airQualityDaily.asStateFlow()

    private val _airQualityHour = MutableStateFlow<AirQualityAtDto?>(null)
    val airQualityHour: StateFlow<AirQualityAtDto?> = _airQualityHour.asStateFlow()

    private val _showHourlyAirQuality = MutableStateFlow(false)
    val showHourlyAirQuality: StateFlow<Boolean> = _showHourlyAirQuality.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadInitial() {
        viewModelScope.launch {
            try {
                _currentWeather.value = weatherRepository.getCurrent()
            } catch (e: Exception) {
                _error.value = "Meteo actuelle indisponible"
            }
        }
        selectDate(LocalDate.now())
    }

    /** Fleches du haut : jour precedent / suivant. */
    fun shiftDay(deltaDays: Long) {
        selectDate(_selectedDate.value.plusDays(deltaDays))
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _showHourlyAirQuality.value = false
        loadHourlyFor(date)
        loadAirQualityDailyFor(date)
    }

    /** Fleches de l'echelle horaire : heure precedente / suivante. */
    fun shiftHour(delta: Int) {
        val newIndex = (_selectedHourIndex.value + delta).coerceIn(0, 23)
        selectHour(newIndex)
    }

    /** Tap direct sur une heure de l'echelle -> bascule le panneau qualite de l'air sur cette heure. */
    fun selectHour(hourIndex: Int) {
        _selectedHourIndex.value = hourIndex
        _showHourlyAirQuality.value = true
        loadAirQualityHourFor(_selectedDate.value, hourIndex)
    }

    private fun loadHourlyFor(date: LocalDate) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _hourlyPoints.value = if (date == LocalDate.now()) {
                    // Aujourd'hui : deja dispo en un seul appel (cache backend, 24 points).
                    weatherRepository.getHourlyToday()
                } else {
                    // Autre jour : pas d'endpoint "hourly" dedie cote backend pour une date
                    // arbitraire -> on reconstruit les 24 points via /weather/at (appels paralleles).
                    (0..23).map { hour ->
                        async {
                            try {
                                val result = weatherRepository.getWeatherAt(
                                    WeatherRequestDto(
                                        latitude = CAMPUS_LAT,
                                        longitude = CAMPUS_LON,
                                        date = date.format(dateFormatter),
                                        time = String.format("%02d:00:00", hour)
                                    )
                                )
                                HourlyWeatherPointDto(
                                    time = String.format("%02d:00", hour),
                                    temperature = result.temperature,
                                    weathercode = result.weathercode,
                                    description = result.description,
                                    icon = result.icon,
                                    precipitationProbability = result.precipitationProbability
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                _selectedHourIndex.value = if (date == LocalDate.now()) LocalTime.now().hour else 12
            } catch (e: Exception) {
                _error.value = "Donnees meteo indisponibles pour cette date"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadAirQualityDailyFor(date: LocalDate) {
        viewModelScope.launch {
            _airQualityDaily.value = try {
                airQualityRepository.getAirQualityAt(
                    WeatherRequestDto(
                        latitude = CAMPUS_LAT,
                        longitude = CAMPUS_LON,
                        date = date.format(dateFormatter)
                    )
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun loadAirQualityHourFor(date: LocalDate, hourIndex: Int) {
        viewModelScope.launch {
            _airQualityHour.value = try {
                airQualityRepository.getAirQualityAt(
                    WeatherRequestDto(
                        latitude = CAMPUS_LAT,
                        longitude = CAMPUS_LON,
                        date = date.format(dateFormatter),
                        time = String.format("%02d:00:00", hourIndex)
                    )
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}