package com.ObservatoireCampus.mobile.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.ObservatoireCampus.mobile.model.CampusDto
import com.ObservatoireCampus.mobile.model.freevehicle.FreeVehiclePositionDto
import com.ObservatoireCampus.mobile.model.parking.ParkingPositionDto
import com.ObservatoireCampus.mobile.model.station.StationTBPositionDto
import com.ObservatoireCampus.mobile.model.station.StationTerPositionDto
import com.ObservatoireCampus.mobile.model.station.StationVPositionDto
import com.ObservatoireCampus.mobile.ui.components.layers.freevehicle.FreeVehicleTypeStyle
import com.ObservatoireCampus.mobile.ui.components.layers.parking.ParkingTypeStyle
import com.ObservatoireCampus.mobile.ui.components.layers.station.StationTypeStyle
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/**
 * Carte OpenStreetMap. Dessine les polygones des campus reçus,
 * les marqueurs de parking, bus/tram, vélo et TER (filtrés par layers actifs,
 * passés déjà filtrés par MapScreen).
 * onMapReady renvoie l'instance MapView au parent (MapScreen) pour
 * pouvoir piloter le zoom et les déplacements depuis l'extérieur.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusMap(
    campusList: List<CampusDto>,
    showPolygons: Boolean,
    languageViewModel: LanguageViewModel,
    parkingList: List<ParkingPositionDto> = emptyList(),
    onParkingClick: (ParkingPositionDto) -> Unit = {},
    stationTBList: List<StationTBPositionDto> = emptyList(),
    onStationTBClick: (StationTBPositionDto) -> Unit = {},
    stationVList: List<StationVPositionDto> = emptyList(),
    onStationVClick: (StationVPositionDto) -> Unit = {},
    stationTerList: List<StationTerPositionDto> = emptyList(),
    onStationTerClick: (StationTerPositionDto) -> Unit = {},
    freeVehicleList: List<FreeVehiclePositionDto> = emptyList(),
    onFreeVehicleClick: (FreeVehiclePositionDto) -> Unit = {},
    onMapReady: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    // Utilisation d'un CoroutineScope Compose officiel et sécurisé
    val coroutineScope = rememberCoroutineScope()

    // On s'assure de repeindre correctement dès que le contenu ou la langue change
    LaunchedEffect(
        campusList, showPolygons, parkingList, stationTBList, stationVList, stationTerList, freeVehicleList, languageViewModel
    ) {
        val mapView = mapViewRef.value ?: return@LaunchedEffect
        mapView.overlays.clear()

        // Redessine l'ensemble avec les nouveaux titres traduits
        if (showPolygons && campusList.isNotEmpty()) {
            drawCampusPolygons(mapView, campusList)
        }
        drawParkingMarkers(mapView, parkingList, languageViewModel, coroutineScope, onParkingClick)
        drawStationTBMarkers(mapView, stationTBList, languageViewModel, coroutineScope, onStationTBClick)
        drawStationVMarkers(mapView, stationVList, languageViewModel, coroutineScope, onStationVClick)
        drawStationTerMarkers(mapView, stationTerList, languageViewModel, coroutineScope, onStationTerClick)
        drawFreeVehicleMarkers(mapView, freeVehicleList, onFreeVehicleClick)

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

private fun drawParkingMarkers(
    mapView: MapView,
    parkingList: List<ParkingPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (ParkingPositionDto) -> Unit
) {
    val context = mapView.context

    parkingList.forEach { parking ->
        val lat = parking.latitude ?: return@forEach
        val lon = parking.longitude ?: return@forEach

        val marker = Marker(mapView).apply {
            position = GeoPoint(lat, lon)
            title = parking.nom
            snippet = parking.taType // Valeur par défaut temporaire

            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = ParkingTypeStyle.color(parking.taType).toArgb(),
                letter = ParkingTypeStyle.markerLetter(parking.taType)
            )
            setOnMarkerClickListener { _, _ -> onClick(parking); true }
        }

        // Lancement asynchrone de la traduction via le scope Compose transmis
        coroutineScope.launch {
            val translatedLabel = ParkingTypeStyle.label(parking.taType, languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun drawStationTBMarkers(
    mapView: MapView,
    stations: List<StationTBPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (StationTBPositionDto) -> Unit
) {
    val context = mapView.context

    stations.forEach { station ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = station.nom
            snippet = station.mode
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = StationTypeStyle.color(station.mode).toArgb(),
                letter = StationTypeStyle.markerLetter(station.mode)
            )
            setOnMarkerClickListener { _, _ -> onClick(station); true }
        }

        coroutineScope.launch {
            val translatedLabel = StationTypeStyle.label(station.mode, languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun drawStationVMarkers(
    mapView: MapView,
    stations: List<StationVPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (StationVPositionDto) -> Unit
) {
    val context = mapView.context

    stations.forEach { station ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = station.nom
            snippet = "VELO"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = StationTypeStyle.color("VELO").toArgb(),
                letter = StationTypeStyle.markerLetter("VELO")
            )
            setOnMarkerClickListener { _, _ -> onClick(station); true }
        }

        coroutineScope.launch {
            val translatedLabel = StationTypeStyle.label("VELO", languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun drawStationTerMarkers(
    mapView: MapView,
    stations: List<StationTerPositionDto>,
    languageViewModel: LanguageViewModel,
    coroutineScope: CoroutineScope,
    onClick: (StationTerPositionDto) -> Unit
) {
    val context = mapView.context

    stations.forEach { station ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(station.latitude, station.longitude)
            title = station.nom
            snippet = "TER"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createMarkerIcon(
                context = context,
                colorArgb = StationTypeStyle.color("TER").toArgb(),
                letter = StationTypeStyle.markerLetter("TER")
            )
            setOnMarkerClickListener { _, _ -> onClick(station); true }
        }

        coroutineScope.launch {
            val translatedLabel = StationTypeStyle.label("TER", languageViewModel)
            marker.snippet = translatedLabel
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }

        mapView.overlays.add(marker)
    }
}

private fun createMarkerIcon(
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

private fun drawFreeVehicleMarkers(
    mapView: MapView,
    vehicles: List<FreeVehiclePositionDto>,
    onClick: (FreeVehiclePositionDto) -> Unit
) {
    val context = mapView.context
    vehicles.forEach { vehicle ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(vehicle.latitude, vehicle.longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = createVehicleMarkerIcon(context, FreeVehicleTypeStyle.color(vehicle.vehicleTypeId).toArgb(), vehicle.vehicleTypeId)
            setOnMarkerClickListener { _, _ -> onClick(vehicle); true }
        }
        mapView.overlays.add(marker)
    }
}

private fun createVehicleMarkerIcon(
    context: Context,
    colorArgb: Int,
    vehicleTypeId: String,
    sizeDp: Int = 36
): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val w = sizePx.toFloat()

    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorArgb; style = Paint.Style.FILL }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
    }

    val cx = w / 2f
    val cy = w * 0.36f
    val r = w * 0.34f
    val path = Path().apply {
        addCircle(cx, cy, r, Path.Direction.CW)
        moveTo(cx - r * 0.85f, cy + r * 0.55f)
        lineTo(cx, w * 0.95f)
        lineTo(cx + r * 0.85f, cy + r * 0.55f)
        close()
    }
    canvas.drawPath(path, pinPaint)
    canvas.drawPath(path, borderPaint)

    val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1.8f * density
        strokeCap = Paint.Cap.ROUND
    }
    val wheelR = w * 0.075f
    val wheelY = cy + w * 0.09f

    when (vehicleTypeId) {
        "yego_bike" -> {
            val leftX = cx - w * 0.13f
            val rightX = cx + w * 0.13f
            canvas.drawCircle(leftX, wheelY, wheelR, glyphPaint)
            canvas.drawCircle(rightX, wheelY, wheelR, glyphPaint)
            canvas.drawLine(leftX, wheelY, cx, cy - w * 0.05f, glyphPaint)
            canvas.drawLine(cx, cy - w * 0.05f, rightX, wheelY, glyphPaint)
            canvas.drawLine(leftX, wheelY, rightX - w * 0.02f, cy - w * 0.02f, glyphPaint)
        }

        "yego_kick" -> {
            val leftX = cx - w * 0.1f
            val rightX = cx + w * 0.14f
            canvas.drawCircle(leftX, wheelY, wheelR * 0.8f, glyphPaint)
            canvas.drawCircle(rightX, wheelY, wheelR * 0.8f, glyphPaint)
            canvas.drawLine(leftX, wheelY, rightX, wheelY, glyphPaint)
            canvas.drawLine(rightX, wheelY, rightX, cy - w * 0.12f, glyphPaint)
            canvas.drawLine(
                rightX - w * 0.05f,
                cy - w * 0.12f,
                rightX + w * 0.05f,
                cy - w * 0.12f,
                glyphPaint
            )
        }

        else -> {
            val leftX = cx - w * 0.12f
            val rightX = cx + w * 0.12f
            canvas.drawCircle(leftX, wheelY, wheelR, glyphPaint)
            canvas.drawCircle(rightX, wheelY, wheelR, glyphPaint)
            canvas.drawLine(leftX, wheelY, cx + w * 0.02f, cy - w * 0.02f, glyphPaint)
            canvas.drawLine(cx + w * 0.02f, cy - w * 0.02f, rightX, wheelY, glyphPaint)
            canvas.drawLine(
                cx - w * 0.02f,
                cy - w * 0.02f,
                cx - w * 0.02f,
                cy - w * 0.14f,
                glyphPaint
            )
        }
    }

    return BitmapDrawable(context.resources, bitmap)
}