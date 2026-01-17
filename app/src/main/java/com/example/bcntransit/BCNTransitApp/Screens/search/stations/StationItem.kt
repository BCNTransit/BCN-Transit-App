package com.bcntransit.app.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bcntransit.app.R
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.model.transport.StationDto
import com.bcntransit.app.model.FavoriteDto
import kotlinx.coroutines.launch

@Composable
fun StationItem(
    station: StationDto,
    isFirst: Boolean,
    isLast: Boolean,
    lineColor: Color,
    lineType: String,
    onClick: () -> Unit
) {
    val verticalPadding = 16.dp
    val circleSize = 16.dp
    val lineWidth = 3.dp
    val circleTopOffset = verticalPadding + 4.dp
    val context = LocalContext.current

    val visibleConnections = remember(station.connections) {
        station.connections?.filter {
            it.transport_type.equals(station.transport_type, ignoreCase = true) && it.name != station.line_name
        } ?: emptyList()
    }

    val hasVisibleConnections = visibleConnections.isNotEmpty()

    var isFavorite by remember { mutableStateOf(false) }
    var isLoadingFavorite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(station.code) {
        try {
            isFavorite = ApiClient.userApiService.userHasFavorite(
                type = lineType,
                itemId = station.code
            )
        } catch (e: Exception) { e.printStackTrace() }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(56.dp)
                .fillMaxHeight()
        ) {
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(lineWidth)
                        .height(circleTopOffset + (circleSize / 2))
                        .background(lineColor.copy(alpha = 0.3f))
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = circleTopOffset + (circleSize / 2))
                        .width(lineWidth)
                        .fillMaxHeight()
                        .background(lineColor.copy(alpha = 0.3f))
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = circleTopOffset)
                    .size(circleSize)
                    .background(
                        color = if (hasVisibleConnections) MaterialTheme.colorScheme.surface else lineColor,
                        shape = CircleShape
                    )
                    .border(
                        width = if (hasVisibleConnections) 3.dp else 0.dp,
                        color = if (hasVisibleConnections) lineColor else Color.Transparent,
                        shape = CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = verticalPadding, bottom = verticalPadding, end = 8.dp)
        ) {
            Text(
                text = station.name_with_emoji ?: station.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            val hasAlerts = station.has_alerts
            val alertText = if (hasAlerts) stringResource(R.string.incidents) else stringResource(R.string.normal_service)
            val alertColor = if (hasAlerts) colorResource(R.color.medium_red) else colorResource(R.color.dark_green)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(alertColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = alertText,
                    style = MaterialTheme.typography.bodySmall,
                    color = alertColor
                )
            }

            if (hasVisibleConnections) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    visibleConnections.forEach { connection ->
                        val lineNameClean = connection.name.lowercase().replace(" ", "_").replace("-", "_")
                        val drawableName = "${connection.transport_type}_$lineNameClean"
                        val drawableId = remember(drawableName) {
                            context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                        }

                        if (drawableId != 0) {
                            Icon(
                                painter = painterResource(id = drawableId),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(top = verticalPadding, bottom = verticalPadding, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        if (isLoadingFavorite) return@launch
                        isLoadingFavorite = true
                        try {
                            if (isFavorite) {
                                ApiClient.userApiService.deleteUserFavorite(lineType, station.code)
                                isFavorite = false
                            } else {
                                ApiClient.userApiService.addUserFavorite(
                                    FavoriteDto(
                                        type = lineType,
                                        line_code = station.line_code,
                                        line_name = station.line_name,
                                        line_name_with_emoji = station.line_name_with_emoji ?: "",
                                        station_code = station.code,
                                        station_name = station.name,
                                        station_group_code = station.CODI_GRUP_ESTACIO.toString(),
                                        coordinates = listOf(station.latitude, station.longitude)
                                    )
                                )
                                isFavorite = true
                            }
                        } catch(e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoadingFavorite = false
                        }
                    }
                }
            ) {
                if (isLoadingFavorite) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.medium_red)
                    )
                } else {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) colorResource(R.color.red) else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}