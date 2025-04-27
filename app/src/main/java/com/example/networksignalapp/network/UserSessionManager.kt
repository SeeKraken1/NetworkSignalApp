package com.example.networksignalapp.network

import android.content.Context

class UserSessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}