package com.example.reto2_elormov

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ir directamente a LoginActivity y cerrar MainActivity
        startActivity(Intent(this, com.example.reto2_elormov.ui.login.LoginActivity::class.java))
        finish()
    }
}