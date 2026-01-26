package com.example.reto2_elormov.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.reto2_elormov.data.dto.repository.AuthRepository
import com.example.reto2_elormov.network.RetrofitClient
import com.example.reto2_elormov.ui.index.IndexActivity
import com.example.reto2_elormov.ui.login.LoginViewModel
import com.example.reto2_elormov.utils.Prefs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.reto2_elormov.R
import com.example.reto2_elormov.ui.profile.ProfileActivity

class LoginActivity : ComponentActivity() {

    // ViewModel con inyección manual
    private val viewModel: LoginViewModel by lazy {
        val repo = AuthRepository(RetrofitClient.elorServApi)
        LoginViewModel(repo)
    }

    // Remember me (solo autocompletar, no auto-login)
    private val prefs: Prefs by lazy { Prefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // ⚠️ INICIALIZA LAS VISTAS AQUÍ, DESPUÉS DE setContentView
        val etUser = findViewById<EditText>(R.id.etUser)
        val etPass = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtForgotPassword = findViewById<TextView>(R.id.txtForgotPassword)

        // Cargar credenciales guardadas (si existen)
        etUser.setText(prefs.lastUsername.orEmpty())
        etPass.setText(prefs.lastPassword.orEmpty())

        // Botón Login
        btnLogin.setOnClickListener {
            val u = etUser.text.toString().trim()
            val p = etPass.text.toString().trim()

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Usuario y contraseña obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(u, p)
        }

        // Enlace "Olvidé mi contraseña"
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // Observar estados del ViewModel
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // Mostrar Progress si quieres
                    }
                    is LoginState.Success -> {
                        // Guardar Remember me
                        prefs.lastUsername = etUser.text.toString()
                        prefs.lastPassword = etPass.text.toString()

                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido ${state.username}",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                        finish()
                    }
                    is LoginState.Error -> {
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is LoginState.Idle -> Unit
                }
            }
        }
    }
}