package com.example.reto2_elormov.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_elormov.R
import com.example.reto2_elormov.data.dto.HorarioDto
import com.example.reto2_elormov.databinding.ItemHorarioBinding

/**
 * Adapter para mostrar los elementos del horario en un RecyclerView. Cada elemento representa una hora en el horario semanal.
 */
class HorarioAdapter(
    private val onItemClick: (HorarioDto) -> Unit
) : ListAdapter<HorarioDto, HorarioAdapter.HorarioViewHolder>(HorarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val binding = ItemHorarioBinding.inflate( // usamos ViewBinding para inflar el layout, es decir, para no usar findViewById
            LayoutInflater.from(parent.context), parent, false
        )
        return HorarioViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HorarioViewHolder(
        private val binding: ItemHorarioBinding,
        private val onItemClick: (HorarioDto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos del HorarioDto al ViewHolder, actualizando la UI según el tipo de clase y el estado de la reunión.
         */
        fun bind(item: HorarioDto) {
            // Reset estado visual
            binding.textViewReunion.visibility = View.GONE
            binding.cardView.setCardBackgroundColor(Color.WHITE)

            // Tipo base
            when (item.tipo) {
                "CLASE" -> {
                    binding.textViewAsignatura.text = item.asignatura.orEmpty()
                    val detalle = listOfNotNull(item.curso, item.ciclo, item.aula)
                        .filter { it.isNotBlank() }
                        .joinToString(" · ")
                    binding.textViewDetalle.text = detalle
                    binding.cardView.setCardBackgroundColor(
                        safeColor(binding.root, R.color.clase_bg, "#E3F2FD")
                    )
                }
                "TUTORIA" -> {
                    binding.textViewAsignatura.text = "Tutoría"
                    binding.textViewDetalle.text = item.aula.orEmpty()
                    binding.cardView.setCardBackgroundColor(
                        safeColor(binding.root, R.color.tutoria_bg, "#FFF9C4")
                    )
                }
                "GUARDIA" -> {
                    binding.textViewAsignatura.text = "Guardia"
                    binding.textViewDetalle.text = item.aula.orEmpty()
                    binding.cardView.setCardBackgroundColor(
                        safeColor(binding.root, R.color.guardia_bg, "#F3E5F5")
                    )
                }
                else -> { // VACIO u otros
                    binding.textViewAsignatura.text = ""
                    binding.textViewDetalle.text = ""
                    binding.cardView.setCardBackgroundColor(Color.WHITE)
                }
            }

            // Reunión (si la hay) – prioriza color de reunión
            item.reunion?.let { r ->
                binding.textViewReunion.visibility = View.VISIBLE
                when (r.estado) {
                    "PENDIENTE" -> {
                        binding.cardView.setCardBackgroundColor(
                            safeColor(binding.root, R.color.reunion_pendiente, "#FFF59D")
                        )
                        binding.textViewReunion.text = "📅 ${r.titulo}"
                    }
                    "CONFLICTO" -> {
                        binding.cardView.setCardBackgroundColor(
                            safeColor(binding.root, R.color.reunion_conflicto, "#BDBDBD")
                        )
                        binding.textViewReunion.text = "⚠️ ${r.titulo}"
                    }
                    "ACEPTADA" -> {
                        binding.cardView.setCardBackgroundColor(
                            safeColor(binding.root, R.color.reunion_aceptada, "#A5D6A7")
                        )
                        binding.textViewReunion.text = "✅ ${r.titulo}"
                    }
                    "CANCELADA" -> {
                        binding.cardView.setCardBackgroundColor(
                            safeColor(binding.root, R.color.reunion_cancelada, "#EF9A9A")
                        )
                        binding.textViewReunion.text = "❌ ${r.titulo}"
                    }
                    else -> {
                        // Estado desconocido: no cambiar color

                    }
                }
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }

        private fun safeColor(view: View, colorRes: Int, fallbackHex: String): Int { // Obtener color con fallback
            return try {
                ContextCompat.getColor(view.context, colorRes)
            } catch (_: Exception) {
                Color.parseColor(fallbackHex)
            }
        }
    }

/**
    * Callback para calcular las diferencias entre dos listas de HorarioDto, optimizando las actualizaciones del RecyclerView.
 */
    class HorarioDiffCallback : DiffUtil.ItemCallback<HorarioDto>() {
        override fun areItemsTheSame(oldItem: HorarioDto, newItem: HorarioDto): Boolean {
            return oldItem.dia == newItem.dia && oldItem.hora == newItem.hora
        }

        override fun areContentsTheSame(oldItem: HorarioDto, newItem: HorarioDto): Boolean { // Compara todo el contenido
            return oldItem == newItem
        }
    }
}