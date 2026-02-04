package com.example.elormov.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.retrofit.client.RetrofitClient
import com.example.elormov.retrofit.entities.HorarioDTO
import com.example.elormov.retrofit.entities.ReunionDTO
import com.example.elormov.retrofit.entities.UserDTO
import com.example.elormov.ui.PerfilActivity
import com.example.elormov.ui.home.horario.ClassSlot
import com.example.elormov.ui.home.horario.HorarioAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val api = RetrofitClient.elorServInterface
private lateinit var horarioAdapter: HorarioAdapter
private var horariosCache: List<HorarioDTO> = emptyList()
private var reunionesCache: List<ReunionDTO> = emptyList()
private var currentWeek: Int = 1
class AlumnoHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // =============== Obtener usuario ==================
        val extras: Bundle? = intent.extras
        val user: UserDTO = extras?.getSerializable("user") as UserDTO
        Toast.makeText(this, "Bienvenido Alumno ${user.nombre}", Toast.LENGTH_SHORT).show()

        // ================= Inicializaciones  ==================
        setupHorario()
        setupProfileButton(user)
        setupMenuButton()
        setupWeekSelector(user)

        // =============== Obtener horarios del profesor ==================
        getHorariosAlumno(user)

        // =============== Obtener reuniones del profesor ==================
        getReunionesAlumno(user)
    }


    /**
     * Configuración del selector de semana para cambiar entre semanas.
     * @param user El objeto UserDTO que contiene la información del profesor.
     */
    private fun setupWeekSelector(user: UserDTO) {
        val btnPrevious = findViewById<ImageButton>(R.id.btnPreviousWeek)
        val btnNext = findViewById<ImageButton>(R.id.btnNextWeek)
        val tvWeekNumber = findViewById<TextView>(R.id.tvWeekNumber)

        updateWeekIndicator()

        btnPrevious.setOnClickListener {
            if (currentWeek > 1) {
                currentWeek--
                tvWeekNumber.text = currentWeek.toString()
                updateWeekIndicator()
                getReunionesAlumno(user)
            }
        }

        btnNext.setOnClickListener {
            if (currentWeek < 3) {
                currentWeek++
                tvWeekNumber.text = currentWeek.toString()
                updateWeekIndicator()
                getReunionesAlumno(user)
            }
        }
    }

    /**
     * Actualiza el indicador visual de la semana seleccionada.
     * Cambia el fondo de los puntos que representan las semanas.
     */
    private fun updateWeekIndicator() {
        val dot1 = findViewById<View>(R.id.dotWeek1)
        val dot2 = findViewById<View>(R.id.dotWeek2)
        val dot3 = findViewById<View>(R.id.dotWeek3)

        dot1.setBackgroundResource(if (currentWeek == 1) R.drawable.week_dot_selected else R.drawable.week_dot_unselected)
        dot2.setBackgroundResource(if (currentWeek == 2) R.drawable.week_dot_selected else R.drawable.week_dot_unselected)
        dot3.setBackgroundResource(if (currentWeek == 3) R.drawable.week_dot_selected else R.drawable.week_dot_unselected)
    }


    /**
     * Obtiene los horarios del alumno desde la API y actualiza el horario.
     * @param user El objeto UserDTO que contiene la información del alumno.
     */
    private fun getHorariosAlumno(user: UserDTO) {
        api.getHorariosAlumno(user.id).enqueue(object : Callback<List<HorarioDTO>> {
            override fun onResponse(
                call: Call<List<HorarioDTO>>,
                response: Response<List<HorarioDTO>>
            ) {
                if (response.isSuccessful) {
                    horariosCache = response.body() ?: emptyList()
                    actualizarHorarioCompleto()
                } else {
                    Toast.makeText(
                        this@AlumnoHome,
                        "Error al obtener horarios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<HorarioDTO>>, t: Throwable) {
                Toast.makeText(
                    this@AlumnoHome,
                    "Fallo en la conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Obtiene las reuniones del alumno desde la API y actualiza el horario para añadir las reuniones.
     * @param user El objeto UserDTO que contiene la información del alumno.
     * TODO
     */
    private fun getReunionesAlumno(user: UserDTO) {
        api.getReunionesAlumno(user.id).enqueue(object : Callback<List<ReunionDTO>> {
            override fun onResponse(
                call: Call<List<ReunionDTO>>,
                response: Response<List<ReunionDTO>>
            ) {
                if (response.isSuccessful) {
                    val allReuniones = response.body() ?: emptyList()
                    reunionesCache = allReuniones.filter { it.semana == currentWeek}
                    actualizarHorarioCompleto()
                } else {
                    Toast.makeText(
                        this@AlumnoHome,
                        "Error al obtener reuniones",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ReunionDTO>>, t: Throwable) {
                Toast.makeText(
                    this@AlumnoHome,
                    "Fallo en la conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Actualiza el horario completo combinando horarios y reuniones.
     * Crea una lista de ClassSlot y actualiza el adaptador del RecyclerView.
     */
    private fun actualizarHorarioCompleto() {
        val horariosMap = horariosCache.groupBy { it.hora }
        val reunionesMap = reunionesCache.groupBy { it.hora }

        val classSlots = (1..6).map { hora ->
            val horasDelDia = horariosMap[hora] ?: emptyList()
            val reunionesDelDia = reunionesMap[hora] ?: emptyList()

            ClassSlot(
                hour = "Hora $hora",
                monday = horasDelDia.find { it.dia == "LUNES" }?.modulo?.nombre ?: "",
                tuesday = horasDelDia.find { it.dia == "MARTES" }?.modulo?.nombre ?: "",
                wednesday = horasDelDia.find { it.dia == "MIERCOLES" }?.modulo?.nombre ?: "",
                thursday = horasDelDia.find { it.dia == "JUEVES" }?.modulo?.nombre ?: "",
                friday = horasDelDia.find { it.dia == "VIERNES" }?.modulo?.nombre ?: "",
                mondayReunion = formatReunion(reunionesDelDia.find { it.dia == 1 }?.titulo),
                tuesdayReunion = formatReunion(reunionesDelDia.find { it.dia == 2 }?.titulo),
                wednesdayReunion = formatReunion(reunionesDelDia.find { it.dia == 3 }?.titulo),
                thursdayReunion = formatReunion(reunionesDelDia.find { it.dia == 4 }?.titulo),
                fridayReunion = formatReunion(reunionesDelDia.find { it.dia == 5 }?.titulo),
                mondayReunionEstado = reunionesDelDia.find { it.dia == 1 }?.estado,
                tuesdayReunionEstado = reunionesDelDia.find { it.dia == 2 }?.estado,
                wednesdayReunionEstado = reunionesDelDia.find { it.dia == 3 }?.estado,
                thursdayReunionEstado = reunionesDelDia.find { it.dia == 4 }?.estado,
                fridayReunionEstado = reunionesDelDia.find { it.dia == 5 }?.estado
            )
        }

        horarioAdapter = HorarioAdapter(classSlots)
        findViewById<RecyclerView>(R.id.rvSchedule).adapter = horarioAdapter
    }

    /**
     * Formatea el título de la reunión para mostrarlo en el horario.
     * @param titulo El título de la reunión.
     * @return Una cadena formateada para mostrar en el horario.
     */
    private fun formatReunion(titulo : String?): String {
        return if (titulo.isNullOrEmpty()) "" else "Reunión: $titulo"
    }

    /**
     * Configuración del RecyclerView para mostrar el horario.
     * Inicializa el adaptador con una lista vacía.
     */
    private fun setupHorario() {
        val rvHorario = findViewById<RecyclerView>(R.id.rvSchedule)
        rvHorario.layoutManager = LinearLayoutManager(this)
        // Inicialmente vacío, se llenará cuando lleguen los datos
        horarioAdapter = HorarioAdapter(emptyList())
        rvHorario.adapter = horarioAdapter
    }

    /**
     * Configuración del botón de perfil para cambiar a la actividad "Perfil" del usuario.
     * @param user El objeto UserDTO que contiene la información del usuario.
     */
    private fun setupProfileButton(user: UserDTO) {
        findViewById<FloatingActionButton>(R.id.fabProfile).setOnClickListener {
            Toast.makeText(this, "Ir al perfil de ${user.nombre}", Toast.LENGTH_SHORT).show()
            val intent  = Intent(this, PerfilActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)

        }
    }

    /**
     * Configuración del botón de menú para mostrar un PopupMenu con opciones.
     * Cuando se selecciona una opción, se cambiará a la actividad correspondiente.(TODO)
     */
    private fun setupMenuButton() {
        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener { view ->
            val popup = PopupMenu(this, view, Gravity.START)
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_perfil -> {
                        Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.menu_configuracion -> {
                        Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.menu_cerrar_sesion -> {
                        Toast.makeText(this, "Cerrar sesión", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.opcionExtra -> {
                        Toast.makeText(this, "Opción Extra", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }
}
