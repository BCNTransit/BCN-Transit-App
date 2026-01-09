package com.example.bcntransit.api

import com.bcntransit.app.BuildConfig
import com.example.bcntransit.app.AuthManager
import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.runBlocking

class AuthInterceptor(private val authManager: AuthManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val firebaseToken = runBlocking { authManager.getAuthToken() }

        val requestBuilder = originalRequest.newBuilder()
            .header("X-API-Key", BuildConfig.API_KEY)

        if (firebaseToken != null) {
            requestBuilder.header("Authorization", "Bearer $firebaseToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}