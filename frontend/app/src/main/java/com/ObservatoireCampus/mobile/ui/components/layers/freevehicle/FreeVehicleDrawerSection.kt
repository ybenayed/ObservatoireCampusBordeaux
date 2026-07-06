package com.ObservatoireCampus.mobile.ui.components.layers.freevehicle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.runtime.Composable
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.LayerSection

@Composable
fun FreeVehicleDrawerSection(
    items: List<LayerItemUiState>,
    masterActive: Boolean,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onItemToggle: (String) -> Unit
) {
    LayerSection(
        label = "Libre-service",
        icon = Icons.Default.ElectricScooter,
        items = items,
        masterActive = masterActive,
        expanded = expanded,
        onExpandToggle = onExpandToggle,
        onMasterToggle = onMasterToggle,
        onItemToggle = onItemToggle,
        itemColor = { FreeVehicleTypeStyle.color(it) },
        itemIcon = { FreeVehicleTypeStyle.icon(it) },
        itemLabel = { FreeVehicleTypeStyle.label(it) }
    )
}