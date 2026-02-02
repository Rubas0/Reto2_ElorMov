package com.example.reto2_elormov.ui.alumnos

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.repository.AlumnoRepository
import com.example.reto2_elormov.network.RetrofitClient
import kotlinx.coroutines.launch

class AlumnoDetail : AppCompatActivity() {

    private lateinit var imgAlumno: ImageView
    private lateinit var txtNombre: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtCiclo: TextView
    private lateinit var txtDual: TextView
    private lateinit var progressBar: ProgressBar

    private val repository: AlumnoRepository by lazy {
        AlumnoRepository(RetrofitClient.elorServApi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno_detail)

        imgAlumno = findViewById(R.id.imgAlumno)
        txtNombre = findViewById(R.id.txtNombre)
        txtEmail = findViewById(R.id.txtEmail)
        txtCiclo = findViewById(R.id.txtCiclo)
        txtDual = findViewById(R.id.txtDual)
        progressBar = findViewById(R.id.progressBar)

        val alumnoId = intent.getIntExtra("ALUMNO_ID", -1)

        if (alumnoId == -1) {
            Toast.makeText(this, "Error: ID alumno no válido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadAlumno(alumnoId)
    }

    private fun loadAlumno(alumnoId: Int) {
        lifecycleScope.launch {
            try {
                val response = repository.getAlumnoById(alumnoId)

                if (response.isSuccessful) {
                    val alumno = response.body()
                    alumno?.let {
                        txtNombre.text = "${it.nombre} ${it.apellidos}"
                        txtEmail.text = it.email ?: "Sin email"
                        txtCiclo.text = "${it.ciclo} - ${it.curso}"
                        txtDual.text = "Dual: ${if (it.dualIntensiva == true) "Sí" else "No"}"

                        val photoUrl = "${RetrofitClient.BASE_URL}api/users/${alumnoId}/photo" // url de la api segñun id para la foto
                        Glide.with(this@AlumnoDetail)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(imgAlumno)
                    }
                } else {
                    Toast.makeText(this@AlumnoDetail, "Error al cargar alumno", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@AlumnoDetail, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}