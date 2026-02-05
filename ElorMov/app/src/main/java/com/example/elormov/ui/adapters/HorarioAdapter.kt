package com.example.elormov.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.ui.utils.ClassSlot

/**
 * Adaptador para el RecyclerView que muestra el horario de clases.
 * Cada fila representa una hora y las asignaturas correspondientes a cada día de la semana.
 * @param classSlots Lista de objetos ClassSlot que contienen la información del horario.
 */
class HorarioAdapter(private val classSlots: List<ClassSlot>) :
    RecyclerView.Adapter<HorarioAdapter.ViewHolder>() {

    /**
     * ViewHolder que contiene las vistas para cada fila del horario.
     * @param view La vista de la fila del horario.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHour: TextView = view.findViewById(R.id.tvHour)
        val tvMonday: TextView = view.findViewById(R.id.tvMonday)
        val tvTuesday: TextView = view.findViewById(R.id.tvTuesday)
        val tvWednesday: TextView = view.findViewById(R.id.tvWednesday)
        val tvThursday: TextView = view.findViewById(R.id.tvThursday)
        val tvFriday: TextView = view.findViewById(R.id.tvFriday)
    }

    /**
     * Infla la vista para cada fila del horario y crea un ViewHolder.
     * @param parent El ViewGroup padre.
     * @param viewType El tipo de vista.
     * @return Un nuevo ViewHolder con la vista inflada (inflada significa crear la vista a partir del XML).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_row, parent, false)
        return ViewHolder(view)
    }

    /**
     * Vincula los datos del ClassSlot a las vistas del ViewHolder.
     * @param holder El ViewHolder que contiene las vistas.
     * @param position La posición del elemento en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = classSlots[position]
        holder.tvHour.text = slot.hour
        setupCell(holder.tvMonday, slot.monday, slot.mondayReunion, slot.mondayReunionEstado)
        setupCell(holder.tvTuesday, slot.tuesday, slot.tuesdayReunion, slot.tuesdayReunionEstado)
        setupCell(holder.tvWednesday, slot.wednesday, slot.wednesdayReunion, slot.wednesdayReunionEstado)
        setupCell(holder.tvThursday, slot.thursday, slot.thursdayReunion, slot.thursdayReunionEstado)
        setupCell(holder.tvFriday, slot.friday, slot.fridayReunion, slot.fridayReunionEstado)
    }

    /**
     * Configura el contenido y el color de fondo de una celda del horario.
     * @param textView La TextView que representa la celda.
     * @param asignatura El nombre de la asignatura.
     * @param reunion El asunto de la reunión.
     * @param estado El estado de la reunión (puede ser nulo).
     */
    private fun setupCell(textView: TextView, asignatura: String, reunion: String, estado: String?) {
        textView.text = formatCell(asignatura, reunion)
        textView.setBackgroundColor(getCellColor(textView, asignatura, reunion, estado))
    }

    /**
     * Determina el color de fondo de una celda según la presencia de asignaturas y reuniones.
     * @param textView La TextView que representa la celda.
     * @param asignatura El nombre de la asignatura.
     * @param reunion El asunto de la reunión.
     * @param estado El estado de la reunión (puede ser nulo).
     * @return El color de fondo correspondiente.
     */
    private fun getCellColor(textView: TextView, asignatura: String, reunion: String, estado: String?): Int {
        val context = textView.context

        return when {
            // Caso 1: Hay asignatura y reunión a la vez
            asignatura.isNotEmpty() && reunion.isNotEmpty() ->
                ContextCompat.getColor(context, R.color.reunion_conflicto)

            // Casos 2-4: Solo hay reunión
            asignatura.isEmpty() && reunion.isNotEmpty() -> {
                when (estado?.uppercase()) {
                    "PENDIENTE" -> ContextCompat.getColor(context, R.color.reunion_pendiente)
                    "RECHAZADA" -> ContextCompat.getColor(context, R.color.reunion_rechazada)
                    "ACEPTADA" -> ContextCompat.getColor(context, R.color.reunion_aceptada)
                    else -> ContextCompat.getColor(context, R.color.background_white)
                }
            }
            // Por defecto: fondo blanco
            else -> ContextCompat.getColor(context, R.color.background_white)
        }
    }

    /**
     * Formatea el contenido de una celda combinando la asignatura y la reunión.
     * Si ambos están presentes, los separa con una línea.
     * @param asignatura El nombre de la asignatura.
     * @param reunion El asunto de la reunión.
     * @return Una cadena formateada para mostrar en la celda.
     */
    private fun formatCell(asignatura: String, reunion: String): String {
        return when {
            asignatura.isNotEmpty() && reunion.isNotEmpty() -> "$asignatura\n---\n$reunion"
            asignatura.isNotEmpty() -> asignatura
            reunion.isNotEmpty() -> reunion
            else -> ""
        }
    }

    /**
     * Devuelve el número total de elementos en la lista.
     * @return El tamaño de la lista classSlots.
     */
    override fun getItemCount() = classSlots.size
}