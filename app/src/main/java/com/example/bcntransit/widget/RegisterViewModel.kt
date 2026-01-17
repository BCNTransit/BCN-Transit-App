package com.bcntransit.app.widget

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bcntransit.app.api.ApiClient
import com.bcntransit.app.data.UserPreferences
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val userPrefs = UserPreferences.getInstance(context)
    val isOnboardingCompleted = userPrefs.isOnboardingCompleted
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    fun completeOnboarding() {
        viewModelScope.launch {
            userPrefs.completeOnboarding()
        }
    }
    fun registerUser() {
        viewModelScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                val body = mapOf(
                    "fcmToken" to fcmToken
                )
                ApiClient.userApiService.registerUser(body)

            } catch (e: retrofit2.HttpException) {
                Log.e("UserRegister", "HTTP error: ${e.code()} - ${e.message()}")
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("UserRegister", "Timeout error: ${e.message}")
            } catch (e: Exception) {
                Log.e("UserRegister", "Unexpected error: ${e.localizedMessage}", e)
            }
        }
    }
}