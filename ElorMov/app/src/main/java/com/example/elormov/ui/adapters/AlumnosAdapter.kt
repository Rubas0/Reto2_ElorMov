package com.example.elormov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.retrofit.entities.UserDTO

class AlumnosAdapter(
    private var alumnos: List<UserDTO>,
    private val onAlumnoClick: (UserDTO) -> Unit
) : RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val tvCurso: TextView = view.findViewById(R.id.tvCursoAlumno)
        val tvCiclo: TextView = view.findViewById(R.id.tvCicloAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.tvNombre.text = "${alumno.nombre} ${alumno.apellidos}"
        holder.itemView.setOnClickListener {
            onAlumnoClick(alumno)
        }
    }

    override fun getItemCount(): Int = alumnos.size

    fun updateAlumnos(nuevosAlumnos: List<UserDTO>) {
        alumnos = nuevosAlumnos
        notifyDataSetChanged()
    }
}