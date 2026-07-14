package com.ObservatoireCampus.mobile.ui.components.navigation

/**
 * Routes de l'application. Fichier dedie (separe des ecrans) pour garder
 * la navigation modulaire, comme demande.
 */
sealed class Screen(val route: String) {
    object Map : Screen("map")

    // Route parametree : lat/lon optionnels (position utilisateur transmise depuis MapScreen).
    // Pattern route brut pour le NavHost -> declaration des placeholders {lat} et {lon}.
    object Weather : Screen("weather?lat={lat}&lon={lon}") {
        // Construit la route reelle a naviguer, avec les valeurs (ou vide si absentes).
        fun buildRoute(lat: Double?, lon: Double?): String {
            val latParam = lat?.toString() ?: ""
            val lonParam = lon?.toString() ?: ""
            return "weather?lat=$latParam&lon=$lonParam"
        }
    }
}