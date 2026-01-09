package com.bcntransit.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("HardwareIds")
fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)!!
}

fun getUserId(): String {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "No user connected"
    return userId
}