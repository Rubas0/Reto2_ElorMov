package com.example.reto2_elormov.ui.profesores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.HorarioDto

/**
 * Está simplificado para grid
 */
class HorarioAdapter : RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {

    private var horario: List<HorarioDto> = emptyList()

    fun submitList(newList: List<HorarioDto>) {
        horario = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horario, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(horario[position])
    }

    override fun getItemCount(): Int = horario.size

    inner class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtInfo: TextView = itemView.findViewById(R.id.txtInfo)

        fun bind(horario: HorarioDto) {
            txtInfo.text = when (horario.tipo) {
                "CLASE" -> "${horario.asignatura}\n${horario.aula}"
                "TUTORIA" -> "Tutoría"
                "GUARDIA" -> "Guardia"
                "REUNION" -> "Reunión"
                else -> "Libre"
            }
        }
    }
}