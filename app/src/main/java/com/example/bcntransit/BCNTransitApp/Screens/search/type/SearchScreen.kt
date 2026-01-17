package com.bcntransit.app.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bcntransit.app.R
import com.bcntransit.app.data.enums.SearchOption
import com.bcntransit.app.data.enums.TransportType
import com.example.bcntransit.BCNTransitApp.components.CustomTopBar

@Composable
fun SearchScreen(
    onTypeSelected: (String) -> Unit
) {
    val searchItems = listOf(
        Triple(TransportType.METRO.type.capitalize(), stringResource(R.string.lines_and_stations, TransportType.METRO.type), R.drawable.metro),
        Triple(TransportType.BUS.type.capitalize(), stringResource(R.string.lines_and_stops, TransportType.BUS.type), R.drawable.bus),
        Triple(TransportType.TRAM.type.capitalize(), stringResource(R.string.lines_and_stops, TransportType.TRAM.type), R.drawable.tram),
        Triple(TransportType.RODALIES.type.capitalize(), stringResource(R.string.lines_and_stations, TransportType.RODALIES.type), R.drawable.rodalies),
        Triple(TransportType.FGC.type.uppercase(), stringResource(R.string.lines_and_stations, TransportType.FGC.type.uppercase()), R.drawable.fgc),
    )

    Scaffold(
        topBar = {
            CustomTopBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Route,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.menu_lines),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                },
                onBackClick = { },
                showBackButton = false
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                item {
                    Text(
                        text = stringResource(R.string.lines_select_transport_type),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.lines_transport_types),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(searchItems) { index, item ->
                    SearchItem(
                        iconRes = item.third,
                        title = item.first,
                        description = item.second,
                        onClick = {
                            val option = SearchOption.entries[index]
                            onTypeSelected(option.name)
                        }
                    )
                }
            }
        }
    )
}
