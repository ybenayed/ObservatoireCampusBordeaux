package com.ObservatoireCampus.mobile.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                onWeatherClick = { lat, lon ->
                    navController.navigate(Screen.Weather.buildRoute(lat, lon))
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
                onBack = { navController.popBackStack() },
                userLat = lat,
                userLon = lon
            )
        }
    }
}