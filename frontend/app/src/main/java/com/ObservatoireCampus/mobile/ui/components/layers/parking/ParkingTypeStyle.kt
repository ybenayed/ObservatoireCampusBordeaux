package com.ObservatoireCampus.mobile.ui.components.layers.parking

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mapping dynamique taType -> style visuel (icone, couleur, label lisible, lettre pour la carte).
 * Base sur des mots-cles pour rester generique meme si de nouveaux taType apparaissent.
 */
object ParkingTypeStyle {

    fun label(taType: String): String =
        taType.replace("_", " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    fun icon(taType: String): ImageVector = when {
        "PARC_RELAIS" in taType -> Icons.Default.DirectionsCar
        "GRATUIT" in taType -> Icons.Default.MoneyOff
        "RESERVE" in taType -> Icons.Default.Lock
        else -> Icons.Default.LocalParking
    }

    fun color(taType: String): Color = when {
        "PARC_RELAIS" in taType -> Color(0xFF16A34A) // vert
        "GRATUIT" in taType -> Color(0xFF0EA5E9)      // bleu clair
        "RESERVE" in taType -> Color(0xFFF59E0B)      // orange
        else -> Color(0xFF2563EB)                     // bleu (parking classique)
    }

    fun markerLetter(taType: String): String = when {
        "PARC_RELAIS" in taType -> "PR"
        "GRATUIT" in taType -> "PG"
        "RESERVE" in taType -> "PR"
        else -> "P"
    }
}