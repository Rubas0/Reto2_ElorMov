package com.example.reto2_elormov.ui.alumnos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.AlumnoDTO

class AlumnosAdapter(
    private val onAlumnoClick: (AlumnoDTO) -> Unit
) : RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder>() {

    private var alumnos: List<AlumnoDTO> = emptyList()

    fun submitList(newList: List<AlumnoDTO>) {
        alumnos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.bind(alumnos[position])
    }

    override fun getItemCount(): Int = alumnos.size

    inner class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtCiclo: TextView = itemView.findViewById(R.id.txtCiclo)

        fun bind(alumno: AlumnoDTO) {
            txtNombre.text = "${alumno.nombre} ${alumno.apellidos}"
            txtCiclo.text = "${alumno.ciclo} - ${alumno.curso}"

            itemView.setOnClickListener {
                onAlumnoClick(alumno)
            }
        }
    }
}