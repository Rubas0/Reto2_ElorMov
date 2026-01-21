package com.example.reto2_elormov.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("elor_prefs", Context.MODE_PRIVATE)

    var lastUsername: String?
        get() = prefs.getString("last_username", null)
        set(value) = prefs.edit().putString("last_username", value).apply()

    var lastPassword: String?
        get() = prefs.getString("last_password", null)
        set(value) = prefs.edit().putString("last_password", value).apply()
}