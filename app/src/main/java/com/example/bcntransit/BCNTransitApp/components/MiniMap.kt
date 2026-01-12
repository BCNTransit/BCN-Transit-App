package com.bcntransit.app.BCNTransitApp.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bcntransit.app.model.transport.AccessDto
import com.bcntransit.app.screens.map.addMarker
import com.bcntransit.app.screens.map.rememberMapViewWithLifecycle
import com.bcntransit.app.ui.theme.AppThemeMode

@Composable
fun MiniMap(
    transportType: String,
    latitude: Double,
    longitude: Double,
    accesses: List<AccessDto>,
    appThemeMode: AppThemeMode,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)
    val isDarkTheme = !(appThemeMode == AppThemeMode.LIGHT || (appThemeMode == AppThemeMode.SYSTEM && !isSystemInDarkTheme()))

    val mapStyleUrl = if (isDarkTheme) {
        "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
    } else {
        "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
    }

    val mapKey = remember(transportType, latitude, longitude, accesses, isDarkTheme) {
        "$transportType-$latitude-$longitude-${accesses.size}-$isDarkTheme"
    }

    LaunchedEffect(mapKey) {
        mapView.getMapAsync { map ->

            map.setStyle(mapStyleUrl) { style ->

                map.uiSettings.apply {
                    isScrollGesturesEnabled = false
                    isZoomGesturesEnabled = false
                    isTiltGesturesEnabled = false
                    isRotateGesturesEnabled = false
                    isLogoEnabled = false
                    isAttributionEnabled = false
                }

                map.clear()

                for (access in accesses) {
                    addMarker(
                        context = context,
                        map = map,
                        iconName = if (access.number_of_elevators > 0) "elevator" else "stairs",
                        latitude = access.latitude,
                        longitude = access.longitude,
                        zoom = 16.0
                    )
                }

                addMarker(
                    context = context,
                    map = map,
                    iconName = transportType,
                    latitude = latitude,
                    longitude = longitude,
                    markerSizeMultiplier = 1f,
                    zoom = 16.0
                )
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}