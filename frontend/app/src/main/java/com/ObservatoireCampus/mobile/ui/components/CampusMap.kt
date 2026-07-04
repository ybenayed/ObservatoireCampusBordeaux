package com.ObservatoireCampus.mobile.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.toArgb
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingTypeStyle

/**
 * Carte OpenStreetMap. Dessine les polygones des campus recus,
 * et les marqueurs de parking (filtres par layers actifs, passes deja filtres par MapScreen).
 * onMapReady renvoie l'instance MapView au parent (MapScreen) pour
 * pouvoir piloter le zoom et les deplacements depuis l'exterieur.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusMap(
    campusList: List<CampusDto>,
    showPolygons: Boolean,
    parkingList: List<ParkingPositionDto> = emptyList(),
    onMapReady: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(campusList, showPolygons, parkingList) {
        val mapView = mapViewRef.value ?: return@LaunchedEffect
        mapView.overlays.clear()
        if (showPolygons && campusList.isNotEmpty()) {
            drawCampusPolygons(mapView, campusList)
        }
        drawParkingMarkers(mapView, parkingList)
        mapView.invalidate()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            Configuration.getInstance().userAgentValue = context.packageName
            val mapView = MapView(context)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.setBuiltInZoomControls(false)
            mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(GeoPoint(44.8067, -0.6050))
            mapViewRef.value = mapView
            onMapReady(mapView)
            mapView
        }
    )
}

private fun drawCampusPolygons(mapView: MapView, campusList: List<CampusDto>) {
    val fillColors = listOf(
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
        val geoPoints = campus.polygonCoordinates.map { coord -> GeoPoint(coord[1], coord[0]) }
        val polygon = Polygon(mapView).apply {
            points = geoPoints
            fillPaint.color = fillColors[index % fillColors.size]
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
}

private fun drawParkingMarkers(mapView: MapView, parkingList: List<ParkingPositionDto>) {
    val context = mapView.context

    parkingList.forEach { parking ->
        val lat = parking.latitude ?: return@forEach
        val lon = parking.longitude ?: return@forEach

        val marker = Marker(mapView).apply {
            position = GeoPoint(lat, lon)
            title = parking.nom
            snippet = ParkingTypeStyle.label(parking.taType)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createParkingMarkerIcon(
                context = context,
                colorArgb = ParkingTypeStyle.color(parking.taType).toArgb(),
                letter = ParkingTypeStyle.markerLetter(parking.taType)
            )
            setOnMarkerClickListener { m, _ -> m.showInfoWindow(); true }
        }
        mapView.overlays.add(marker)
    }
}

private fun createParkingMarkerIcon(
    context: Context,
    colorArgb: Int,
    letter: String,
    sizeDp: Int = 30
): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorArgb
        style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
    }

    val center = sizePx / 2f
    val radius = center - (2f * density)
    canvas.drawCircle(center, center, radius, circlePaint)
    canvas.drawCircle(center, center, radius, borderPaint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = sizePx * 0.5f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    val textY = center - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(letter, center, textY, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}