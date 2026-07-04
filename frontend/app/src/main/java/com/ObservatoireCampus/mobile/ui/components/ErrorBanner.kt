package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Bandeau d'erreur. Ne s'affiche que si error != null.
 */
@Composable
fun ErrorBanner(error: String?, modifier: Modifier = Modifier) {
    if (error == null) return

    Surface(
        modifier = modifier,
        color = Color.Red.copy(alpha = 0.9f),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Erreur : $error",
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}