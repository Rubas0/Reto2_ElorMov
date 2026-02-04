package com.example.elormov.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.elormov.R
import com.example.elormov.retrofit.client.RetrofitClient
import com.example.elormov.retrofit.entities.LoginRequestDTO
import com.example.elormov.retrofit.entities.LoginResponseDTO
import com.example.elormov.retrofit.entities.UserDTO
import com.example.elormov.retrofit.utils.Prefs
import com.example.elormov.ui.home.AlumnoHome
import com.example.elormov.ui.home.ProfesorHome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {

    private lateinit var prefs: Prefs
    private val api = RetrofitClient.elorServInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val userInput = findViewById<EditText>(R.id.userInput)
        val passInput = findViewById<EditText>(R.id.passInput)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtForgotPassword = findViewById<TextView>(R.id.txtForgotPassword)

        // Cargar credenciales guardadas (si existen)
        prefs = Prefs(this)
        userInput.setText(prefs.lastUsername.orEmpty())
        passInput.setText(prefs.lastPassword.orEmpty())

        // Botón Login
        btnLogin.setOnClickListener {
            val username = userInput.text.toString().trim()
            val passwd = passInput.text.toString().trim()

            if (username.isEmpty() || passwd.isEmpty()) {
                Toast.makeText(this, "Usuario y contraseña obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(username, passwd)
        }

        // Enlace "Olvidé mi contraseña"
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    fun login(username: String, passwd: String) {
        val loginRequest = LoginRequestDTO(username, passwd)
        api.login(loginRequest).enqueue(object : Callback<LoginResponseDTO> {
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                if (response.isSuccessful) { // codigo de respuesta [200..300]
                    val loginResponse = response.body()
                    val user = loginResponse?.user
                    // Guardar credenciales en shared preferences
                    prefs.lastUsername = username
                    prefs.lastPassword = passwd
                    comprobarTipoUser(user)
                } else { //el codigo de respuesta no es [200..300]
                    Toast.makeText(
                        this@LoginActivity,
                        "El usuario o la contraseña son incorrectas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO?>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error de conexión con la API",
                    Toast.LENGTH_LONG
                ).show()
                println("Error completo: ${t.printStackTrace()}")
                t.printStackTrace()
            }
        })
    }

    fun comprobarTipoUser(user: UserDTO?) {
        when (user?.tipo?.name) {
            "profesor" -> {
                val intent = Intent(this, ProfesorHome::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
            }

            "alumno" -> {
                val intent = Intent(this, AlumnoHome::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
            }

            else -> {
                Toast.makeText(this, "Usuario no permitido en la app", Toast.LENGTH_SHORT).show()
            }
        }
    }
}