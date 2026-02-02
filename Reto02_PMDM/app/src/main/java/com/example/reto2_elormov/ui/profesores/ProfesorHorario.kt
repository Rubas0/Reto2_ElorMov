package com.example.reto2_elormov.ui.profesores

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.repository.ProfesorRepository
import com.example.reto2_elormov.network.RetrofitClient
import com.example.reto2_elormov.ui.home.HorarioAdapter
import kotlinx.coroutines.launch

class ProfesorHorario : AppCompatActivity() {

    private lateinit var txtProfesorNombre: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val repository: ProfesorRepository by lazy {
        ProfesorRepository(RetrofitClient.elorServApi)
    }

    private val adapter = HorarioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesor_horario)

        txtProfesorNombre = findViewById(R.id.txtProfesorNombre)
        recyclerView = findViewById(R.id.recyclerViewHorario)
        progressBar = findViewById(R.id.progressBar)

        val profesorId = intent.getIntExtra("PROFESOR_ID", -1)
        val profesorNombre = intent.getStringExtra("PROFESOR_NOMBRE") ?: "Profesor"

        if (profesorId == -1) {
            Toast.makeText(this, "Error: ID profesor no válido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        txtProfesorNombre.text = "Horario de $profesorNombre"

        // RecyclerView en Grid: 6 columnas (1 por día: Lunes-Viernes + cabecera)
        recyclerView.layoutManager = GridLayoutManager(this, 6)
        recyclerView.adapter = adapter

        loadHorario(profesorId)
    }

    private fun loadHorario(profesorId: Int) {
        lifecycleScope.launch {
            try {
                val response = repository.getHorarioProfesor(profesorId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.horario != null) {
                        adapter.submitList(body.horario)
                    } else {
                        Toast.makeText(
                            this@ProfesorHorario,
                            body?.message ?: "No se encontró horario",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProfesorHorario,
                        "Error al cargar horario",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@ProfesorHorario,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}