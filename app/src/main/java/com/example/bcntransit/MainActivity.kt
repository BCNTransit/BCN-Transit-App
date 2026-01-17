package com.bcntransit.app

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcntransit.app.ui.theme.AppThemeMode
import com.bcntransit.app.ui.theme.BCNTransitTheme
import com.bcntransit.app.util.LanguageManager
import com.bcntransit.app.widget.RegisterViewModel
import com.example.bcntransit.BCNTransitApp.Screens.OnboardingScreen
import com.example.bcntransit.BCNTransitApp.Screens.settings.SettingsViewModelFactory
import org.maplibre.android.MapLibre
import com.example.bcntransit.BCNTransitApp.Screens.settings.SettingsViewModel
import com.example.bcntransit.BCNTransitApp.components.ChangeSystemBarsTheme
import com.example.bcntransit.BCNTransitApp.components.CustomSplashScreen

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()

        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        window.statusBarColor = android.graphics.Color.BLACK

        super.onCreate(savedInstanceState)

        val factory = SettingsViewModelFactory(applicationContext)
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        setContent {
            val state by settingsViewModel.state.collectAsState()
            val registerViewModel: RegisterViewModel = viewModel()
            val isOnboardingCompleted by registerViewModel.isOnboardingCompleted.collectAsState()

            BCNTransitTheme(appTheme = state.themeMode) {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    ChangeSystemBarsTheme(
                        statusBarColor = Color.Transparent,
                        navigationBarColor = Color.Transparent,
                        lightTheme = false
                    )

                    MapLibre.getInstance(this)

                    CustomSplashScreen(
                        onAnimationFinished = {
                            showSplash = false
                        }
                    )
                }  else {
                    ChangeSystemBarsTheme(
                        statusBarColor = MaterialTheme.colorScheme.background,
                        navigationBarColor = MaterialTheme.colorScheme.background,
                        lightTheme = state.themeMode == AppThemeMode.LIGHT || !isSystemInDarkTheme()
                    )

                    if (isOnboardingCompleted == true) {
                        BCNTransitApp()
                    } else {
                        OnboardingScreen(
                            onFinish = {
                                registerViewModel.completeOnboarding()
                                registerViewModel.registerUser()
                            }
                        )
                    }
                }
            }
        }

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}