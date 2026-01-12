package com.bcntransit.app

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.bcntransit.app.ui.theme.BCNTransitTheme
import com.bcntransit.app.util.LanguageManager
import com.example.bcntransit.BCNTransitApp.Screens.settings.SettingsViewModelFactory
import org.maplibre.android.MapLibre
import com.example.bcntransit.BCNTransitApp.Screens.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    private var isReady = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        //val splashScreen = installSplashScreen()

        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        val factory = SettingsViewModelFactory(applicationContext)
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        setContent {
            val state by settingsViewModel.state.collectAsState()

            BCNTransitTheme(appTheme = state.themeMode) {
                BCNTransitApp()
            }
        }

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}