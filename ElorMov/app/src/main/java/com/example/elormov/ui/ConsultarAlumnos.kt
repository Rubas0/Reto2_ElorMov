package com.example.elormov.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.retrofit.client.RetrofitClient
import com.example.elormov.retrofit.entities.UserDTO
import com.example.elormov.ui.adapters.AlumnosAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var user: UserDTO
private lateinit var recyclerView: RecyclerView
private lateinit var alumnosAdapter: AlumnosAdapter
private var alumnosCache : List<UserDTO> = emptyList()
private var api = RetrofitClient.elorServInterface
class ConsultarAlumnos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultar_alumnos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ============= Configurar RecyclerView y Adapter ==============
        recyclerView = findViewById(R.id.rvAlumnos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        alumnosAdapter = AlumnosAdapter(emptyList()) { alumno ->
            navegarAPerfilAlumno(alumno)
        }
        recyclerView.adapter = alumnosAdapter

        // ============= Recibir el UserDTO desde el Intent =============
        val extras: Bundle? = intent.extras
        user = extras?.getSerializable("user") as UserDTO

        // ============= Obtener todos los alumnos del profesor ===============
        getAlumnosDelProfesor(user)
    }

    /**
     * Realiza una llamada a la API para obtener la lista de alumnos asociados al profesor dado.
     */
    private fun getAlumnosDelProfesor(profesor: UserDTO) {
        api.getAlumnosDelProfesor(profesor.id).enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(
                call: Call<List<UserDTO>>,
                response: Response<List<UserDTO>>
            ) {
                if (response.isSuccessful) { // codigo de respuesta [200..300]
                    alumnosCache = response.body() ?: emptyList()
                    alumnosAdapter.updateAlumnos(alumnosCache)
                } else { //el codigo de respuesta no es [200..300]
                    Toast.makeText(this@ConsultarAlumnos, getString(R.string.credenciales_incorrectas), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Toast.makeText(this@ConsultarAlumnos, getString(R.string.error_conexion_api), Toast.LENGTH_LONG).show()
                println("Error completo: ${t.printStackTrace()}")
                t.printStackTrace()
            }
        })
    }
    private fun navegarAPerfilAlumno(alumno: UserDTO) {
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("userLogin", user)
        intent.putExtra("userPerfil", alumno)
        startActivity(intent)
    }
}