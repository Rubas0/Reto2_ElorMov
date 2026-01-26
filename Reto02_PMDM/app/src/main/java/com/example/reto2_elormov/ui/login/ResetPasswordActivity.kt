package com.example.reto2_elormov.ui.login


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.repository.AuthRepository
import com.example.reto2_elormov.network.RetrofitClient
import kotlinx.coroutines.launch

class ResetPasswordActivity : ComponentActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnReset: Button

    private val repository: AuthRepository by lazy {
        AuthRepository(RetrofitClient.elorServApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etEmail = findViewById(R.id.etEmail)
        btnReset = findViewById(R.id.btnReset)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Introduce tu email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = repository.resetPassword(email)

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.success == true) {
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                body.message ?: "Contraseña enviada a tu correo",
                                Toast.LENGTH_LONG
                            ).show()
                            finish() // Volver al login
                        } else {
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                body?.message ?: "Email no encontrado",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Error HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}