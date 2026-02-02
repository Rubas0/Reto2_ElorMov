package com.example.reto2_elormov.ui.alumnos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.AlumnoDTO
import com.example.reto2_elormov.data.dto.repository.AlumnoRepository
import com.example.reto2_elormov.network.RetrofitClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlumnosListActivity : AppCompatActivity() {

    private lateinit var spinnerCiclo: Spinner
    private lateinit var spinnerCurso: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtCount: TextView

    private val viewModel: AlumnosViewModel by lazy {
        AlumnosViewModel(AlumnoRepository(RetrofitClient.elorServApi))
    }

    private var alumnosCompletos: List<AlumnoDTO> = emptyList()
    private var alumnosFiltrados: List<AlumnoDTO> = emptyList()

    private val adapter = AlumnosAdapter { alumno ->
        // Al hacer clic en un alumno, abrir detalle
        val intent = Intent(this, AlumnoDetail::class.java)
        intent.putExtra("ALUMNO_ID", alumno.id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumnos_list)

        spinnerCiclo = findViewById(R.id.spinnerCiclo)
        spinnerCurso = findViewById(R.id.spinnerCurso)
        recyclerView = findViewById(R.id.recyclerViewAlumnos)
        progressBar = findViewById(R.id.progressBar)
        txtCount = findViewById(R.id.txtCount)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupSpinners()

        // Cargar alumnos
        viewModel.loadAlumnos()

        // Observar estados
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is AlumnosState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is AlumnosState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is AlumnosState.Success -> {
                        progressBar.visibility = View.GONE
                        alumnosCompletos = state.alumnos
                        alumnosFiltrados = state.alumnos
                        adapter.submitList(alumnosFiltrados)
                        updateCount()
                    }
                    is AlumnosState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@AlumnosListActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupSpinners() {
        // Spinner Ciclo
        val ciclos = arrayOf("Todos", "DAM", "DAW", "ASIR")
        spinnerCiclo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciclos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Spinner Curso
        val cursos = arrayOf("Todos", "1º", "2º")
        spinnerCurso.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cursos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Listeners
        spinnerCiclo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterAlumnos()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCurso.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterAlumnos()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterAlumnos() {
        val cicloSeleccionado = spinnerCiclo.selectedItem.toString()
        val cursoSeleccionado = spinnerCurso.selectedItem.toString()

        alumnosFiltrados = alumnosCompletos.filter { alumno ->
            val matchCiclo = cicloSeleccionado == "Todos" || alumno.ciclo == cicloSeleccionado
            val matchCurso = cursoSeleccionado == "Todos" || alumno.curso == cursoSeleccionado
            matchCiclo && matchCurso
        }

        adapter.submitList(alumnosFiltrados)
        updateCount()
    }

    private fun updateCount() {
        txtCount.text = "Alumnos encontrados: ${alumnosFiltrados.size}"
    }
}