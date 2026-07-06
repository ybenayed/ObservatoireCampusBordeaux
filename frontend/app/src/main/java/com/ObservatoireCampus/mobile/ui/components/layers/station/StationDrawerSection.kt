package com.ObservatoireCampus.mobile.ui.components.layers.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.runtime.Composable
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection

@Composable
fun StationTBDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit
) {
    LayerSection(
        label = "Bus & Tram",
        icon = Icons.Default.DirectionsBus,
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

@Composable
fun StationVDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit
) {
    LayerSection(
        label = "Velo",
        icon = Icons.Default.DirectionsBike,
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