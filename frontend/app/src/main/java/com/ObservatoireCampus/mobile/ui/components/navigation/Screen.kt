package com.ObservatoireCampus.mobile.ui.components.navigation

/**
 * Routes de l'application. Fichier dedie (separe des ecrans) pour garder
 * la navigation modulaire, comme demande.
 */
sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Weather : Screen("weather")
}