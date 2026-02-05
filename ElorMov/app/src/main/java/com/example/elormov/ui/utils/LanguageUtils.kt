package com.example.elormov.ui.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageUtils {

    /**
     * Cambia el idioma de la aplicación a través del código de idioma proporcionado (por ejemplo, "es" para español, "en" para inglés).
     * @param context El contexto de la aplicación para acceder a los recursos y configuraciones.
     * @param languageCode El código del idioma al que se desea cambiar (por ejemplo, "es" para español, "en" para inglés).
     */
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * Recupera el idioma guardado en SharedPreferences. Si no se encuentra, devuelve "es" (español) por defecto.
     * @param context El contexto de la aplicación para acceder a SharedPreferences.
     */
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("lang", "es") ?: "es"
    }
}