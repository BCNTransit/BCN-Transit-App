package com.bcntransit.app.utils

import android.content.Context
import com.bcntransit.app.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class ArrivalDisplay(
    val text: String,
    val showExactTime: Boolean
)

// 1. Añadimos 'context' como parámetro
fun formatArrivalTime(context: Context, arrivalEpochSeconds: Long): ArrivalDisplay {
    val nowMillis = System.currentTimeMillis()
    val arrivalMillis = arrivalEpochSeconds * 1000

    // 2. Pasamos el context a la función privada
    val remaining = remainingTime(context, arrivalEpochSeconds)

    // Lógica para decidir si mostramos hora exacta (> 1 hora)
    val showExactTime = arrivalMillis - nowMillis > TimeUnit.HOURS.toMillis(1)

    if (!showExactTime) return ArrivalDisplay(remaining, false)

    val nowCal = Calendar.getInstance()
    val arrivalCal = Calendar.getInstance().apply { timeInMillis = arrivalMillis }
    val showDate = nowCal.get(Calendar.YEAR) != arrivalCal.get(Calendar.YEAR) ||
            nowCal.get(Calendar.DAY_OF_YEAR) != arrivalCal.get(Calendar.DAY_OF_YEAR)

    val pattern = if (showDate) "dd/MM HH:mm" else "HH:mm"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    val text = formatter.format(Date(arrivalMillis)) + "h"

    return ArrivalDisplay(text, true)
}

// 3. Añadimos 'context' aquí también
private fun remainingTime(context: Context, arrivalEpochSeconds: Long): String {
    val nowMs = System.currentTimeMillis()
    val arrivalMs = arrivalEpochSeconds * 1000
    val diffMs = arrivalMs - nowMs

    return if (diffMs <= 40000) {
        // SOLUCIÓN: Usamos context.getString() para obtener el texto real
        context.getString(R.string.route_arriving)
    } else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diffMs) % 60
        if (minutes > 0) "$minutes min ${seconds}s"
        else "${seconds}s"
    }
}