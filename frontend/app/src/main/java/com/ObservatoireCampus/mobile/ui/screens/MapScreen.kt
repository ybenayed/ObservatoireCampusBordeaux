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
    languageViewModel: LanguageViewModel,
    onWeatherClick: (Double?, Double?) -> Unit = { _, _ -> },
    onInternshipClick: () -> Unit = {} // <-- Déclaré ici !
) {
    val campusList by viewModel.campusList.collectAsState()
    val campusError by viewModel.error.collectAsState()

    val parkingViewModel: ParkingViewModel = viewModel(
        factory = ParkingViewModelFactory(ParkingRepository())
    )
    val parkingLayers by parkingViewModel.parkingLayers.collectAsState()
    val visibleParking by parkingViewModel.visiblePositions.collectAsState()
    val parkingError by parkingViewModel.error.collectAsState()
    val selectedParkingId by parkingViewModel.selectedParkingId.collectAsState()
    val selectedParkingStatus by parkingViewModel.selectedParkingStatus.collectAsState()
    val bubbleLoadingParking by parkingViewModel.bubbleLoading.collectAsState()
    var parkingExpanded by remember { mutableStateOf(false) }

    // Bus / Tram
    val stationTBViewModel: StationTBViewModel = viewModel(
        factory = StationTBViewModelFactory(StationTBRepository())
    )
    val stationTBLayers by stationTBViewModel.layers.collectAsState()
    val visibleStationsTB by stationTBViewModel.visiblePositions.collectAsState()
    val selectedStationTB by stationTBViewModel.selectedStation.collectAsState()
    val passagesTB by stationTBViewModel.passages.collectAsState()
    val bubbleLoadingTB by stationTBViewModel.bubbleLoading.collectAsState()
    val stationTBError by stationTBViewModel.error.collectAsState()
    var stationTBExpanded by remember { mutableStateOf(false) }

    // Velo
    val stationVViewModel: StationVViewModel = viewModel(
        factory = StationVViewModelFactory(StationVRepository())
    )
    val stationVLayers by stationVViewModel.layers.collectAsState()
    val visibleStationsV by stationVViewModel.visiblePositions.collectAsState()
    val selectedStationVDetail by stationVViewModel.selectedDetail.collectAsState()
    val bubbleLoadingV by stationVViewModel.bubbleLoading.collectAsState()
    val stationVError by stationVViewModel.error.collectAsState()
    var stationVExpanded by remember { mutableStateOf(false) }

    // TER
    val stationTerViewModel: StationTerViewModel = viewModel(
        factory = StationTerViewModelFactory(StationTerRepository())
    )
    val stationTerLayers by stationTerViewModel.layers.collectAsState()
    val visibleStationsTer by stationTerViewModel.visiblePositions.collectAsState()
    val selectedStationTer by stationTerViewModel.selectedStation.collectAsState()
    val passagesTer by stationTerViewModel.passages.collectAsState()
    val bubbleLoadingTer by stationTerViewModel.bubbleLoading.collectAsState()
    val stationTerError by stationTerViewModel.error.collectAsState()
    var stationTerExpanded by remember { mutableStateOf(false) }

    // Free vehicle
    val freeVehicleViewModel: FreeVehicleViewModel = viewModel(
        factory = FreeVehicleViewModelFactory(FreeVehicleRepository())
    )
    val freeVehicleLayers by freeVehicleViewModel.layers.collectAsState()
    val visibleFreeVehicles by freeVehicleViewModel.visiblePositions.collectAsState()
    val freeVehicleError by freeVehicleViewModel.error.collectAsState()
    val selectedFreeVehicleId by freeVehicleViewModel.selectedVehicleId.collectAsState()
    val selectedFreeVehicle by freeVehicleViewModel.selectedVehicle.collectAsState()
    val bubbleLoadingFV by freeVehicleViewModel.bubbleLoading.collectAsState()
    var freeVehicleExpanded by remember { mutableStateOf(false) }

    // Localisation utilisateur
    val context = LocalContext.current
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(LocationServices.getFusedLocationProviderClient(context))
    )
    val userLocation by locationViewModel.userLocation.collectAsState()
    val locationBubbleVisible by locationViewModel.bubbleVisible.collectAsState()
    val locationAccuracy by locationViewModel.accuracyMeters.collectAsState()
    val locationState by locationViewModel.locationState.collectAsState()

    // Langue
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val isTranslating by languageViewModel.isTranslating.collectAsState()
    val languageError by languageViewModel.error.collectAsState()

    // --- TRADUCTION DE LA LOCALISATION ET DES CAMPUS ---
    var displayedCampusList by remember { mutableStateOf(campusList) }
    var translatedUserLocationPinTitle by remember { mutableStateOf("Ma position") }

    LaunchedEffect(currentLanguage, campusList) {
        translatedUserLocationPinTitle = languageViewModel.translate("Ma position")

        displayedCampusList = if (currentLanguage == AppLanguage.FR) {
            campusList
        } else {
            campusList.map { campus ->
                val nomTraduit = languageViewModel.translate(campus.name)
                campus.copy(name = nomTraduit)
            }
        }
    }

    val combinedError =
        listOfNotNull(
            campusError, parkingError, stationTBError, stationVError,
            stationTerError, freeVehicleError, languageError
        )
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" | ")

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var showCampus by remember { mutableStateOf(false) }

    // Référence du marqueur "Ma position"
    var userLocationMarker by remember { mutableStateOf<Marker?>(null) }

    LaunchedEffect(Unit) {
        if (campusList.isEmpty()) viewModel.loadCampus()
        parkingViewModel.loadParking()
        stationTBViewModel.loadStations()
        stationVViewModel.loadStations()
        stationTerViewModel.loadStations()
        freeVehicleViewModel.loadStations()
    }

    LaunchedEffect(userLocation, mapView, translatedUserLocationPinTitle) {
        val currentMapView = mapView ?: return@LaunchedEffect
        val point = userLocation

        if (point != null) {
            userLocationMarker = upsertUserLocationMarker(
                mapView = currentMapView,
                existing = userLocationMarker,
                point = point,
                titleText = translatedUserLocationPinTitle,
                onClick = { locationViewModel.onMarkerClicked() }
            )
            currentMapView.controller.setZoom(18.0)
            currentMapView.controller.animateTo(point)
        } else if (userLocationMarker != null) {
            removeUserLocationMarker(currentMapView, userLocationMarker)
            userLocationMarker = null
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                languageViewModel = languageViewModel,
                parkingLayers = parkingLayers,
                parkingMasterActive = parkingViewModel.masterActive,
                parkingExpanded = parkingExpanded,
                onParkingExpandToggle = { parkingExpanded = !parkingExpanded },
                onParkingMasterToggle = { parkingViewModel.toggleMaster() },
                onParkingItemToggle = { key -> parkingViewModel.toggleType(key) },
                stationTBLayers = stationTBLayers,
                stationTBMasterActive = stationTBViewModel.masterActive,
                stationTBExpanded = stationTBExpanded,
                onStationTBExpandToggle = { stationTBExpanded = !stationTBExpanded },
                onStationTBMasterToggle = { stationTBViewModel.toggleMaster() },
                onStationTBItemToggle = { key -> stationTBViewModel.toggleType(key) },
                stationVLayers = stationVLayers,
                stationVMasterActive = stationVViewModel.masterActive,
                stationVExpanded = stationVExpanded,
                onStationVExpandToggle = { stationVExpanded = !stationVExpanded },
                onStationVMasterToggle = { stationVViewModel.toggleMaster() },
                onStationVItemToggle = { key -> stationVViewModel.toggleType(key) },
                stationTerLayers = stationTerLayers,
                stationTerMasterActive = stationTerViewModel.masterActive,
                stationTerExpanded = stationTerExpanded,
                onStationTerExpandToggle = { stationTerExpanded = !stationTerExpanded },
                onStationTerMasterToggle = { stationTerViewModel.toggleMaster() },
                onStationTerItemToggle = { key -> stationTerViewModel.toggleType(key) },
                freeVehicleLayers = freeVehicleLayers,
                freeVehicleMasterActive = freeVehicleViewModel.masterActive,
                freeVehicleExpanded = freeVehicleExpanded,
                onFreeVehicleExpandToggle = { freeVehicleExpanded = !freeVehicleExpanded },
                onFreeVehicleMasterToggle = { freeVehicleViewModel.toggleMaster() },
                onFreeVehicleItemToggle = { key -> freeVehicleViewModel.toggleType(key) },
                currentLanguage = currentLanguage,
                isTranslating = isTranslating,
                onLanguageSelected = { selectedLang ->
                    languageViewModel.setLanguage(selectedLang)
                },
                onWeatherClick = {
                    scope.launch { drawerState.close() }
                    onWeatherClick(userLocation?.latitude, userLocation?.longitude)
                },
                // --- CORRECTION CI-DESSOUS : L'ACTION EST MAINTENANT LIÉE ET DÉFINIE ! ---
                onInternshipClick = {
                    scope.launch { drawerState.close() }
                    onInternshipClick() // Appelle la callback passée à MapScreen
                },
                onBackToMap = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObcampusBackground)
        ) {
            key(displayedCampusList) {
                CampusMap(
                    campusList = displayedCampusList,
                    showPolygons = showCampus,
                    languageViewModel = languageViewModel,
                    parkingList = visibleParking,
                    onParkingClick = { parkingViewModel.onParkingClicked(it.id) },
                    stationTBList = visibleStationsTB,
                    onStationTBClick = { stationTBViewModel.onStationClicked(it) },
                    stationVList = visibleStationsV,
                    onStationVClick = { stationVViewModel.onStationClicked(it) },
                    stationTerList = visibleStationsTer,
                    onStationTerClick = { stationTerViewModel.onStationClicked(it) },
                    freeVehicleList = visibleFreeVehicles,
                    onFreeVehicleClick = { freeVehicleViewModel.onVehicleClicked(it.bikeId) },
                    onMapReady = { mapView = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            TopBar(
                languageViewModel = languageViewModel,
                onMenuClick = { scope.launch { drawerState.open() } }
            )

            SearchBar(
                languageViewModel = languageViewModel,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
            )

            CampusButton(
                languageViewModel = languageViewModel,
                onClick = { showCampus = !showCampus },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 10.dp)
            )

            LocationButton(
                viewModel = locationViewModel,
                languageViewModel = languageViewModel,
                currentLanguage = currentLanguage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 62.dp)
            )

            ZoomControls(
                onZoomIn = { mapView?.controller?.zoomIn() },
                onZoomOut = { mapView?.controller?.zoomOut() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 16.dp)
            )

            CurrentWeatherBadge(
                onClick = { onWeatherClick(userLocation?.latitude, userLocation?.longitude) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 24.dp, start = 16.dp)
            )

            ErrorBanner(
                error = combinedError,
                languageViewModel = languageViewModel,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 116.dp, start = 16.dp, end = 16.dp)
            )

            if (selectedParkingId != null) {
                key(currentLanguage) {
                    ParkingBubble(
                        status = selectedParkingStatus,
                        loading = bubbleLoadingParking,
                        onClose = { parkingViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedStationTB != null) {
                key(currentLanguage) {
                    StationTBBubble(
                        station = selectedStationTB!!,
                        passages = passagesTB,
                        loading = bubbleLoadingTB,
                        onClose = { stationTBViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedStationVDetail != null) {
                val positionCorrespondante = visibleStationsV.find { it.stationId == selectedStationVDetail!!.stationId }
                    ?: StationVPositionDto(0L, selectedStationVDetail!!.stationId, selectedStationVDetail!!.nom ?: "Station", selectedStationVDetail!!.latitude, selectedStationVDetail!!.longitude)

                key(currentLanguage) {
                    StationVBubble(
                        position = positionCorrespondante,
                        detail = selectedStationVDetail,
                        loading = bubbleLoadingV,
                        onClose = { stationVViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedStationTer != null) {
                key(currentLanguage) {
                    StationTerBubble(
                        station = selectedStationTer!!,
                        passages = passagesTer,
                        loading = bubbleLoadingTer,
                        onClose = { stationTerViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (selectedFreeVehicleId != null) {
                key(currentLanguage) {
                    FreeVehicleBubble(
                        detail = selectedFreeVehicle,
                        loading = bubbleLoadingFV,
                        onClose = { freeVehicleViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            } else if (locationBubbleVisible) {
                key(currentLanguage) {
                    LocationBubble(
                        point = userLocation,
                        loading = locationState is com.ObservatoireCampus.mobile.viewmodel.location.LocationUiState.Loading,
                        accuracyMeters = locationAccuracy,
                        onClose = { locationViewModel.closeBubble() },
                        languageViewModel = languageViewModel,
                        currentLanguage = currentLanguage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}