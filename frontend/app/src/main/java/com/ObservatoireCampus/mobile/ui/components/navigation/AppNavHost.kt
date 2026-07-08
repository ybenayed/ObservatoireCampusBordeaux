package com.ObservatoireCampus.mobile.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ObservatoireCampus.mobile.ui.screens.MapScreen
import com.ObservatoireCampus.mobile.ui.screens.WeatherScreen

/**
 * Point d'entree unique de la navigation. A appeler depuis MainActivity :
 *
 *   setContent { AppNavHost() }
 *
 * (remplace un eventuel appel direct a MapScreen()).
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Map.route) {
        composable(Screen.Map.route) {
            MapScreen(
                onWeatherClick = { navController.navigate(Screen.Weather.route) }
            )
        }
        composable(Screen.Weather.route) {
            WeatherScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}