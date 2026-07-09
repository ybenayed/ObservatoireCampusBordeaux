package com.ObservatoireCampus.mobile.ui.screens

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
import com.ObservatoireCampus.mobile.ui.components.CampusButton
import com.ObservatoireCampus.mobile.ui.components.CampusMap
import com.ObservatoireCampus.mobile.ui.components.DrawerMenu
import com.ObservatoireCampus.mobile.ui.components.ErrorBanner
import com.ObservatoireCampus.mobile.ui.components.SearchBar
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.ZoomControls
import com.ObservatoireCampus.mobile.ui.components.weather.CurrentWeatherBadge
import com.ObservatoireCampus.mobile.ui.components.station.StationTBBubble
import com.ObservatoireCampus.mobile.ui.components.station.StationVBubble
import com.ObservatoireCampus.mobile.ui.components.parking.ParkingBubble
import com.ObservatoireCampus.mobile.ui.theme.ObcampusBackground
import com.ObservatoireCampus.mobile.viewmodel.MapViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationTBViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationTBViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.station.StationVViewModel
import com.ObservatoireCampus.mobile.viewmodel.station.StationVViewModelFactory
import com.ObservatoireCampus.mobile.viewmodel.freevehicle.FreeVehicleViewModel
import com.ObservatoireCampus.mobile.viewmodel.freevehicle.FreeVehicleViewModelFactory
import com.ObservatoireCampus.mobile.repository.freevehicle.FreeVehicleRepository
import com.ObservatoireCampus.mobile.ui.components.freevehicle.FreeVehicleBubble
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onWeatherClick: () -> Unit = {}
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

    // Une seule zone d'erreur pour tout l'ecran : on combine les sources
    val combinedError =
        listOfNotNull(campusError, parkingError, stationTBError, stationVError, freeVehicleError)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" | ")

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var showCampus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (campusList.isEmpty()) viewModel.loadCampus()
        parkingViewModel.loadParking()
        stationTBViewModel.loadStations()
        stationVViewModel.loadStations()
        freeVehicleViewModel.loadStations()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
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
                freeVehicleLayers = freeVehicleLayers,
                freeVehicleMasterActive = freeVehicleViewModel.masterActive,
                freeVehicleExpanded = freeVehicleExpanded,
                onFreeVehicleExpandToggle = { freeVehicleExpanded = !freeVehicleExpanded },
                onFreeVehicleMasterToggle = { freeVehicleViewModel.toggleMaster() },
                onFreeVehicleItemToggle = { key -> freeVehicleViewModel.toggleType(key) },
                onWeatherClick = {
                    scope.launch { drawerState.close() }
                    onWeatherClick()
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
            CampusMap(
                campusList = campusList,
                showPolygons = showCampus,
                parkingList = visibleParking,
                onParkingClick = { parkingViewModel.onParkingClicked(it.id) },
                stationTBList = visibleStationsTB,
                onStationTBClick = { stationTBViewModel.onStationClicked(it) },
                stationVList = visibleStationsV,
                onStationVClick = { stationVViewModel.onStationClicked(it) },
                freeVehicleList = visibleFreeVehicles,
                onFreeVehicleClick = { freeVehicleViewModel.onVehicleClicked(it.bikeId) },
                onMapReady = { mapView = it },
                modifier = Modifier.fillMaxSize()
            )

            TopBar(onMenuClick = { scope.launch { drawerState.open() } })

            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
            )

            CampusButton(
                onClick = { showCampus = !showCampus },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 10.dp)
            )

            ZoomControls(
                onZoomIn = { mapView?.controller?.zoomIn() },
                onZoomOut = { mapView?.controller?.zoomOut() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 16.dp)
            )

            CurrentWeatherBadge(
                onClick = onWeatherClick,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 24.dp, start = 16.dp)
            )

            ErrorBanner(
                error = combinedError,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 116.dp, start = 16.dp, end = 16.dp)
            )

            // --- GESTION DES BULLES D'INFO ---
            if (selectedParkingId != null) {
                ParkingBubble(
                    status = selectedParkingStatus,
                    loading = bubbleLoadingParking,
                    onClose = { parkingViewModel.closeBubble() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                )
            } else if (selectedStationTB != null) {
                StationTBBubble(
                    station = selectedStationTB!!,
                    passages = passagesTB,
                    loading = bubbleLoadingTB,
                    onClose = { stationTBViewModel.closeBubble() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                )
            } else if (selectedStationVDetail != null) {
                val positionCorrespondante = visibleStationsV.find { it.stationId == selectedStationVDetail!!.stationId }
                    ?: StationVPositionDto(0L, selectedStationVDetail!!.stationId, selectedStationVDetail!!.nom ?: "Station", selectedStationVDetail!!.latitude, selectedStationVDetail!!.longitude)

                StationVBubble(
                    position = positionCorrespondante,
                    detail = selectedStationVDetail,
                    loading = bubbleLoadingV,
                    onClose = { stationVViewModel.closeBubble() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                )
            } else if (selectedFreeVehicleId != null) {
                FreeVehicleBubble(
                    detail = selectedFreeVehicle,
                    loading = bubbleLoadingFV,
                    onClose = { freeVehicleViewModel.closeBubble() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}