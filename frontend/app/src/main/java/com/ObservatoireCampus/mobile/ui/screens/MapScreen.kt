package com.ObservatoireCampus.mobile.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val campusList by viewModel.campusList.collectAsState()
    val error by viewModel.error.collectAsState()
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    var showPolygons by remember { mutableStateOf(false) }

    LaunchedEffect(campusList, showPolygons) {
        val mapView = mapViewRef.value ?: return@LaunchedEffect
        mapView.overlays.clear()
        if (showPolygons && campusList.isNotEmpty()) {
            drawCampusPolygons(mapView, campusList)
        }
        mapView.invalidate()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                Configuration.getInstance().userAgentValue = context.packageName
                val mapView = MapView(context)
                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.setMultiTouchControls(true)
                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(GeoPoint(44.8067, -0.6050))
                mapViewRef.value = mapView
                mapView
            }
        )

        // Affiche l'erreur en rouge si présente
        error?.let {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                color = Color.Red.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Erreur : $it",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Info campus chargés
        if (campusList.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                color = Color(0xFF1E88E5).copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "${campusList.size} campus chargés",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Button(
            onClick = {
                if (campusList.isEmpty()) {
                    viewModel.loadCampus() // réessaie si vide
                } else {
                    showPolygons = !showPolygons
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showPolygons)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                when {
                    campusList.isEmpty() && error != null -> "Réessayer"
                    campusList.isEmpty() -> "Chargement..."
                    showPolygons -> "Masquer les campus"
                    else -> "Voir les campus (${campusList.size})"
                }
            )
        }
    }
}

private fun drawCampusPolygons(mapView: MapView, campusList: List<CampusDto>) {
    val colors = listOf(
        0x882563eb.toInt(),
        0x8816a34a.toInt(),
        0x88dc2626.toInt()
    )
    val strokeColors = listOf(
        0xFF2563eb.toInt(),
        0xFF16a34a.toInt(),
        0xFFdc2626.toInt()
    )

    campusList.forEachIndexed { index, campus ->
        if (campus.polygonCoordinates.isEmpty()) return@forEachIndexed

        val geoPoints = campus.polygonCoordinates.map { coord ->
            GeoPoint(coord[1], coord[0])
        }

        val polygon = Polygon(mapView).apply {
            points = geoPoints
            fillPaint.color = colors[index % colors.size]
            outlinePaint.color = strokeColors[index % strokeColors.size]
            outlinePaint.strokeWidth = 3f
            title = campus.name
            setOnClickListener { _, _, _ ->
                showInfoWindow()
                true
            }
        }
        mapView.overlays.add(polygon)
    }

    campusList.firstOrNull()?.let {
        mapView.controller.animateTo(GeoPoint(it.centerLat, it.centerLng))
    }
}