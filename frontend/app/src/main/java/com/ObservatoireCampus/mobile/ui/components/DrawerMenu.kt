package com.ObservatoireCampus.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationTerDrawerSection
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.ui.theme.ObcampusSecondary
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel // AJOUT

private data class DrawerOption(
    val id: String, // Identification pour la logique interne
    val icon: ImageVector
)

// On utilise des ID stables au lieu de textes écrits en dur pour pouvoir les traduire à la volée
private val drawerOptions = listOf(
    DrawerOption("bornes_electriques", Icons.Default.EvStation),
    DrawerOption("meteo", Icons.Default.Cloud)
)

@Composable
fun DrawerMenu(
    languageViewModel: LanguageViewModel, // AJOUT de la gestion de langue
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
    stationTerLayers: List<LayerItemUiState>,
    stationTerMasterActive: Boolean,
    stationTerExpanded: Boolean,
    onStationTerExpandToggle: () -> Unit,
    onStationTerMasterToggle: () -> Unit,
    onStationTerItemToggle: (String) -> Unit,
    freeVehicleLayers: List<LayerItemUiState>,
    freeVehicleMasterActive: Boolean,
    freeVehicleExpanded: Boolean,
    onFreeVehicleExpandToggle: () -> Unit,
    onFreeVehicleMasterToggle: () -> Unit,
    onFreeVehicleItemToggle: (String) -> Unit,
    currentLanguage: AppLanguage,
    isTranslating: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    onWeatherClick: () -> Unit = {},
    onOptionClick: (String, Boolean) -> Unit = { _, _ -> },
    onBackToMap: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var activeStates by remember {
        mutableStateOf(drawerOptions.associate { it.id to false })
    }

    // Traductions des textes statiques du Drawer
    var translatedBornesLabel by remember { mutableStateOf("Bornes électriques") }
    var translatedMeteoLabel by remember { mutableStateOf("Météo") }
    var translatedLogoutLabel by remember { mutableStateOf("Déconnexion") }
    var translatedBackLabel by remember { mutableStateOf("Retour") }

    LaunchedEffect(currentLanguage) {
        translatedBornesLabel = languageViewModel.translate("Bornes électriques")
        translatedMeteoLabel = languageViewModel.translate("Météo")
        translatedLogoutLabel = languageViewModel.translate("Déconnexion")
        translatedBackLabel = languageViewModel.translate("Retour")
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
                Icon(Icons.Default.ArrowBack, contentDescription = translatedBackLabel)
            }
            Text(
                text = "OBCampus",
                style = MaterialTheme.typography.titleLarge,
                color = ObcampusPrimary
            )
        }

        HorizontalDivider()

        // Conteneur déroulant
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            // PARKING
            ParkingDrawerSection(
                items = parkingLayers,
                masterActive = parkingMasterActive,
                expanded = parkingExpanded,
                onExpandToggle = onParkingExpandToggle,
                onMasterToggle = onParkingMasterToggle,
                onItemToggle = onParkingItemToggle,
                languageViewModel = languageViewModel, // PASSAGE DE LA LANGUE
                currentLanguage = currentLanguage      // PASSAGE DE LA LANGUE
            )

            // BUS / TRAM
            StationTBDrawerSection(
                items = stationTBLayers,
                masterActive = stationTBMasterActive,
                expanded = stationTBExpanded,
                onExpandToggle = onStationTBExpandToggle,
                onMasterToggle = onStationTBMasterToggle,
                onItemToggle = onStationTBItemToggle,
                languageViewModel = languageViewModel, // PASSAGE DE LA LANGUE
                currentLanguage = currentLanguage      // PASSAGE DE LA LANGUE
            )

            // VELO
            StationVDrawerSection(
                items = stationVLayers,
                masterActive = stationVMasterActive,
                expanded = stationVExpanded,
                onExpandToggle = onStationVExpandToggle,
                onMasterToggle = onStationVMasterToggle,
                onItemToggle = onStationVItemToggle,
                languageViewModel = languageViewModel, // PASSAGE DE LA LANGUE
                currentLanguage = currentLanguage      // PASSAGE DE LA LANGUE
            )

            // TER
            StationTerDrawerSection(
                items = stationTerLayers,
                masterActive = stationTerMasterActive,
                expanded = stationTerExpanded,
                onExpandToggle = onStationTerExpandToggle,
                onMasterToggle = onStationTerMasterToggle,
                onItemToggle = onStationTerItemToggle,
                languageViewModel = languageViewModel, // PASSAGE DE LA LANGUE
                currentLanguage = currentLanguage      // PASSAGE DE LA LANGUE
            )

            // LIBRE-SERVICE
            FreeVehicleDrawerSection(
                items = freeVehicleLayers,
                masterActive = freeVehicleMasterActive,
                expanded = freeVehicleExpanded,
                onExpandToggle = onFreeVehicleExpandToggle,
                onMasterToggle = onFreeVehicleMasterToggle,
                onItemToggle = onFreeVehicleItemToggle,
                languageViewModel = languageViewModel, // PASSAGE DE LA LANGUE
                currentLanguage = currentLanguage      // PASSAGE DE LA LANGUE
            )

            // AUTRES OPTIONS - Statiques et Traduisibles
            drawerOptions.forEach { option ->
                val isActive = activeStates[option.id] == true

                // Association de l'id d'option avec le label traduit
                val labelText = if (option.id == "bornes_electriques") translatedBornesLabel else translatedMeteoLabel

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
                            if (option.id == "meteo") {
                                onWeatherClick()
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = labelText,
                            tint = if (isActive) ObcampusPrimary else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = labelText,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (option.id != "meteo") {
                        IconButton(
                            onClick = {
                                val newState = !(activeStates[option.id] ?: false)
                                activeStates = activeStates.toMutableMap().apply {
                                    put(option.id, newState)
                                }
                                onOptionClick(option.id, newState)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isActive) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = "toggle"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(28.dp))
                    }
                }
            }
        }

        // --- SECTION BASSE (STATIQUE, TOUJOURS VISIBLE) ---
        HorizontalDivider()

        // 1. SECTION LANGUE
        LanguageDrawerSection(
            languageViewModel = languageViewModel, // <-- AJOUT de la transmission ici !
            currentLanguage = currentLanguage,
            isTranslating = isTranslating,
            onLanguageSelected = onLanguageSelected
        )

        HorizontalDivider()

        // 2. BOUTON DE DECONNEXION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = translatedLogoutLabel, tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = translatedLogoutLabel, color = Color.Red, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = "logout", tint = Color.Gray)
        }
    }
}