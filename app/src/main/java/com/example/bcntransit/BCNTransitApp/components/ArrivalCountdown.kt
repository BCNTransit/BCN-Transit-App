package com.bcntransit.app.BCNTransitApp.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.bcntransit.app.utils.formatArrivalTime
import kotlinx.coroutines.delay
import com.bcntransit.app.R

@Composable
fun ArrivalCountdown(arrivalEpochSeconds: Long, index: Int) {
    val context = LocalContext.current
    var display by remember { mutableStateOf(formatArrivalTime(context, arrivalEpochSeconds)) }

    LaunchedEffect(arrivalEpochSeconds) {
        while (true) {
            display = formatArrivalTime(context, arrivalEpochSeconds)
            delay(1000)
        }
    }

    Text(
        text = display.text,
        style = if(index == 0) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
        fontStyle = if (!display.showExactTime) FontStyle.Italic else FontStyle.Normal,
        fontWeight = if (display.text == stringResource(R.string.route_arriving)) FontWeight.Bold else FontWeight.Normal
    )
}