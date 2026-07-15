package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Tram
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Train


object StationTypeStyle {
    fun color(key: String): Color = when (key) {
        "TRAM" -> Color(0xFF1976D2)
        "BUS" -> Color(0xFFEF6C00)
        "VELO" -> Color(0xFF2E7D32)
        "TER" -> Color(0xFF6A1B9A)   // violet SNCF
        else -> Color.Gray
    }

    fun icon(key: String): ImageVector = when (key) {
        "TRAM" -> Icons.Default.Tram
        "BUS" -> Icons.Default.DirectionsBus
        "VELO" -> Icons.Default.DirectionsBike
        "TER" -> Icons.Default.Train
        else -> Icons.Default.Place
    }

    fun label(key: String): String = when (key) {
        "TRAM" -> "Tram"
        "BUS" -> "Bus"
        "VELO" -> "Velo"
        "TER" -> "TER"
        else -> key
    }

    fun markerLetter(key: String): String = when (key) {
        "TRAM" -> "T"
        "BUS" -> "B"
        "VELO" -> "V"
        "TER" -> "R"   // R pour "Rail" (T est déjà pris par Tram)
        else -> "?"
    }
}