package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary

/**
 * Habillage visuel de l'ecran Meteo.
 * - Zone agrandie (280dp au lieu de 200dp) pour une meilleure visibilite d'ensemble
 *   (date, navigation, courbe).
 * - Affiche la VRAIE icone meteo du moment (soleil / nuage / pluie / etc., deja fournie
 *   par le backend via WeatherCodeResolver -> icone OpenWeatherMap selon le weathercode),
 *   en grand, plutot que des icones generiques statiques.
 */
@Composable
fun WeatherBackgroundArt(
    currentIconUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Brush.verticalGradient(colors = listOf(ObcampusPrimary, Color(0xFF60A5FA))))
    ) {
        if (currentIconUrl != null) {
            AsyncImage(
                model = currentIconUrl,
                contentDescription = "Icone meteo actuelle",
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 20.dp)
            )
        } else {
            // Repli si l'icone n'est pas encore chargee
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 24.dp, top = 20.dp)
            )
        }
    }
}