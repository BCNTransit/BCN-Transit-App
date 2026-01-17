import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NorthWest
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bcntransit.app.R
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.model.transport.NearbyStation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    initialQuery: String = "",
    onSearch: (String, String, String) -> Unit,
    enabled: Boolean,
    onActiveChange: (Boolean) -> Unit = {}
) {
    var query by remember { mutableStateOf(initialQuery) }
    var active by remember { mutableStateOf(false) }

    var isSearching by remember { mutableStateOf(false) }
    var noResults by remember { mutableStateOf(false) }
    var stations by remember { mutableStateOf<List<NearbyStation>>(emptyList()) }

    var searchHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingHistory by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val suggestions = remember(stations) { stations.take(20) }

    val brandColor = MaterialTheme.colorScheme.primary

    val searchBarColors = SearchBarDefaults.colors(
        containerColor = if (active) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerLowest,

        dividerColor = Color.Transparent,

        inputFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )

    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(300)
            .collectLatest { q ->
                if (q.isNotBlank() && q.length >= 3) {
                    isSearching = true
                    try {
                        stations = ApiClient.resultsApiService.getResultsByName(q)
                        noResults = stations.isEmpty()
                    } catch (e: Exception) {
                        stations = emptyList()
                        noResults = true
                    } finally {
                        isSearching = false
                    }
                } else {
                    stations = emptyList()
                    noResults = false
                    isSearching = false
                }
            }
    }

    LaunchedEffect(active) {
        if (active && query.isBlank()) {
            isLoadingHistory = true
            try {
                searchHistory = ApiClient.resultsApiService.getSearchHistory()
            } catch (e: Exception) {
                searchHistory = emptyList()
            } finally {
                isLoadingHistory = false
            }
        }
    }

    if (enabled) {
        SearchBar(
            query = query,
            onQueryChange = { if (enabled) query = it },
            onSearch = {},
            active = active,
            onActiveChange = { isActive ->
                if (enabled) {
                    active = isActive
                    onActiveChange(isActive)
                    if (!isActive) query = ""
                }
            },
            enabled = enabled,
            windowInsets = WindowInsets(0.dp),
            tonalElevation = 0.dp,
            shadowElevation = if (active) 0.dp else 6.dp,
            colors = searchBarColors,
            leadingIcon = {
                Icon(
                    imageVector = if (query.isEmpty() && active) Icons.Rounded.History else Icons.Rounded.Search,
                    contentDescription = "Buscar",
                    tint = brandColor
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.map_search_station),
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (active) 0.dp else 16.dp, vertical = if (active) 0.dp else 20.dp)
        ) {
            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = brandColor)
                }
            }
            else if (query.isNotBlank()) {
                if (stations.isEmpty() && noResults) {
                    EmptyStateMessage(
                        icon = Icons.Rounded.Search,
                        message = "No hemos encontrado estaciones con ese nombre."
                    )
                } else {
                    ResultsList(
                        items = suggestions,
                        enabled = enabled,
                        onItemClick = { station ->
                            query = station.station_name
                            active = false
                            onActiveChange(false)
                            stations = emptyList()
                            coroutineScope.launch {
                                try {
                                    onSearch(station.type, station.line_code ?: "", station.station_code)
                                } catch (e: Exception) { }
                            }
                        }
                    )
                }
            }
            else {
                if (isLoadingHistory) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.outline)
                    }
                } else if (searchHistory.isNotEmpty()) {
                    SimpleHistoryList(
                        historyItems = searchHistory,
                        onHistoryClick = { historyText -> query = historyText }
                    )
                } else {
                    EmptyStateMessage(
                        icon = Icons.Rounded.Train,
                        message = "Busca por nombre de parada, línea o código numérico."
                    )
                }
            }
        }
    }
}


@Composable
fun SimpleHistoryList(
    historyItems: List<String>,
    onHistoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().imePadding(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Búsquedas recientes",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        items(historyItems) { text ->
            ListItem(
                headlineContent = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onHistoryClick(text) }) {
                        Icon(
                            imageVector = Icons.Rounded.NorthWest,
                            contentDescription = "Rellenar",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHistoryClick(text) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun ResultsList(
    items: List<NearbyStation>,
    enabled: Boolean,
    onItemClick: (NearbyStation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().imePadding(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items) { station ->
            ListItem(
                headlineContent = {
                    Text(
                        text = station.station_name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(
                        text = "Código: ${station.station_code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    val context = LocalContext.current
                    val drawableName = "${station.type}_${station.line_name?.lowercase()?.replace(" ", "_")}"
                    val drawableId = remember(station.line_name) {
                        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                            .takeIf { it != 0 }
                            ?: context.resources.getIdentifier(station.type, "drawable", context.packageName)
                    }

                    Icon(
                        painter = painterResource(drawableId),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onItemClick(station) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun EmptyStateMessage(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}