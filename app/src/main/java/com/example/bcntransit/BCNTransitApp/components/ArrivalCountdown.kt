package com.bcntransit.app.BCNTransitApp.components

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.bcntransit.app.R
import kotlinx.coroutines.delay
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun ArrivalCountdown(
    arrivalEpochSeconds: Long,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    showSeconds: Boolean = true,
    isBold: Boolean = false,
    isWarning: Boolean = false,
    defaultColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val context = LocalContext.current

    var timeText by remember { mutableStateOf("") }
    var isUrgentTime by remember { mutableStateOf(false) }

    LaunchedEffect(arrivalEpochSeconds, showSeconds) {
        while (true) {
            val nowSeconds = System.currentTimeMillis() / 1000
            val diffSeconds = arrivalEpochSeconds - nowSeconds

            timeText = calculateFormattedText(context, arrivalEpochSeconds, diffSeconds, showSeconds)

            isUrgentTime = diffSeconds < 60

            val delayMillis = if (diffSeconds > 3600) {
                val secondsUntilCountdown = diffSeconds - 3600
                (secondsUntilCountdown.coerceIn(1, 30) * 1000)
            } else {
                1000L
            }
            delay(delayMillis)
        }
    }

    val finalColor = if (isWarning && isUrgentTime) {
        colorResource(R.color.logo_red)
    } else {
        defaultColor
    }

    Text(
        text = timeText,
        style = style,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        color = finalColor
    )
}

private fun calculateFormattedText(
    context: Context,
    arrivalEpochSeconds: Long,
    diffSeconds: Long,
    showSeconds: Boolean
): String {
    if (diffSeconds > 3600) {
        val date = Date(arrivalEpochSeconds * 1000)
        return android.text.format.DateFormat.getTimeFormat(context).format(date) + "h"
    }

    if (diffSeconds <= 40) {
        return context.getString(R.string.route_arriving)
    }

    val minutes = TimeUnit.SECONDS.toMinutes(diffSeconds)
    val seconds = diffSeconds % 60

    val strMin = "min"
    val strSec = "s"

    return if (showSeconds) {
        if (minutes == 0L) {
            "$seconds $strSec"
        } else {
            "$minutes $strMin $seconds $strSec"
        }
    } else {
        if (minutes == 0L) {
            context.getString(R.string.route_arriving)
        } else {
            "$minutes $strMin"
        }
    }
}