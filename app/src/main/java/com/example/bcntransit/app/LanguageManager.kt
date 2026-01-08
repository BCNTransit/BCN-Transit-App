package com.bcntransit.app.util

import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageManager {
    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_LANG = "app_language"

    fun setLocale(context: Context, languageCode: String) {
        // 1. Siempre guardamos en SharedPreferences como respaldo
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, languageCode).apply()

        // 2. Aplicamos según versión
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: El sistema lo hace todo
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            // Android 12 e inferior: AppCompatDelegate ayuda a recrear,
            // pero el trabajo sucio lo haremos en el attachBaseContext
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    // Función auxiliar para leer la preferencia guardada
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG, Locale.getDefault().language) ?: "es"
    }

    // Esta función envuelve el contexto con el idioma forzado (CRUCIAL para ComponentActivity)
    fun wrapContext(context: Context): Context {
        val language = getSavedLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}