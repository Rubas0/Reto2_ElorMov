package com.example.elormov

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.elormov.ui.LoginActivity
import com.example.elormov.ui.utils.LanguageUtils
import com.example.elormov.ui.utils.ThemeUtils
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplicar idioma guardado

        val savedLanguage = LanguageUtils.getSavedLanguage(this)
        LanguageUtils.setLocale(this, savedLanguage)

        // Aplicar tema guardado
        val savedTheme = ThemeUtils.getSavedTheme(this)
        ThemeUtils.applyTheme(savedTheme)

        // Ir directamente a LoginActivity y cerrar MainActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    //========== FUNCION PARA CAMBIAR EL IDIOMA DE LA APP ==========
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}