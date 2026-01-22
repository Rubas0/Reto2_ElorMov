package com.example.reto2_elormov.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.reto2_elormov.network.RetrofitClient
import com.example.reto2_elormov.ui.index.IndexActivity
import com.example.reto2_elormov.utils.Prefs
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.repository.AuthRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var etUser: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button

    private val viewModel: LoginViewModel by lazy {
        val repo = AuthRepository(RetrofitClient.elorServApi)
        LoginViewModel(repo)
    }

    private val prefs: Prefs by lazy { Prefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUser = findViewById(R.id.etUser)
        etPass = findViewById(R.id.etPass)
        btnLogin = findViewById(R.id.btnLogin)

        // Precargar Remember me
        prefs.lastUsername?.let { etUser.setText(it) }
        prefs.lastPassword?.let { etPass.setText(it) }

        btnLogin.setOnClickListener {
            val u = etUser.text.toString().trim()
            val p = etPass.text.toString().trim()

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Usuario y contraseña obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(u, p)
        }

        // Observar estados del ViewModel
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // TODO: Mostrar ProgressBar
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

                        startActivity(Intent(this@LoginActivity, IndexActivity::class.java))
                        finish()
                    }
                    is LoginState.Error -> {
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is LoginState.Idle -> Unit // No hacer nada, es igual a void
                }
            }
        }
    }
}