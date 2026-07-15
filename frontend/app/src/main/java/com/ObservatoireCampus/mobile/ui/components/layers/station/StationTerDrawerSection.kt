package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Train
import androidx.compose.runtime.Composable
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection

@Composable
fun StationTerDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit
) {
    LayerSection(
        label = "TER",
        icon = Icons.Default.Train,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemColor = { StationTypeStyle.color(it) },
        itemIcon = { StationTypeStyle.icon(it) },
        itemLabel = { StationTypeStyle.label(it) }
    )
}