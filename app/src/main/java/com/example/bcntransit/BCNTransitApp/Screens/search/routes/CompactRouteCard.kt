package com.example.bcntransit.BCNTransitApp.Screens.search.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bcntransit.app.R
import com.bcntransit.app.model.transport.RouteDto
import com.bcntransit.app.utils.formatArrivalTime

@Composable
fun CompactRouteCard(
    route: RouteDto,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val specificDrawableId = remember(route.line_name) {
        val drawableName = "${route.line_type}_${route.line_name.lowercase().replace(" ", "_")}"
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    }

    val iconSize = 42.dp

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalles",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(16.dp)
                    .alpha(0.5f),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (specificDrawableId != 0) {
                    Icon(
                        painter = painterResource(specificDrawableId),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(iconSize)
                    )
                } else {
                    val routeColor = remember(route.color) { parseColorSafe(route.color) }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(iconSize)
                            .clip(CircleShape)
                            .background(routeColor)
                    ) {
                        Text(
                            text = route.line_name.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.medium_red)
                    )
                } else {
                    val nextTrip = route.next_trips.firstOrNull()

                    if (nextTrip != null) {
                        val nowSeconds = System.currentTimeMillis() / 1000
                        val minutes = ((nextTrip.arrival_time - nowSeconds) / 60).coerceAtLeast(0)

                        val timeText = if (minutes == 0L) stringResource(R.string.route_arriving) else "${minutes} min"
                        val textColor = if (minutes < 5) colorResource(R.color.medium_red) else MaterialTheme.colorScheme.onSurface

                        Text(
                            text = timeText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.bicing_out_of_service),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

private fun parseColorSafe(hexString: String?): Color {
    if (hexString.isNullOrEmpty()) return Color.Gray
    return try {
        val finalHex = if (hexString.startsWith("#")) hexString else "#$hexString"
        Color(android.graphics.Color.parseColor(finalHex))
    } catch (e: Exception) {
        Color.Gray
    }
}