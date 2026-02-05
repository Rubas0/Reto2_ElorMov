package com.example.elormov.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elormov.R
import com.example.elormov.retrofit.client.RetrofitClient
import com.example.elormov.retrofit.entities.ResetPasswordRequestDTO
import com.example.elormov.retrofit.entities.ResetPasswordResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var inputEmail: EditText
    private lateinit var btnReset: Button
    private val api = RetrofitClient.elorServInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        inputEmail = findViewById(R.id.etEmail)
        btnReset = findViewById(R.id.btnReset)

        btnReset.setOnClickListener {
            val email = inputEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.introduce_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            enviarCorreo(email)

        }

    }
    fun enviarCorreo(email : String){
        try {
            val passwordResetDTO = ResetPasswordRequestDTO(email)
            api.resetPassword(passwordResetDTO).enqueue(object : Callback<ResetPasswordResponseDTO>{
                override fun onResponse(
                    call: Call<ResetPasswordResponseDTO>,
                    response: Response<ResetPasswordResponseDTO>
                ){
                    if (response.isSuccessful){ // codigo de respuesta [200..300]
                        val passwdResetResponse = response.body()
                        Toast.makeText(this@ResetPasswordActivity, passwdResetResponse?.message, Toast.LENGTH_SHORT).show()
                        finish() // Volver al login
                    }else{ //el codigo de respuesta no es [200..300]
                        val passwdResetResponse = response.body()
                        Toast.makeText(this@ResetPasswordActivity, passwdResetResponse?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResetPasswordResponseDTO?>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordActivity, getString(R.string.error_conexion_api), Toast.LENGTH_LONG).show()
                    println("Error completo: ${t.printStackTrace()}")
                    t.printStackTrace()
                }
            })
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.error_try_catch), Toast.LENGTH_SHORT).show()
            println("Error completo: ${e.printStackTrace()}")
        }
    }
}