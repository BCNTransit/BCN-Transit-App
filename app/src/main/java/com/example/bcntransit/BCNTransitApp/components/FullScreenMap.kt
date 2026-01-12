package com.bcntransit.app.BCNTransitApp.Screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bcntransit.app.R
import com.bcntransit.app.model.transport.AccessDto
import com.bcntransit.app.screens.map.getBitmapFromDrawable
import com.bcntransit.app.screens.map.getDrawableIdByName
import com.bcntransit.app.screens.map.rememberMapViewWithLifecycle
import com.bcntransit.app.ui.theme.AppThemeMode
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions

@Composable
fun FullScreenMap(
    transportType: String,
    latitude: Double,
    longitude: Double,
    accesses: List<AccessDto>,
    appThemeMode: AppThemeMode,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)

    var showDirectionsPopup by remember { mutableStateOf(false) }
    val isDarkTheme = !(appThemeMode == AppThemeMode.LIGHT || (appThemeMode == AppThemeMode.SYSTEM && !isSystemInDarkTheme()))

    var selectedMarkerLatitude by remember { mutableStateOf(0.0) }
    var selectedMarkerLongitude by remember { mutableStateOf(0.0) }

    val mapStyleUrl = if (isDarkTheme) {
        "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
    } else {
        "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
    }

    // Colores dinámicos para los textos del mapa
    val labelTextColor = if (isDarkTheme) "#FFFFFF" else "#333333"
    val labelHaloColor = if (isDarkTheme) "#000000" else "#FFFFFF"

    val mapKey = remember(transportType, latitude, longitude, accesses, isDarkTheme) {
        "$transportType-$latitude-$longitude-${accesses.size}-$isDarkTheme"
    }

    LaunchedEffect(mapKey) {
        mapView.getMapAsync { map ->

            map.setStyle(mapStyleUrl) { style ->

                map.uiSettings.apply {
                    isCompassEnabled = false
                    isLogoEnabled = false
                    isAttributionEnabled = false
                    isRotateGesturesEnabled = true
                    isTiltGesturesEnabled = true
                    isZoomGesturesEnabled = true
                    isScrollGesturesEnabled = true
                }

                map.clear()

                val symbolManager = SymbolManager(mapView, map, style).apply {
                    iconAllowOverlap = true
                    textAllowOverlap = false
                }

                val accessesSymbols = mutableListOf<Symbol>()

                for (access in accesses) {
                    val iconName = if (access.number_of_elevators > 0) "elevator" else "stairs"
                    val drawableId = if (access.number_of_elevators > 0) R.drawable.elevator else R.drawable.stairs

                    val bitmap = getBitmapFromDrawable(context, drawableId, 64)
                    style.addImage(iconName, bitmap)

                    val symbolOptions = SymbolOptions()
                        .withLatLng(LatLng(access.latitude, access.longitude))
                        .withIconImage(iconName)
                        .withIconSize(1.2f)
                        .withTextField(access.name ?: "")
                        .withTextOffset(arrayOf(0f, 2.0f))
                        .withTextSize(12f)
                        .withTextColor(labelTextColor)
                        .withTextHaloColor(labelHaloColor)
                        .withTextHaloWidth(2f)
                        .withTextAnchor("top")

                    val symbol = symbolManager.create(symbolOptions)
                    accessesSymbols.add(symbol)
                }

                val mainIconName = transportType
                val mainBitmap = getBitmapFromDrawable(
                    context,
                    getDrawableIdByName(context, transportType),
                    72
                )
                style.addImage(mainIconName, mainBitmap)

                val mainSymbol = symbolManager.create(
                    SymbolOptions()
                        .withLatLng(LatLng(latitude, longitude))
                        .withIconImage(mainIconName)
                        .withIconSize(1.5f)
                )

                val allPoints = accesses.map { LatLng(it.latitude, it.longitude) } + LatLng(latitude, longitude)

                if (allPoints.size > 1) {
                    val boundsBuilder = LatLngBounds.Builder()
                    allPoints.forEach { boundsBuilder.include(it) }
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150))
                } else {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16.0)
                    )
                }

                symbolManager.addClickListener { clickedSymbol ->
                    if (clickedSymbol == mainSymbol || clickedSymbol in accessesSymbols) {
                        selectedMarkerLatitude = clickedSymbol.latLng.latitude
                        selectedMarkerLongitude = clickedSymbol.latLng.longitude
                        showDirectionsPopup = true
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cerrar mapa",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (showDirectionsPopup) {
            AlertDialog(
                onDismissRequest = { showDirectionsPopup = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                title = { Text("Cómo llegar", style = MaterialTheme.typography.titleMedium) },
                text = { Text("¿Abrir Google Maps con la ruta?") },
                confirmButton = {
                    TextButton(onClick = {
                        val uri = "geo:$selectedMarkerLatitude,$selectedMarkerLongitude?q=$selectedMarkerLatitude,$selectedMarkerLongitude"
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                            data = android.net.Uri.parse(uri)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val webUri = "https://www.google.com/maps/search/?api=1&query=$selectedMarkerLatitude,$selectedMarkerLongitude"
                            val webIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(webUri))
                            context.startActivity(webIntent)
                        }
                        showDirectionsPopup = false
                    }) {
                        Text("Ir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDirectionsPopup = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}