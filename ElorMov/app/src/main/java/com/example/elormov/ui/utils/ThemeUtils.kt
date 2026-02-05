package com.example.elormov.ui.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    const val PREF_THEME = "theme"
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"

    fun applyTheme(theme: String) {
        when (theme) {
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun getSavedTheme(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString(PREF_THEME, THEME_LIGHT) ?: THEME_LIGHT
    }
}
