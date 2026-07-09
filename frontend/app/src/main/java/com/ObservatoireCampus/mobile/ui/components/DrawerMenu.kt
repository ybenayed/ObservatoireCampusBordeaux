package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ObservatoireCampus.mobile.model.layers.LayerItemUiState
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationTBDrawerSection
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationVDrawerSection
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusSecondary
import androidx.compose.foundation.clickable

private data class DrawerOption(
    val label: String,
    val icon: ImageVector
)

private val drawerOptions = listOf(
    DrawerOption("Bornes electriques", Icons.Default.EvStation),
    DrawerOption("Meteo", Icons.Default.Cloud)
)

@Composable
fun DrawerMenu(
    parkingLayers: List<LayerItemUiState>,
    parkingMasterActive: Boolean,
    parkingExpanded: Boolean,
    onParkingExpandToggle: () -> Unit,
    onParkingMasterToggle: () -> Unit,
    onParkingItemToggle: (String) -> Unit,
    stationTBLayers: List<LayerItemUiState>,
    stationTBMasterActive: Boolean,
    stationTBExpanded: Boolean,
    onStationTBExpandToggle: () -> Unit,
    onStationTBMasterToggle: () -> Unit,
    onStationTBItemToggle: (String) -> Unit,
    stationVLayers: List<LayerItemUiState>,
    stationVMasterActive: Boolean,
    stationVExpanded: Boolean,
    onStationVExpandToggle: () -> Unit,
    onStationVMasterToggle: () -> Unit,
    onStationVItemToggle: (String) -> Unit,
    freeVehicleLayers: List<LayerItemUiState>,
    freeVehicleMasterActive: Boolean,
    freeVehicleExpanded: Boolean,
    onFreeVehicleExpandToggle: () -> Unit,
    onFreeVehicleMasterToggle: () -> Unit,
    onFreeVehicleItemToggle: (String) -> Unit,
    onWeatherClick: () -> Unit = {},
    onOptionClick: (String, Boolean) -> Unit = { _, _ -> },
    onBackToMap: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var activeStates by remember {
        mutableStateOf(drawerOptions.associate { it.label to false })
    }

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.69f)
            .background(Color.White)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackToMap) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "OBCampus",
                style = MaterialTheme.typography.titleLarge,
                color = ObcampusPrimary
            )
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        // PARKING - layer dynamique
        ParkingDrawerSection(
            items = parkingLayers,
            masterActive = parkingMasterActive,
            expanded = parkingExpanded,
            onExpandToggle = onParkingExpandToggle,
            onMasterToggle = onParkingMasterToggle,
            onItemToggle = onParkingItemToggle
        )

        // BUS / TRAM - layer dynamique
        StationTBDrawerSection(
            items = stationTBLayers,
            masterActive = stationTBMasterActive,
            expanded = stationTBExpanded,
            onExpandToggle = onStationTBExpandToggle,
            onMasterToggle = onStationTBMasterToggle,
            onItemToggle = onStationTBItemToggle
        )

        // VELO - layer dynamique
        StationVDrawerSection(
            items = stationVLayers,
            masterActive = stationVMasterActive,
            expanded = stationVExpanded,
            onExpandToggle = onStationVExpandToggle,
            onMasterToggle = onStationVMasterToggle,
            onItemToggle = onStationVItemToggle
        )

        // LIBRE-SERVICE - layer dynamique
        FreeVehicleDrawerSection(
            items = freeVehicleLayers,
            masterActive = freeVehicleMasterActive,
            expanded = freeVehicleExpanded,
            onExpandToggle = onFreeVehicleExpandToggle,
            onMasterToggle = onFreeVehicleMasterToggle,
            onItemToggle = onFreeVehicleItemToggle
        )

        // AUTRES OPTIONS - Statiques
        drawerOptions.forEach { option ->
            val isActive = activeStates[option.label] == true

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isActive) ObcampusSecondary.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clickable {
                        if (option.label == "Meteo") {
                            onWeatherClick()
                        }
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically, // Aligne verticalement l'icône, le texte et l'œil au milieu
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp) // Repousse l'œil tout à la fin
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.label,
                        tint = if (isActive) ObcampusPrimary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option.label,
                        maxLines = 2, // Autorise le nom à s'afficher sur deux lignes maximum
                        modifier = Modifier.weight(1f) // Prend le maximum de place et pousse le reste vers le bout
                    )
                }

                if (option.label != "Meteo") {
                    IconButton(
                        onClick = {
                            val newState = !(activeStates[option.label] ?: false)
                            activeStates = activeStates.toMutableMap().apply {
                                put(option.label, newState)
                            }
                            onOptionClick(option.label, newState)
                        },
                        modifier = Modifier.size(28.dp) // Taille fixe pour l'œil
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = "toggle"
                        )
                    }
                } else {
                    // Spacer pour maintenir l'alignement même si l'icône météo n'a pas d'œil
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()

        // LOGOUT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Deconnexion", color = Color.Red)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ArrowForward, contentDescription = "logout")
            }
        }
    }
}