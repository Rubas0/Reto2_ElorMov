package com.example.elormov.retrofit.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Clase para gestionar las preferencias compartidas de la aplicación.
 */
class Prefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ElorMovPrefs", Context.MODE_PRIVATE)

    var lastUsername: String?
        get() = prefs.getString("lastUsername", null)
        set(value) = prefs.edit { putString("lastUsername", value) }

    var lastPassword: String?
        get() = prefs.getString("lastPassword", null)
        set(value) = prefs.edit { putString("lastPassword", value) }

    var userId: Int
        get() = prefs.getInt("userId", -1)
        set(value) = prefs.edit { putInt("userId", value) }

    // Token de autenticación para llamadas a la API
    var authToken: String?
        get() = prefs.getString("authToken", null)
        set(value) = prefs.edit { putString("authToken", value) }


    fun clearCredentials() {
        prefs.edit {
            remove("lastUsername")
                .remove("lastPassword")
                .remove("userId")
                .remove("authToken")
        }
    }
}