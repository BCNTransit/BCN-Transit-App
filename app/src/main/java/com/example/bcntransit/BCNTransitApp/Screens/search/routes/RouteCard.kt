package com.bcntransit.app.BCNTransitApp.Screens.search.routes

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bcntransit.app.BCNTransitApp.components.ArrivalCountdown
import com.bcntransit.app.R
import com.bcntransit.app.model.transport.RouteDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RouteCard(route: RouteDto, isLoading: Boolean) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val drawableName = "${route.line_type}_${route.line_name.lowercase().replace(" ", "_")}"
                    val drawableId = remember(route.line_name) {
                        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                    }

                    if (drawableId != 0) {
                        Icon(painter = painterResource(drawableId), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(38.dp))
                    } else {
                        val routeColor = remember(route.color) { parseColorSafe(route.color) }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(routeColor)
                        ) {
                            Text(
                                text = route.line_name.uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(stringResource(R.string.routes_direction), style = MaterialTheme.typography.labelSmall)
                        Text(if(drawableId == 0) "${route.line_name} - ${route.destination}" else route.destination,
                            style = MaterialTheme.typography.titleLarge)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(color = colorResource(R.color.medium_red))
                    }
                }
                else if (route.next_trips.isEmpty()) {
                    Text(stringResource(R.string.routes_not_available))
                } else {
                    val limit = if (isExpanded) 5 else 2
                    val visibleTrips = route.next_trips.take(limit)

                    visibleTrips.forEachIndexed { index, trip ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier.size(24.dp).background(color = colorResource(R.color.next_arrival_background), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text((index+1).toString(), style = MaterialTheme.typography.bodyMedium, color = colorResource(R.color.next_arrival_text))
                            }

                            Spacer(modifier = Modifier.width(12.dp))
                            ArrivalCountdown(arrivalEpochSeconds = trip.arrival_time, index)

                            Spacer(modifier = Modifier.width(12.dp))
                            Text(buildString { if (!trip.platform.isNullOrEmpty()) append("Vía: ${trip.platform}") },
                                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }

                        if (trip.delay_in_minutes != 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp)
                            ) {
                                Spacer(modifier = Modifier.width(48.dp))

                                val formatter = remember {
                                    SimpleDateFormat("HH:mm", Locale.getDefault())
                                }

                                val realTime = Date(trip.arrival_time * 1000)
                                val plannedTime = Date((trip.arrival_time - trip.delay_in_minutes * 60) * 1000)

                                val plannedStr = formatter.format(plannedTime) + "h"
                                val realStr = formatter.format(realTime) + "h"

                                val delaySign = if (trip.delay_in_minutes > 0) "+" else ""
                                val delayStr = " ($delaySign${trip.delay_in_minutes} min)"

                                val delayColor = if (trip.delay_in_minutes > 0) {
                                    colorResource(R.color.medium_red)
                                } else {
                                    colorResource(R.color.dark_green)
                                }

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                textDecoration = TextDecoration.LineThrough,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        ) {
                                            append(plannedStr)
                                        }

                                        append(" → ")

                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        ) {
                                            append(realStr)
                                        }

                                        withStyle(
                                            style = SpanStyle(
                                                color = delayColor
                                            )
                                        ) {
                                            append(delayStr)
                                        }
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    if (route.next_trips.size > 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
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