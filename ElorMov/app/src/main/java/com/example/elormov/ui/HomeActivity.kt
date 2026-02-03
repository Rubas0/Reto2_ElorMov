package com.example.elormov.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.elormov.R
import com.example.elormov.retrofit.entities.UserDTO

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // ============ Obtener el usuario completo del Intent ============
        val extras: Bundle? = intent.extras
        val user : UserDTO = extras?.getSerializable("usuario") as UserDTO
        Toast.makeText(this, "Bienvenido, ${user.nombre}", Toast.LENGTH_LONG).show()


    }
}