package com.example.bcntransit.BCNTransitApp.Screens.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class SettingsState(
    val receiveAlerts: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.LIGHT
)

class SettingsViewModel(
    private val context: Context,
    private val androidId: String
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    init {
        observeLocalSettings()
        fetchRemoteSettings()
    }

    private fun observeLocalSettings() {
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences ->
                    val modeName = preferences[THEME_MODE_KEY] ?: AppThemeMode.LIGHT.name
                    try {
                        AppThemeMode.valueOf(modeName)
                    } catch (e: IllegalArgumentException) {
                        AppThemeMode.LIGHT
                    }
                }
                .collect { themeMode ->
                    _state.value = _state.value.copy(themeMode = themeMode)
                }
        }
    }

    private fun fetchRemoteSettings() {
        viewModelScope.launch {
            try {
                val receiveAlerts = ApiClient.userApiService.getUserNotificationsConfiguration()
                _state.value = _state.value.copy(receiveAlerts = receiveAlerts)
            } catch (e: Exception) {
                _state.value = _state.value.copy(receiveAlerts = true)
            }
        }
    }

    fun toggleReceiveAlerts(enabled: Boolean) {
        _state.value = _state.value.copy(receiveAlerts = enabled)

        viewModelScope.launch {
            try {
                ApiClient.userApiService.toggleUserNotifications(enabled)
            } catch (e: Exception) {
                _state.value = _state.value.copy(receiveAlerts = !enabled)
            }
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.name
            }
            _state.value = _state.value.copy(themeMode = mode)
        }
    }
}

class SettingsViewModelFactory(
    private val context: Context,
    private val androidId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(context, androidId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}