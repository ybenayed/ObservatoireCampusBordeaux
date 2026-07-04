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
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingDrawerSection
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusSecondary

private data class DrawerOption(
    val label: String,
    val icon: ImageVector
)

// "Parking" retire d'ici - gere maintenant par ParkingDrawerSection (dynamique)
private val drawerOptions = listOf(
    DrawerOption("Vélo", Icons.Default.DirectionsBike),
    DrawerOption("Trottinette", Icons.Default.ElectricScooter),
    DrawerOption("Bus", Icons.Default.DirectionsBus),
    DrawerOption("Tram", Icons.Default.Tram),
    DrawerOption("Bornes électriques", Icons.Default.EvStation),
    DrawerOption("Météo", Icons.Default.Cloud)
)

@Composable
fun DrawerMenu(
    parkingLayers: List<LayerItemUiState>,
    parkingMasterActive: Boolean,
    parkingExpanded: Boolean,
    onParkingExpandToggle: () -> Unit,
    onParkingMasterToggle: () -> Unit,
    onParkingItemToggle: (String) -> Unit,
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
            .fillMaxWidth(0.66f)
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

        Divider()
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

        // AUTRES OPTIONS - toujours statiques pour l'instant
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
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.label,
                        tint = if (isActive) ObcampusPrimary else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = option.label)
                }

                IconButton(
                    onClick = {
                        val newState = !(activeStates[option.label] ?: false)
                        activeStates = activeStates.toMutableMap().apply {
                            put(option.label, newState)
                        }
                        onOptionClick(option.label, newState)
                    }
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = "toggle"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Divider()

        // LOGOUT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Déconnexion", color = Color.Red)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ArrowForward, contentDescription = "logout")
            }
        }
    }
}