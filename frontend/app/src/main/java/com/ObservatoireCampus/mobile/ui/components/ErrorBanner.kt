package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

/**
 * Bandeau d'erreur traduit dynamiquement.
 */
@Composable
fun ErrorBanner(
    error: String?,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    if (error == null) return

    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    var translatedPrefix by remember { mutableStateOf("Erreur : ") }

    LaunchedEffect(currentLanguage) {
        translatedPrefix = languageViewModel.translate("Erreur : ")
    }

    Surface(
        modifier = modifier,
        color = Color.Red.copy(alpha = 0.9f),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "$translatedPrefix$error",
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}