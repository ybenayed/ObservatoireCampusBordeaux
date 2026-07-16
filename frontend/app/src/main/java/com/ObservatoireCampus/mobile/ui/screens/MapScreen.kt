package com.ObservatoireCampus.mobile.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.repository.ParkingRepository
import com.ObservatoireCampus.mobile.repository.station.StationTBRepository
import com.ObservatoireCampus.mobile.repository.station.StationVRepository
import com.ObservatoireCampus.mobile.repository.station.StationTerRepository
import com.ObservatoireCampus.mobile.ui.components.CampusButton
import com.ObservatoireCampus.mobile.ui.components.LocationButton
import com.ObservatoireCampus.mobile.ui.components.CampusMap
import com.ObservatoireCampus.mobile.ui.components.DrawerMenu
import com.ObservatoireCampus.mobile.ui.components.ErrorBanner
import com.ObservatoireCampus.mobile.ui.components.SearchBar
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.ZoomControls
import com.ObservatoireCampus.mobile.ui.components.weather.CurrentWeatherBadge
import com.ObservatoireCampus.mobile.ui.components.station.StationTBBubble
import com.ObservatoireCampus.mobile.ui.components.station.StationVBubble
import com.ObservatoireCampus.mobile.ui.components.station.StationTerBubble
import com.ObservatoireCampus.mobile.ui.components.parking.ParkingBubble
import com.ObservatoireCampus.mobile.ui.components.location.LocationBubble
import com.ObservatoireCampus.mobile.ui.components.location.upsertUserLocationMarker
import com.ObservatoireCampus.mobile.ui.components.location.removeUserLocationMarker
import com.ObservatoireCampus.mobile.ui.theme.ObcampusBackground
import com.ObservatoireCampus.mobile.viewmodel.MapViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationTBViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationTBViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationVViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationVViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationTerViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationTerViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.freevehicle.FreeVehicleViewModel
import com.ObservatoireCampus.mobile.viewmodel.freevehicle.FreeVehicleViewModelFactory
import com.ObservatoireCampus.mobile.repository.freevehicle.FreeVehicleRepository
import com.ObservatoireCampus.mobile.ui.components.freevehicle.FreeVehicleBubble
import androidx.compose.ui.platform.LocalContext
import com.ObservatoireCampus.mobile.viewmodel.location.LocationViewModel
import com.ObservatoireCampus.mobile.viewmodel.location.LocationViewModelFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import com.ObservatoireCampus.mobile.viewmodel.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    languageViewModel: LanguageViewModel, // <-- Instance partagée reçue[cite: 5]
    onWeatherClick: (Double?, Double?) -> Unit = { _, _ -> } // <--[cite: 5]
) {
    val campusList by viewModel.campusList.collectAsState() // <--[cite: 5]
    val campusError by viewModel.error.collectAsState() // <--[cite: 5]

    val parkingViewModel: ParkingViewModel = viewModel( // <--[cite: 5]
        factory = ParkingViewModelFactory(ParkingRepository()) // <--[cite: 5]
    )
    val parkingLayers by parkingViewModel.parkingLayers.collectAsState() // <--[cite: 5]
    val visibleParking by parkingViewModel.visiblePositions.collectAsState() // <--[cite: 5]
    val parkingError by parkingViewModel.error.collectAsState() // <--[cite: 5]
    val selectedParkingId by parkingViewModel.selectedParkingId.collectAsState() // <--[cite: 5]
    val selectedParkingStatus by parkingViewModel.selectedParkingStatus.collectAsState() // <--[cite: 5]
    val bubbleLoadingParking by parkingViewModel.bubbleLoading.collectAsState() // <--[cite: 5]
    var parkingExpanded by remember { mutableStateOf(false) } // <--[cite: 5]

    // Bus / Tram
    val stationTBViewModel: StationTBViewModel = viewModel( // <--[cite: 5]
        factory = StationTBViewModelFactory(StationTBRepository()) // <--[cite: 5]
    )
    val stationTBLayers by stationTBViewModel.layers.collectAsState() // <--[cite: 5]
    val visibleStationsTB by stationTBViewModel.visiblePositions.collectAsState() // <--[cite: 5]
    val selectedStationTB by stationTBViewModel.selectedStation.collectAsState() // <--[cite: 5]
    val passagesTB by stationTBViewModel.passages.collectAsState() // <--[cite: 5]
    val bubbleLoadingTB by stationTBViewModel.bubbleLoading.collectAsState() // <--[cite: 5]
    val stationTBError by stationTBViewModel.error.collectAsState() // <--[cite: 5]
    var stationTBExpanded by remember { mutableStateOf(false) } // <--[cite: 5]

    // Velo
    val stationVViewModel: StationVViewModel = viewModel( // <--[cite: 5]
        factory = StationVViewModelFactory(StationVRepository()) // <--[cite: 5]
    )
    val stationVLayers by stationVViewModel.layers.collectAsState() // <--[cite: 5]
    val visibleStationsV by stationVViewModel.visiblePositions.collectAsState() // <--[cite: 5]
    val selectedStationVDetail by stationVViewModel.selectedDetail.collectAsState() // <--[cite: 5]
    val bubbleLoadingV by stationVViewModel.bubbleLoading.collectAsState() // <--[cite: 5]
    val stationVError by stationVViewModel.error.collectAsState() // <--[cite: 5]
    var stationVExpanded by remember { mutableStateOf(false) } // <--[cite: 5]

    // TER
    val stationTerViewModel: StationTerViewModel = viewModel( // <--[cite: 5]
        factory = StationTerViewModelFactory(StationTerRepository()) // <--[cite: 5]
    )
    val stationTerLayers by stationTerViewModel.layers.collectAsState() // <--[cite: 5]
    val visibleStationsTer by stationTerViewModel.visiblePositions.collectAsState() // <--[cite: 5]
    val selectedStationTer by stationTerViewModel.selectedStation.collectAsState() // <--[cite: 5]
    val passagesTer by stationTerViewModel.passages.collectAsState() // <--[cite: 5]
    val bubbleLoadingTer by stationTerViewModel.bubbleLoading.collectAsState() // <--[cite: 5]
    val stationTerError by stationTerViewModel.error.collectAsState() // <--[cite: 5]
    var stationTerExpanded by remember { mutableStateOf(false) } // <--[cite: 5]

    // Free vehicle
    val freeVehicleViewModel: FreeVehicleViewModel = viewModel( // <--[cite: 5]
        factory = FreeVehicleViewModelFactory(FreeVehicleRepository()) // <--[cite: 5]
    )
    val freeVehicleLayers by freeVehicleViewModel.layers.collectAsState() // <--[cite: 5]
    val visibleFreeVehicles by freeVehicleViewModel.visiblePositions.collectAsState() // <--[cite: 5]
    val freeVehicleError by freeVehicleViewModel.error.collectAsState() // <--[cite: 5]
    val selectedFreeVehicleId by freeVehicleViewModel.selectedVehicleId.collectAsState() // <--[cite: 5]
    val selectedFreeVehicle by freeVehicleViewModel.selectedVehicle.collectAsState() // <--[cite: 5]
    val bubbleLoadingFV by freeVehicleViewModel.bubbleLoading.collectAsState() // <--[cite: 5]
    var freeVehicleExpanded by remember { mutableStateOf(false) } // <--[cite: 5]

    // Localisation utilisateur
    val context = LocalContext.current // <--[cite: 5]
    val locationViewModel: LocationViewModel = viewModel( // <--[cite: 5]
        factory = LocationViewModelFactory(LocationServices.getFusedLocationProviderClient(context)) // <--[cite: 5]
    )
    val userLocation by locationViewModel.userLocation.collectAsState() // <--[cite: 5]
    val locationBubbleVisible by locationViewModel.bubbleVisible.collectAsState() // <--[cite: 5]
    val locationAccuracy by locationViewModel.accuracyMeters.collectAsState() // <--[cite: 5]
    val locationState by locationViewModel.locationState.collectAsState() // <--[cite: 5]

    // Langue (Obtenue depuis les paramètres de la fonction)
    val currentLanguage by languageViewModel.currentLanguage.collectAsState() // <--[cite: 5]
    val isTranslating by languageViewModel.isTranslating.collectAsState() // <--[cite: 5]
    val languageError by languageViewModel.error.collectAsState() // <--[cite: 5]

    // --- TRADUCTION DE LA LOCALISATION ET DES CAMPUS ---
    var displayedCampusList by remember { mutableStateOf(campusList) } // <--[cite: 5]
    var translatedUserLocationPinTitle by remember { mutableStateOf("Ma position") } // <-- AJOUTÉ

    LaunchedEffect(currentLanguage, campusList) { // <--[cite: 5]
        Log.d("OBS_CAMPUS", "--- UPDATE LANGUE ---") // <--[cite: 5]
        Log.d("OBS_CAMPUS", "Langue actuelle détectée dans MapScreen : $currentLanguage") // <--[cite: 5]
        Log.d("OBS_CAMPUS", "Nombre de campus dans campusList d'origine : ${campusList.size}") // <--[cite: 5]

        // Traduction du titre du pin
        translatedUserLocationPinTitle = languageViewModel.translate("Ma position") // <-- AJOUTÉ

        displayedCampusList = if (currentLanguage == AppLanguage.FR) { // <--[cite: 5]
            Log.d("OBS_CAMPUS", "La langue est FR. Pas de traduction nécessaire. Retour campus d'origine.") // <--[cite: 5]
            campusList // <--[cite: 5]
        } else {
            Log.d("OBS_CAMPUS", "La langue n'est pas FR (Langue actuelle : $currentLanguage). Traduction en cours...") // <--[cite: 5]
            campusList.map { campus -> // <--[cite: 5]
                val nomTraduit = languageViewModel.translate(campus.name) // <--[cite: 5]
                Log.d("OBS_CAMPUS", "Traduction campus : '${campus.name}' -> '$nomTraduit'") // <--[cite: 5]
                campus.copy(name = nomTraduit) // <--[cite: 5]
            }
        }
    }

    // Une seule zone d'erreur pour tout l'ecran : on combine les sources
    val combinedError = // <--[cite: 5]
        listOfNotNull( // <--[cite: 5]
            campusError, parkingError, stationTBError, stationVError, // <--[cite: 5]
            stationTerError, freeVehicleError, languageError // <--[cite: 5]
        ) // <--[cite: 5]
            .takeIf { it.isNotEmpty() } // <--[cite: 5]
            ?.joinToString(" | ") // <--[cite: 5]

    val drawerState = rememberDrawerState(DrawerValue.Closed) // <--[cite: 5]
    val scope = rememberCoroutineScope() // <--[cite: 5]
    var mapView by remember { mutableStateOf<MapView?>(null) } // <--[cite: 5]
    var showCampus by remember { mutableStateOf(false) } // <--[cite: 5]

    // Référence du marqueur "Ma position" pour le déplacer sans le recréer
    var userLocationMarker by remember { mutableStateOf<Marker?>(null) } // <--[cite: 5]

    LaunchedEffect(Unit) { // <--[cite: 5]
        if (campusList.isEmpty()) viewModel.loadCampus() // <--[cite: 5]
        parkingViewModel.loadParking() // <--[cite: 5]
        stationTBViewModel.loadStations() // <--[cite: 5]
        stationVViewModel.loadStations() // <--[cite: 5]
        stationTerViewModel.loadStations() // <--[cite: 5]
        freeVehicleViewModel.loadStations() // <--[cite: 5]
    }

    // Dessine / met à jour le marqueur avec la traduction appropriée
    LaunchedEffect(userLocation, mapView, translatedUserLocationPinTitle) { // <-- AJOUT de la dépendance de traduction
        val currentMapView = mapView ?: return@LaunchedEffect // <--[cite: 5]
        val point = userLocation // <--[cite: 5]

        if (point != null) { // <--[cite: 5]
            userLocationMarker = upsertUserLocationMarker( // <--[cite: 5]
                mapView = currentMapView, // <--[cite: 5]
                existing = userLocationMarker, // <--[cite: 5]
                point = point, // <--[cite: 5]
                titleText = translatedUserLocationPinTitle, // <-- AJOUTÉ : Titre traduit injecté
                onClick = { locationViewModel.onMarkerClicked() } // <--[cite: 5]
            )
            currentMapView.controller.setZoom(18.0) // <--[cite: 5]
            currentMapView.controller.animateTo(point) // <--[cite: 5]
        } else if (userLocationMarker != null) { // <--[cite: 5]
            removeUserLocationMarker(currentMapView, userLocationMarker) // <--[cite: 5]
            userLocationMarker = null // <--[cite: 5]
        }
    }

    ModalNavigationDrawer( // <--[cite: 5]
        drawerState = drawerState, // <--[cite: 5]
        drawerContent = { // <--[cite: 5]
            DrawerMenu( // <--[cite: 5]
                languageViewModel = languageViewModel, // <--[cite: 5]
                parkingLayers = parkingLayers, // <--[cite: 5]
                parkingMasterActive = parkingViewModel.masterActive, // <--[cite: 5]
                parkingExpanded = parkingExpanded, // <--[cite: 5]
                onParkingExpandToggle = { parkingExpanded = !parkingExpanded }, // <--[cite: 5]
                onParkingMasterToggle = { parkingViewModel.toggleMaster() }, // <--[cite: 5]
                onParkingItemToggle = { key -> parkingViewModel.toggleType(key) }, // <--[cite: 5]
                stationTBLayers = stationTBLayers, // <--[cite: 5]
                stationTBMasterActive = stationTBViewModel.masterActive, // <--[cite: 5]
                stationTBExpanded = stationTBExpanded, // <--[cite: 5]
                onStationTBExpandToggle = { stationTBExpanded = !stationTBExpanded }, // <--[cite: 5]
                onStationTBMasterToggle = { stationTBViewModel.toggleMaster() }, // <--[cite: 5]
                onStationTBItemToggle = { key -> stationTBViewModel.toggleType(key) }, // <--[cite: 5]
                stationVLayers = stationVLayers, // <--[cite: 5]
                stationVMasterActive = stationVViewModel.masterActive, // <--[cite: 5]
                stationVExpanded = stationVExpanded, // <--[cite: 5]
                onStationVExpandToggle = { stationVExpanded = !stationVExpanded }, // <--[cite: 5]
                onStationVMasterToggle = { stationVViewModel.toggleMaster() }, // <--[cite: 5]
                onStationVItemToggle = { key -> stationVViewModel.toggleType(key) }, // <--[cite: 5]
                stationTerLayers = stationTerLayers, // <--[cite: 5]
                stationTerMasterActive = stationTerViewModel.masterActive, // <--[cite: 5]
                stationTerExpanded = stationTerExpanded, // <--[cite: 5]
                onStationTerExpandToggle = { stationTerExpanded = !stationTerExpanded }, // <--[cite: 5]
                onStationTerMasterToggle = { stationTerViewModel.toggleMaster() }, // <--[cite: 5]
                onStationTerItemToggle = { key -> stationTerViewModel.toggleType(key) }, // <--[cite: 5]
                freeVehicleLayers = freeVehicleLayers, // <--[cite: 5]
                freeVehicleMasterActive = freeVehicleViewModel.masterActive, // <--[cite: 5]
                freeVehicleExpanded = freeVehicleExpanded, // <--[cite: 5]
                onFreeVehicleExpandToggle = { freeVehicleExpanded = !freeVehicleExpanded }, // <--[cite: 5]
                onFreeVehicleMasterToggle = { freeVehicleViewModel.toggleMaster() }, // <--[cite: 5]
                onFreeVehicleItemToggle = { key -> freeVehicleViewModel.toggleType(key) }, // <--[cite: 5]

                // Données de langue passées au Drawer
                currentLanguage = currentLanguage, // <--[cite: 5]
                isTranslating = isTranslating, // <--[cite: 5]
                onLanguageSelected = { selectedLang -> // <--[cite: 5]
                    Log.d("OBS_CAMPUS", "Clic détecté pour changer de langue vers : $selectedLang") // <--[cite: 5]
                    languageViewModel.setLanguage(selectedLang) // <--[cite: 5]
                },
                onWeatherClick = { // <--[cite: 5]
                    scope.launch { drawerState.close() } // <--[cite: 5]
                    onWeatherClick(userLocation?.latitude, userLocation?.longitude) // <--[cite: 5]
                },
                onBackToMap = { scope.launch { drawerState.close() } } // <--[cite: 5]
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObcampusBackground) // <--[cite: 5]
        ) {
            // Force la reconstruction complète de la carte à chaque changement de langue
            key(displayedCampusList) { // <--[cite: 5]
                CampusMap( // <--[cite: 5]
                    campusList = displayedCampusList, // <--[cite: 5]
                    showPolygons = showCampus, // <--[cite: 5]
                    languageViewModel = languageViewModel, // <--[cite: 5]
                    parkingList = visibleParking, // <--[cite: 5]
                    onParkingClick = { parkingViewModel.onParkingClicked(it.id) }, // <--[cite: 5]
                    stationTBList = visibleStationsTB, // <--[cite: 5]
                    onStationTBClick = { stationTBViewModel.onStationClicked(it) }, // <--[cite: 5]
                    stationVList = visibleStationsV, // <--[cite: 5]
                    onStationVClick = { stationVViewModel.onStationClicked(it) }, // <--[cite: 5]
                    stationTerList = visibleStationsTer, // <--[cite: 5]
                    onStationTerClick = { stationTerViewModel.onStationClicked(it) }, // <--[cite: 5]
                    freeVehicleList = visibleFreeVehicles, // <--[cite: 5]
                    onFreeVehicleClick = { freeVehicleViewModel.onVehicleClicked(it.bikeId) }, // <--[cite: 5]
                    onMapReady = { mapView = it }, // <--[cite: 5]
                    modifier = Modifier.fillMaxSize() // <--[cite: 5]
                )
            }

            TopBar(
                languageViewModel = languageViewModel, // <--[cite: 5]
                onMenuClick = { scope.launch { drawerState.open() } } // <--[cite: 5]
            )

            SearchBar(
                languageViewModel = languageViewModel, // <--[cite: 5]
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp) // <--[cite: 5]
            )

            CampusButton(
                languageViewModel = languageViewModel, // <--[cite: 5]
                onClick = { showCampus = !showCampus }, // <--[cite: 5]
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 10.dp) // <--[cite: 5]
            )

            // Bouton de localisation avec traduction
            LocationButton(
                viewModel = locationViewModel, // <--[cite: 5]
                languageViewModel = languageViewModel, // <-- AJOUTÉ
                currentLanguage = currentLanguage,     // <-- AJOUTÉ
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 62.dp) // <--[cite: 5]
            )

            ZoomControls(
                onZoomIn = { mapView?.controller?.zoomIn() }, // <--[cite: 5]
                onZoomOut = { mapView?.controller?.zoomOut() }, // <--[cite: 5]
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 16.dp) // <--[cite: 5]
            )

            CurrentWeatherBadge(
                onClick = { onWeatherClick(userLocation?.latitude, userLocation?.longitude) }, // <--[cite: 5]
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 24.dp, start = 16.dp) // <--[cite: 5]
            )

            ErrorBanner(
                error = combinedError, // <--[cite: 5]
                languageViewModel = languageViewModel, // <--[cite: 5]
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 116.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
            )

            // --- GESTION DES BULLES D'INFO ---
            if (selectedParkingId != null) { // <--[cite: 5]
                key(currentLanguage) { // <--[cite: 5]
                    ParkingBubble( // <--[cite: 5]
                        status = selectedParkingStatus, // <--[cite: 5]
                        loading = bubbleLoadingParking, // <--[cite: 5]
                        onClose = { parkingViewModel.closeBubble() }, // <--[cite: 5]
                        languageViewModel = languageViewModel, // <--[cite: 5]
                        currentLanguage = currentLanguage, // <--[cite: 5]
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
                    )
                }
            } else if (selectedStationTB != null) { // <--[cite: 5]
                key(currentLanguage) { // <--[cite: 5]
                    StationTBBubble( // <--[cite: 5]
                        station = selectedStationTB!!, // <--[cite: 5]
                        passages = passagesTB, // <--[cite: 5]
                        loading = bubbleLoadingTB, // <--[cite: 5]
                        onClose = { stationTBViewModel.closeBubble() }, // <--[cite: 5]
                        languageViewModel = languageViewModel, // <--[cite: 5]
                        currentLanguage = currentLanguage, // <--[cite: 5]
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
                    )
                }
            } else if (selectedStationVDetail != null) { // <--[cite: 5]
                val positionCorrespondante = visibleStationsV.find { it.stationId == selectedStationVDetail!!.stationId } // <--[cite: 5]
                    ?: StationVPositionDto(0L, selectedStationVDetail!!.stationId, selectedStationVDetail!!.nom ?: "Station", selectedStationVDetail!!.latitude, selectedStationVDetail!!.longitude) // <--[cite: 5]

                key(currentLanguage) { // <--[cite: 5]
                    StationVBubble( // <--[cite: 5]
                        position = positionCorrespondante, // <--[cite: 5]
                        detail = selectedStationVDetail, // <--[cite: 5]
                        loading = bubbleLoadingV, // <--[cite: 5]
                        onClose = { stationVViewModel.closeBubble() }, // <--[cite: 5]
                        languageViewModel = languageViewModel, // <--[cite: 5]
                        currentLanguage = currentLanguage, // <--[cite: 5]
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
                    )
                }
            } else if (selectedStationTer != null) { // <--[cite: 5]
                key(currentLanguage) { // <--[cite: 5]
                    StationTerBubble( // <--[cite: 5]
                        station = selectedStationTer!!, // <--[cite: 5]
                        passages = passagesTer, // <--[cite: 5]
                        loading = bubbleLoadingTer, // <--[cite: 5]
                        onClose = { stationTerViewModel.closeBubble() }, // <--[cite: 5]
                        languageViewModel = languageViewModel, // <--[cite: 5]
                        currentLanguage = currentLanguage, // <--[cite: 5]
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
                    )
                }
            } else if (selectedFreeVehicleId != null) { // <--[cite: 5]
                key(currentLanguage) { // <--[cite: 5]
                    FreeVehicleBubble( // <--[cite: 5]
                        detail = selectedFreeVehicle, // <--[cite: 5]
                        loading = bubbleLoadingFV, // <--[cite: 5]
                        onClose = { freeVehicleViewModel.closeBubble() }, // <--[cite: 5]
                        languageViewModel = languageViewModel, // <--[cite: 5]
                        currentLanguage = currentLanguage, // <--[cite: 5]
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
                    )
                }
            } else if (locationBubbleVisible) { // <--[cite: 5]
                // Bulle d'informations de géolocalisation avec traduction
                key(currentLanguage) { // <-- AJOUTÉ : Clé de recomposition pour rafraîchir en direct
                    LocationBubble(
                        point = userLocation, // <--[cite: 5]
                        loading = locationState is com.ObservatoireCampus.mobile.viewmodel.location.LocationUiState.Loading, // <--[cite: 5]
                        accuracyMeters = locationAccuracy, // <--[cite: 5]
                        onClose = { locationViewModel.closeBubble() }, // <--[cite: 5]
                        languageViewModel = languageViewModel, // <-- AJOUTÉ
                        currentLanguage = currentLanguage,     // <-- AJOUTÉ
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // <--[cite: 5]
                    )
                }
            }
        }
    }
}