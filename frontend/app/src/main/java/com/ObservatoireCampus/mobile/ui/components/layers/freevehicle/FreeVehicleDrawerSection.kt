package com.ObservatoireCampus.mobile.ui.components.layers.freevehicle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.runtime.*
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage // AJOUT
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel // AJOUT

@Composable
fun FreeVehicleDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit,
    languageViewModel: LanguageViewModel, // AJOUT
    currentLanguage: AppLanguage           // AJOUT
) {
    var translatedTitle by remember { mutableStateOf("Libre-service") }
    var translatedItemLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(currentLanguage, items) {
        translatedTitle = languageViewModel.translate("Libre-service")

        val newLabels = items.associate { item ->
            item.key to FreeVehicleTypeStyle.label(item.key, languageViewModel)
        }
        translatedItemLabels = newLabels
    }

    LayerSection(
        label = translatedTitle,
        icon = Icons.Default.ElectricScooter,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemColor = { FreeVehicleTypeStyle.color(it) },
        itemIcon = { FreeVehicleTypeStyle.icon(it) },
        itemLabel = { key -> translatedItemLabels[key] ?: key }
    )
}