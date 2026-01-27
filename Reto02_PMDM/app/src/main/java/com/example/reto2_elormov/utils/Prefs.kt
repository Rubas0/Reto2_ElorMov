package com.example.reto2_elormov.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase para gestionar las preferencias compartidas de la aplicación.
 */
class Prefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ElorMovPrefs", Context.MODE_PRIVATE)

    var lastUsername: String?
        get() = prefs.getString("lastUsername", null)
        set(value) = prefs.edit().putString("lastUsername", value).apply()

    var lastPassword: String?
        get() = prefs.getString("lastPassword", null)
        set(value) = prefs.edit().putString("lastPassword", value).apply()

    var userId: Int
    get() = prefs.getInt("userId", -1)
    set(value) = prefs.edit().putInt("userId", value).apply()

    // Token de autenticación para llamadas a la API
    var authToken: String?
        get() = prefs.getString("authToken", null)
        set(value) = prefs.edit().putString("authToken", value).apply()


    fun clearCredentials() {
        prefs.edit()
            .remove("lastUsername")
            .remove("lastPassword")
            .remove("userId")
            .remove("authToken")
            .apply()
    }
}