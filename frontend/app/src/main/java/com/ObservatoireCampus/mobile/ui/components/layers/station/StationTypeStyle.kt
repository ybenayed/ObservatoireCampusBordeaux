package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Tram
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object StationTypeStyle {
    fun color(key: String): Color = when (key) {
        "TRAM" -> Color(0xFF1976D2)
        "BUS" -> Color(0xFFEF6C00)
        "VELO" -> Color(0xFF2E7D32)
        else -> Color.Gray
    }

    fun icon(key: String): ImageVector = when (key) {
        "TRAM" -> Icons.Default.Tram
        "BUS" -> Icons.Default.DirectionsBus
        "VELO" -> Icons.Default.DirectionsBike
        else -> Icons.Default.Place
    }

    fun label(key: String): String = when (key) {
        "TRAM" -> "Tram"
        "BUS" -> "Bus"
        "VELO" -> "Velo"
        else -> key
    }
    fun markerLetter(key: String): String = when (key) {
        "TRAM" -> "T"
        "BUS" -> "B"
        "VELO" -> "V"
        else -> "?"
    }
}