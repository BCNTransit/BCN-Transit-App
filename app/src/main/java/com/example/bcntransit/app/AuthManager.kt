package com.example.bcntransit.app

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun getAuthToken(): String? {
        val user = auth.currentUser

        return if (user == null) {
            try {
                val authResult = auth.signInAnonymously().await()
                authResult.user?.getIdToken(true)?.await()?.token
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            try {
                user.getIdToken(false).await().token
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}