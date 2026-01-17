package com.example.bcntransit.BCNTransitApp.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ChangeSystemBarsTheme(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    lightTheme: Boolean
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = statusBarColor.toArgb()
            window.navigationBarColor = navigationBarColor.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = lightTheme
            insetsController.isAppearanceLightNavigationBars = lightTheme
        }
    }
}