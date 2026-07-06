package com.ObservatoireCampus.mobile.ui.components.layers.freevehicle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// vehicleTypeId vient directement du backend (GBFS RideYeGo) : yego_scooter, yego_bike, yego_kick
object FreeVehicleTypeStyle {

    fun color(vehicleTypeId: String): Color = when (vehicleTypeId) {
        "yego_scooter" -> Color(0xFF8E24AA) // violet - scooter/moped
        "yego_bike" -> Color(0xFF2E7D32)    // vert - velo
        "yego_kick" -> Color(0xFF0288D1)    // bleu - trottinette
        else -> Color.Gray
    }

    fun icon(vehicleTypeId: String): ImageVector = when (vehicleTypeId) {
        "yego_scooter" -> Icons.Default.ElectricMoped
        "yego_bike" -> Icons.Default.DirectionsBike
        "yego_kick" -> Icons.Default.ElectricScooter
        else -> Icons.Default.Place
    }

    fun label(vehicleTypeId: String): String = when (vehicleTypeId) {
        "yego_scooter" -> "Scooter électrique"
        "yego_bike" -> "Vélo électrique"
        "yego_kick" -> "Trottinette électrique"
        else -> vehicleTypeId
    }
}