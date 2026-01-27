package com.example.reto2_elormov.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Activity principal que muestra el horario semanal del usuario (profesor o alumno).
 *
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var horarioAdapter: HorarioAdapter

    private lateinit var recyclerViewHorario: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewEmpty: TextView
    private lateinit var rootView: View

    private var usuarioId: Int = -1
    private var esProfesor: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializar vistas
        rootView = findViewById(android.R.id.content) // Vista raíz para barra de notificaciones
        recyclerViewHorario = findViewById(R.id.recyclerViewHorario)
        progressBar = findViewById(R.id.progressBar)
        textViewEmpty = findViewById(R.id.textViewEmpty)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Obtener datos del intent
        usuarioId = intent.getIntExtra("USUARIO_ID", -1)
        esProfesor = intent.getBooleanExtra("ES_PROFESOR", false)

        setupRecyclerView()
        setupObservers()

        if (usuarioId != -1) {
            cargarHorario()
        } else {
            mostrarError("Error: Usuario no válido")
        }
    }

    private fun setupRecyclerView() { // Configurar RecyclerView y su adaptador
        horarioAdapter = HorarioAdapter { horario ->
            horario.reunion?.let { reunion ->
                mostrarDetalleReunion(reunion)
            }
        }

        recyclerViewHorario.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 6)
            adapter = horarioAdapter
        }
    }

    private fun setupObservers() { // Observar cambios en el ViewModel, es decir, el horario y el estado de carga
        lifecycleScope.launch {
            viewModel.horario.collect { result ->
                result?.fold(
                    onSuccess = { horarioSemanal ->
                        horarioAdapter.submitList(horarioSemanal.horario)
                        progressBar.visibility = View.GONE
                        recyclerViewHorario.visibility = View.VISIBLE
                        textViewEmpty.visibility = View.GONE
                    },
                    onFailure = { error ->
                        progressBar.visibility = View.GONE
                        recyclerViewHorario.visibility = View.GONE
                        textViewEmpty.visibility = View.VISIBLE
                        textViewEmpty.text = error.message
                        mostrarError(error.message ?: "Error desconocido")
                    }
                )
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun cargarHorario() {
        viewModel.cargarHorario(usuarioId, esProfesor)
    }

    private fun mostrarDetalleReunion(reunion: com.example.reto2_elormov.data.dto.ReunionDto) {
        Snackbar.make(
            rootView,
            "Reunión: ${reunion.titulo} - ${reunion.estado}",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun mostrarError(mensaje: String) {
        Snackbar.make(rootView, mensaje, Snackbar.LENGTH_LONG).show()
    }
}
