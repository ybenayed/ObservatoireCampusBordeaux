package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.repository.weather.AirQualityRepository
import com.ObservatoireCampus.mobile.repository.weather.WeatherRepository
import com.ObservatoireCampus.mobile.ui.components.ErrorBanner
import com.ObservatoireCampus.mobile.ui.components.weather.AirQualityPanel
import com.ObservatoireCampus.mobile.ui.components.weather.CurrentDateTimeHeader
import com.ObservatoireCampus.mobile.ui.components.weather.HourlyWeatherScale
import com.ObservatoireCampus.mobile.ui.components.weather.TemperatureCurve
import com.ObservatoireCampus.mobile.ui.components.weather.WeatherBackgroundArt
import com.ObservatoireCampus.mobile.ui.components.weather.WeatherTopBar
import com.ObservatoireCampus.mobile.ui.components.weather.WeekDayNavigator
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.weather.WeatherViewModel
import com.ObservatoireCampus.mobile.viewmodel.weather.WeatherViewModelFactory

/**
 * Ecran Meteo complet. Assemble les petits composants de ui/components/weather :
 * nav du haut, illustration, navigation par jour, courbe + echelle horaire,
 * panneau qualite de l'air. Ne contient (volontairement) aucune logique detaillee,
 * juste l'agencement -> chaque brique reste modifiable independamment.
 */
@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(WeatherRepository(), AirQualityRepository())
    )
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val hourlyPoints by viewModel.hourlyPoints.collectAsState()
    val selectedHourIndex by viewModel.selectedHourIndex.collectAsState()
    val airQualityDaily by viewModel.airQualityDaily.collectAsState()
    val airQualityHour by viewModel.airQualityHour.collectAsState()
    val showHourlyAirQuality by viewModel.showHourlyAirQuality.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadInitial() }

    Column(modifier = Modifier.fillMaxSize()) {
        WeatherTopBar(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                WeatherBackgroundArt()
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CurrentDateTimeHeader()
                    Spacer(modifier = Modifier.height(4.dp))
                    WeekDayNavigator(
                        selectedDate = selectedDate,
                        onPrevious = { viewModel.shiftDay(-1) },
                        onNext = { viewModel.shiftDay(1) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (hourlyPoints.isNotEmpty()) {
                        TemperatureCurve(
                            points = hourlyPoints,
                            selectedIndex = selectedHourIndex,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            ErrorBanner(error = error, modifier = Modifier.padding(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObcampusPrimary)
                    .padding(vertical = 12.dp)
            ) {
                HourlyWeatherScale(
                    points = hourlyPoints,
                    selectedIndex = selectedHourIndex,
                    onHourSelected = { viewModel.selectHour(it) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AirQualityPanel(
                dailyData = airQualityDaily,
                hourData = airQualityHour,
                showHourly = showHourlyAirQuality,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}