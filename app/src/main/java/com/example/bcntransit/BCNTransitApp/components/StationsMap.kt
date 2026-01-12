package com.bcntransit.app.BCNTransitApp.screens.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bcntransit.app.model.transport.StationDto
import com.bcntransit.app.screens.map.rememberMapViewWithLifecycle
import com.bcntransit.app.ui.theme.AppThemeMode
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

private const val ICON_SIZE = 64
private const val STATION_RADIUS = 16f
private const val ALERT_RADIUS = 12f
private const val BORDER_WIDTH = 3f

@Composable
fun StationsMap(
    stations: List<StationDto>,
    lineColor: Color,
    appThemeMode: AppThemeMode,
    modifier: Modifier = Modifier,
    onStationClick: (StationDto) -> Unit
) {
    val context = LocalContext.current

    val mapView = rememberMapViewWithLifecycle(context)
    var currentSymbolManager by remember { mutableStateOf<SymbolManager?>(null) }
    val isDarkTheme = !(appThemeMode == AppThemeMode.LIGHT || (appThemeMode == AppThemeMode.SYSTEM && !isSystemInDarkTheme()))

    val mapStyleUrl = if (isDarkTheme) {
        "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
    } else {
        "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
    }

    val labelTextColor = if (isDarkTheme) "#FFFFFF" else "#000000"
    val labelHaloColor = if (isDarkTheme) "#000000" else "#FFFFFF"

    val mapConfigKey = remember(stations, lineColor, isDarkTheme) {
        val stationsHash = stations.joinToString(",") { "${it.code}-${it.has_alerts}" }
        "$stationsHash-${lineColor.toArgb()}-$isDarkTheme"
    }

    DisposableEffect(Unit) {
        onDispose {
            currentSymbolManager?.deleteAll()
            currentSymbolManager = null
        }
    }

    LaunchedEffect(mapConfigKey) {
        mapView.getMapAsync { map ->

            map.setStyle(mapStyleUrl) { style ->

                currentSymbolManager?.deleteAll()
                style.removeLayer("route-layer")
                style.removeSource("route-source")

                if (stations.size > 1) {
                    val points = stations.map { Point.fromLngLat(it.longitude, it.latitude) }
                    val lineSource = GeoJsonSource(
                        "route-source",
                        FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromLngLats(points)))
                    )
                    style.addSource(lineSource)

                    val lineLayer = LineLayer("route-layer", "route-source").withProperties(
                        lineColor(lineColor.toArgb()),
                        lineWidth(3f)
                    )
                    style.addLayer(lineLayer)
                }

                val center = ICON_SIZE / 2f

                val iconNormalName = "station-circle-${lineColor.toArgb()}"
                val bitmapNormal = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888)
                val canvasNormal = Canvas(bitmapNormal)
                val paintNormal = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = lineColor.toArgb()
                }
                paintNormal.style = Paint.Style.FILL
                canvasNormal.drawCircle(center, center, STATION_RADIUS, paintNormal)
                style.addImage(iconNormalName, bitmapNormal)

                val iconWarningName = "station-warning-${lineColor.toArgb()}"
                val bitmapWarning = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888)
                val canvasWarning = Canvas(bitmapWarning)

                canvasWarning.drawCircle(center, center, STATION_RADIUS, paintNormal)

                val badgeCx = center + (STATION_RADIUS * 0.7f)
                val badgeCy = center - (STATION_RADIUS * 0.7f)

                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                }
                borderPaint.style = Paint.Style.FILL
                canvasWarning.drawCircle(badgeCx, badgeCy, ALERT_RADIUS + BORDER_WIDTH, borderPaint)

                val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.RED
                }
                badgePaint.style = Paint.Style.FILL
                canvasWarning.drawCircle(badgeCx, badgeCy, ALERT_RADIUS, badgePaint)

                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                    textSize = ALERT_RADIUS * 1.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                val textY = badgeCy - ((textPaint.descent() + textPaint.ascent()) / 2)
                canvasWarning.drawText("!", badgeCx, textY, textPaint)

                style.addImage(iconWarningName, bitmapWarning)


                val symbolManager = SymbolManager(mapView, map, style).apply {
                    iconAllowOverlap = true
                    textAllowOverlap = false
                }
                currentSymbolManager = symbolManager

                symbolManager.addClickListener { symbol ->
                    val clickedStation = stations.find { station ->
                        symbol.latLng.latitude == station.latitude &&
                                symbol.latLng.longitude == station.longitude
                    }
                    clickedStation?.let { onStationClick(it) }
                    true
                }

                val optionsList = stations.map { station ->
                    val iconToUse = if (station.has_alerts) iconWarningName else iconNormalName

                    SymbolOptions()
                        .withLatLng(LatLng(station.latitude, station.longitude))
                        .withIconImage(iconToUse)
                        .withIconSize(1.0f)
                        .withTextField(station.name)
                        .withTextOffset(arrayOf(0f, 1.2f))
                        .withTextSize(12f)
                        .withTextColor(labelTextColor)
                        .withTextHaloColor(labelHaloColor)
                        .withTextHaloWidth(2f)
                        .withTextAnchor("top")
                }

                symbolManager.create(optionsList)


                if (stations.isNotEmpty()) {
                    val boundsBuilder = LatLngBounds.Builder()
                    stations.forEach { s -> boundsBuilder.include(LatLng(s.latitude, s.longitude)) }
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150))
                }
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}