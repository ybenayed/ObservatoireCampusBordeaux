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
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel // <-- AJOUT

/**
 * Point d'entree unique de la navigation. A appeler depuis MainActivity :
 *
 *   setContent {
 *       val languageViewModel: LanguageViewModel = viewModel()
 *       AppNavHost(languageViewModel = languageViewModel)
 *   }
 *
 * (remplace un eventuel appel direct a MapScreen()).
 */
@Composable
fun AppNavHost(
    languageViewModel: LanguageViewModel, // <-- AJOUT du ViewModel de langue partagé
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Map.route) {
        composable(Screen.Map.route) {
            MapScreen(
                languageViewModel = languageViewModel, // <-- AJOUT de la transmission de l'instance
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

            // Si jamais WeatherScreen a lui aussi besoin des traductions,
            // vous pourrez lui passer `languageViewModel` de la même manière ici.
            WeatherScreen(
                languageViewModel = languageViewModel,
                onBack = { navController.popBackStack() },
                userLat = lat,
                userLon = lon
            )
        }
    }
}