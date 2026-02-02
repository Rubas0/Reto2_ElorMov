package com.example.reto2_elormov.ui.profesores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.ProfesorDTO

class ProfesoresAdapter(
    private val onProfesorClick: (ProfesorDTO) -> Unit
) : RecyclerView.Adapter<ProfesoresAdapter.ProfesorViewHolder>() {

    private var profesores: List<ProfesorDTO> = emptyList()

    fun submitList(newList: List<ProfesorDTO>) {
        profesores = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        holder.bind(profesores[position])
    }

    override fun getItemCount(): Int = profesores.size

    inner class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtDepartamento: TextView = itemView.findViewById(R.id.txtDepartamento)

        fun bind(profesor: ProfesorDTO) {
            txtNombre.text = "${profesor.nombre} ${profesor.apellidos}"
            txtDepartamento.text = profesor.departamento ?: "Sin departamento"

            itemView.setOnClickListener {
                onProfesorClick(profesor)
            }
        }
    }
}