package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.runtime.*
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@Composable
fun StationTBDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage
) {
    var translatedTitle by remember { mutableStateOf("Bus & Tram") }
    var translatedItemLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage, items) {
        translatedTitle = languageViewModel.translate("Bus & Tram")

        // Correction : On résout les fonctions suspendues correctement à l'intérieur du LaunchedEffect
        val newLabels = items.associate { item ->
            item.key to StationTypeStyle.label(item.key, languageViewModel)
        }
        translatedItemLabels = newLabels
    }

    LayerSection(
        label = translatedTitle,
        icon = Icons.Default.DirectionsBus,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemColor = { StationTypeStyle.color(it) },
        itemIcon = { StationTypeStyle.icon(it) },
        itemLabel = { key -> translatedItemLabels[key] ?: key }
    )
}

@Composable
fun StationVDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit,
    languageViewModel: LanguageViewModel,
    currentLanguage: AppLanguage
) {
    // Initialisation avec "Vélo" par défaut
    var translatedTitle by remember { mutableStateOf("Vélo") } // <-- Correction de l'orthographe
    var translatedItemLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage, items) {
        translatedTitle = languageViewModel.translate("Vélo") // <-- Envoi de "Vélo" pour traduction

        val newLabels = items.associate { item ->
            item.key to StationTypeStyle.label(item.key, languageViewModel)
        }
        translatedItemLabels = newLabels
    }

    LayerSection(
        label = translatedTitle,
        icon = Icons.Default.DirectionsBike,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemColor = { StationTypeStyle.color(it) },
        itemIcon = { StationTypeStyle.icon(it) },
        itemLabel = { key -> translatedItemLabels[key] ?: key }
    )
}