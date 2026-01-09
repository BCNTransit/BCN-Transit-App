package com.example.bcntransit.api

import android.util.Log
import com.example.bcntransit.app.AuthManager
import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.runBlocking

class AuthInterceptor(private val authManager: AuthManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking { authManager.getAuthToken() }

        val newRequest = if (token != null) {
            Log.d("AUTH SERVICE", "Bearer $token")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            Log.d("AUTH SERVICE", "Token is null")
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}