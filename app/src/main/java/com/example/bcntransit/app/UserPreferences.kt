package com.bcntransit.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences private constructor(private val context: Context) {

    companion object {
        val APP_ONBOARDING_COMPLETED = booleanPreferencesKey("app_onboarding_completed")

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[APP_ONBOARDING_COMPLETED] ?: false }

    suspend fun completeOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[APP_ONBOARDING_COMPLETED] = true
        }
    }
}