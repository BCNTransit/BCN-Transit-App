package com.bcntransit.app.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bcntransit.app.BCNTransitApp.Screens.map.FullScreenMap
import com.bcntransit.app.BCNTransitApp.Screens.search.routes.RouteCard
import com.bcntransit.app.BCNTransitApp.components.InlineErrorBanner
import com.bcntransit.app.BCNTransitApp.components.MiniMap
import com.bcntransit.app.R
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.api.ApiService
import com.bcntransit.app.data.enums.TransportType
import com.bcntransit.app.model.FavoriteDto
import com.bcntransit.app.model.transport.RouteDto
import com.bcntransit.app.screens.map.getDrawableIdByName
import com.bcntransit.app.util.LanguageManager
import com.bcntransit.app.util.getAndroidId
import com.example.bcntransit.BCNTransitApp.Screens.search.routes.CompactRouteCard
import com.example.bcntransit.BCNTransitApp.components.CustomFloatingActionButton
import com.example.bcntransit.BCNTransitApp.components.CustomTopBar
import kotlinx.coroutines.launch
import kotlin.Unit

@Composable
fun RoutesScreen(
    lineCode: String,
    stationCode: String,
    apiService: ApiService,
    onConnectionClick: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: RoutesViewModel = viewModel(
        key = "$lineCode-$stationCode-${apiService.hashCode()}",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RoutesViewModel(
                    apiService = apiService,
                    lineCode = lineCode,
                    stationCode = stationCode
                ) as T
            }
        }
    )

    val currentUserId = getAndroidId(LocalContext.current)
    val routesState by viewModel.routesState.collectAsState()
    val connectionsState by viewModel.stationConnectionsState.collectAsState()
    val accessesState by viewModel.stationAccessesState.collectAsState()
    val selectedStation by viewModel.selectedStation.collectAsState()
    var showFullMap by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var isFavorite by remember { mutableStateOf(false) }
    var isLoadingFavorite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentLangCode = remember { LanguageManager.getSavedLanguage(context) }

    var selectedRoute by remember { mutableStateOf<RouteDto?>(null) }

    if (selectedStation == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colorResource(R.color.medium_red))
        }
        return
    } else {
        LaunchedEffect(selectedStation!!.code, currentUserId) {
            try {
                isFavorite = ApiClient.userApiService.userHasFavorite(
                    type = selectedStation!!.transport_type,
                    itemId = selectedStation!!.code
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val drawableId = remember(selectedStation!!.line_name) {
        getDrawableIdByName(context, selectedStation!!.transport_type)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showFullMap) {
            FullScreenMap(
                transportType = selectedStation!!.transport_type,
                latitude = selectedStation!!.latitude,
                longitude = selectedStation!!.longitude,
                accesses = accessesState.accesses,
                onDismiss = { showFullMap = false }
            )
        }
        else {
            Scaffold(
                topBar = {
                    CustomTopBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(drawableId),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(50.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = selectedStation!!.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "(${selectedStation!!.code})  ·  ",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        val alertText = if (selectedStation!!.has_alerts) stringResource(R.string.incidents) else stringResource(R.string.normal_service)
                                        val alertColor = if (selectedStation!!.has_alerts) colorResource(R.color.red) else colorResource(R.color.dark_green)

                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(alertColor, shape = CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            alertText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            if (isFavorite) {
                                                try {
                                                    isLoadingFavorite = true
                                                    ApiClient.userApiService.deleteUserFavorite(
                                                        selectedStation!!.transport_type,
                                                        selectedStation!!.code
                                                    )
                                                    isFavorite = false
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                } finally {
                                                    isLoadingFavorite = false
                                                }
                                            } else {
                                                try {
                                                    isLoadingFavorite = true
                                                    ApiClient.userApiService.addUserFavorite(
                                                        favorite = FavoriteDto(
                                                            USER_ID = currentUserId,
                                                            TYPE = selectedStation!!.transport_type,
                                                            LINE_CODE = selectedStation!!.line_code,
                                                            LINE_NAME = selectedStation!!.line_name,
                                                            LINE_NAME_WITH_EMOJI = selectedStation!!.line_name_with_emoji ?: "",
                                                            STATION_CODE = selectedStation!!.code,
                                                            STATION_NAME = selectedStation!!.name,
                                                            STATION_GROUP_CODE = selectedStation!!.CODI_GRUP_ESTACIO.toString(),
                                                            coordinates = listOf(
                                                                selectedStation!!.latitude,
                                                                selectedStation!!.longitude
                                                            )
                                                        )
                                                    )
                                                    isFavorite = true
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                } finally {
                                                    isLoadingFavorite = false
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    if (isLoadingFavorite) {
                                        CircularProgressIndicator(color = colorResource(R.color.medium_red))
                                    } else {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = "Favorito",
                                            tint = colorResource(R.color.red)
                                        )
                                    }
                                }
                            }
                        },
                        onBackClick = onBackClick,
                        height = 80.dp
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // ALERTS
                        if (selectedStation!!.has_alerts) {
                            item {
                                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                    selectedStation!!.alerts.forEach { alert ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.medium_red))
                                        ) {
                                            val (header, body) = when (currentLangCode) {
                                                "ca" -> alert.headerCa to alert.textCa
                                                "en" -> alert.headerEn to alert.textEn
                                                else -> alert.headerEs to alert.textEs
                                            }
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = header,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                if (body.isNotEmpty()) {
                                                    Text(
                                                        text = body,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        // RUTAS
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedRoute == null) {
                                    Text(
                                        stringResource(R.string.routes_arrivals),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(top = 16.dp)
                                    )
                                } else {
                                    Spacer(Modifier.padding(top = 16.dp))
                                }
                            }
                        }

                        if (routesState.loading && routesState.routes.isEmpty()) {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = colorResource(R.color.medium_red)) }
                        } else if (routesState.error != null) {
                            item { InlineErrorBanner(routesState.error!!) }
                        } else if (routesState.routes.isEmpty()){
                            item { Text(stringResource(R.string.routes_not_available)) }
                        } else {
                            // CASO 1: Es BUS y tenemos una ruta seleccionada -> MOSTRAMOS SOLO ESA (Grande)
                            if (selectedStation!!.transport_type == TransportType.BUS.type && selectedRoute != null) {
                                item {
                                    Column {
                                        // Botón "Atrás" para volver a la lista (UX importante)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp)
                                                .clickable { selectedRoute = null },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = stringResource(R.string.back),
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = stringResource(R.string.route_back_to_list),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        RouteCard(selectedRoute!!, routesState.loading)
                                    }
                                }
                            }
                            // CASO 2: Es BUS y NO hay selección -> MOSTRAMOS GRID (Compactas)
                            else if (selectedStation!!.transport_type == TransportType.BUS.type && routesState.routes.size > 1) {
                                val chunks = routesState.routes.chunked(2)
                                items(chunks) { rowRoutes ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Columna 1
                                        Box(modifier = Modifier.weight(1f)) {
                                            CompactRouteCard(
                                                route = rowRoutes[0],
                                                isLoading = routesState.loading,
                                                onClick = { selectedRoute = rowRoutes[0] }
                                            )
                                        }

                                        // Columna 2
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (rowRoutes.size > 1) {
                                                CompactRouteCard(
                                                    route = rowRoutes[1],
                                                    isLoading = routesState.loading,
                                                    onClick = { selectedRoute = rowRoutes[1] }
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.fillMaxWidth())
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                            // CASO 3: NO es BUS (Metro, Tram, etc.) -> Lista normal siempre
                            else {
                                items(routesState.routes) { route ->
                                    RouteCard(route, routesState.loading)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        // CONEXIONES
                        if (connectionsState.loading) {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = colorResource(R.color.medium_red)) }
                        } else if (connectionsState.error != null) {
                            item { InlineErrorBanner(connectionsState.error!!) }
                        } else if (connectionsState.connections.isNotEmpty() && selectedStation!!.transport_type != TransportType.BUS.type) {
                            item { Row { Text(stringResource(R.string.routes_connections), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp)) } }
                            items(connectionsState.connections) { connection ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val context = LocalContext.current
                                    val drawableName =
                                        "${connection.transport_type}_${connection.name.lowercase().replace(" ", "_")}"
                                    val drawableId = remember(connection.name) {
                                        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                                            .takeIf { it != 0 } ?: getDrawableIdByName(context, connection.transport_type)
                                    }

                                    TextButton(
                                        onClick = {
                                            viewModel.viewModelScope.launch {
                                                val selectedConnection = viewModel.fetchSelectedConnection(connection.code)
                                                selectedConnection?.let {
                                                    onConnectionClick(it.code, connection.code)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(drawableId),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(42.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            connection.description,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }


                        // LOCATION
                        item { Row { Text(stringResource(R.string.location), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 32.dp)) } }
                        item {
                            val station = selectedStation!!

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                MiniMap(
                                    transportType = station.transport_type,
                                    latitude = station.latitude,
                                    longitude = station.longitude,
                                    accesses = accessesState.accesses,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(top = 8.dp)
                                )
                                CustomFloatingActionButton(
                                    onClick = { showFullMap = true },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp),
                                    imageVector = Icons.Filled.OpenInFull,
                                    contentDescription = "Abrir mapa"
                                )
                            }
                        }
                        if (accessesState.loading) {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = colorResource(R.color.medium_red)) }
                        } else if (accessesState.error != null) {
                            item { InlineErrorBanner(accessesState.error!!) }
                        } else {
                            items(accessesState.accesses) { access ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(
                                        onClick = {},
                                        enabled = selectedStation!!.transport_type != TransportType.BUS.type
                                    ) {
                                        Icon(
                                            imageVector = if (access.number_of_elevators > 0) Icons.Default.Elevator else Icons.Default.Stairs,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            access.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
