package com.ObservatoireCampus.mobile.ui.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary

/**
 * Habillage visuel (degrade + icones) pour identifier immediatement la page
 * comme une page "Meteo". Simple et leger : a remplacer par une vraie illustration
 * (drawable/svg) plus tard si besoin, sans toucher au reste de l'ecran.
 */
@Composable
fun WeatherBackgroundArt(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Brush.verticalGradient(colors = listOf(ObcampusPrimary, Color(0xFF60A5FA))))
    ) {
        Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.35f),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 12.dp)
        )
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 36.dp)
        )
    }
}