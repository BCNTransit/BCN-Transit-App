package com.bcntransit.app

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bcntransit.app.util.LanguageManager
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalar SplashScreen
        //val splashScreen = installSplashScreen()

        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        setContent {
            BCNTransitApp()
        }

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}