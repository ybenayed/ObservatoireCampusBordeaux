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
import com.ObservatoireCampus.mobile.ui.components.CampusButton
import com.ObservatoireCampus.mobile.ui.components.CampusMap
import com.ObservatoireCampus.mobile.ui.components.DrawerMenu
import com.ObservatoireCampus.mobile.ui.components.ErrorBanner
import com.ObservatoireCampus.mobile.ui.components.SearchBar
import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.components.ZoomControls
import com.ObservatoireCampus.mobile.ui.theme.ObcampusBackground
import com.ObservatoireCampus.mobile.viewmodel.MapViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModel
import com.ObservatoireCampus.mobile.viewmodel.parking.ParkingViewModelFactory
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val campusList by viewModel.campusList.collectAsState()
    val campusError by viewModel.error.collectAsState()

    val parkingViewModel: ParkingViewModel = viewModel(
        factory = ParkingViewModelFactory(ParkingRepository())
    )
    val parkingLayers by parkingViewModel.parkingLayers.collectAsState()
    val visibleParking by parkingViewModel.visiblePositions.collectAsState()
    val parkingError by parkingViewModel.error.collectAsState()
    var parkingExpanded by remember { mutableStateOf(false) }

    // Une seule zone d'erreur pour tout l'ecran : on combine les sources
    val combinedError = listOfNotNull(campusError, parkingError)
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" | ")

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var showCampus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (campusList.isEmpty()) viewModel.loadCampus()
        parkingViewModel.loadParking()
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

            // Zone d'erreur UNIQUE pour tout l'ecran
            ErrorBanner(
                error = combinedError,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 116.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}