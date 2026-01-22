package com.example.reto2_elormov.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ElorMovPrefs", Context.MODE_PRIVATE)

    var lastUsername: String?
        get() = prefs.getString("lastUsername", null)
        set(value) = prefs.edit().putString("lastUsername", value).apply()

    var lastPassword: String?
        get() = prefs.getString("lastPassword", null)
        set(value) = prefs.edit().putString("lastPassword", value).apply()

    fun clearCredentials() {
        prefs.edit()
            .remove("lastUsername")
            .remove("lastPassword")
            .apply()
    }
}