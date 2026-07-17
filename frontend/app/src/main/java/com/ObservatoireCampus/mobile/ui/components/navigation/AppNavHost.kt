package com.ObservatoireCampus.mobile.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ObservatoireCampus.mobile.ui.screens.MapScreen
import com.ObservatoireCampus.mobile.ui.screens.WeatherScreen
import com.ObservatoireCampus.mobile.ui.screens.InternshipScreen // Import de l'écran d'informations de stage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun AppNavHost(
    languageViewModel: LanguageViewModel,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Map.route) {
        composable(Screen.Map.route) {
            MapScreen(
                languageViewModel = languageViewModel,
                onWeatherClick = { lat, lon ->
                    navController.navigate(Screen.Weather.buildRoute(lat, lon))
                },
                onInternshipClick = {
                    // Navigation directe vers l'écran "À propos"
                    navController.navigate(Screen.Internship.route)
                }
            )
        }

        composable(
            route = Screen.Weather.route,
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("lon") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()

            WeatherScreen(
                languageViewModel = languageViewModel,
                onBack = { navController.popBackStack() },
                userLat = lat,
                userLon = lon
            )
        }

        // AJOUT : Écran "À propos" (InternshipScreen)
        composable(Screen.Internship.route) {
            InternshipScreen(
                languageViewModel = languageViewModel,
                onBack = { navController.popBackStack() } // Retourne à la carte
            )
        }
    }
}