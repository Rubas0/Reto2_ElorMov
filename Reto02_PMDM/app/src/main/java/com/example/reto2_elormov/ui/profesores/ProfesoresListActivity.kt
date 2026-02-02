package com.example.reto2_elormov.ui.profesores

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.ProfesorDTO
import com.example.reto2_elormov.data.dto.repository.ProfesorRepository
import com.example.reto2_elormov.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfesoresListActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtCount: TextView

    private val viewModel: ProfesoresViewModel by lazy {
        ProfesoresViewModel(ProfesorRepository(RetrofitClient.elorServApi))
    }

    private var profesoresCompletos: List<ProfesorDTO> = emptyList()
    private var profesoresFiltrados: List<ProfesorDTO> = emptyList()

    private val adapter = ProfesoresAdapter { profesor ->
        // Al hacer clic en un profesor, abrir su horario
        val intent = Intent(this, ProfesorHorario::class.java)
        intent.putExtra("PROFESOR_ID", profesor.id)
        intent.putExtra("PROFESOR_NOMBRE", "${profesor.nombre} ${profesor.apellidos}")
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profesores_list)

        etSearch = findViewById(R.id.etSearch)
        recyclerView = findViewById(R.id.recyclerViewProfesores)
        progressBar = findViewById(R.id.progressBar)
        txtCount = findViewById(R.id.txtCount)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupSearch()

        // Cargar profesores
        viewModel.loadProfesores()

        // Observar estados
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is ProfesoresState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is ProfesoresState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is ProfesoresState.Success -> {
                        progressBar.visibility = View.GONE
                        profesoresCompletos = state.profesores
                        profesoresFiltrados = state.profesores
                        adapter.submitList(profesoresFiltrados)
                        updateCount()
                    }
                    is ProfesoresState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@ProfesoresListActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProfesores(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProfesores(query: String) {
        profesoresFiltrados = if (query.isEmpty()) {
            profesoresCompletos
        } else {
            profesoresCompletos.filter { profesor ->
                val nombreCompleto = "${profesor.nombre} ${profesor.apellidos}".lowercase()
                nombreCompleto.contains(query.lowercase())
            }
        }
        adapter.submitList(profesoresFiltrados)
        updateCount()
    }

    private fun updateCount() {
        txtCount.text = "Profesores encontrados: ${profesoresFiltrados.size}"
    }
}