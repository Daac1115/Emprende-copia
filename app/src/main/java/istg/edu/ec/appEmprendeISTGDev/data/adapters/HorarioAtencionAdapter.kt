package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.HorarioModel

// Adaptador para mostrar una lista de horarios de atención en un RecyclerView
class HorarioAtencionAdapter(
    private val horarios: List<HorarioModel> // Lista de horarios a mostrar
) : RecyclerView.Adapter<HorarioAtencionAdapter.HorarioViewHolder>() {

    // ViewHolder que representa cada horario en la lista
    class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencia al TextView donde se mostrará el horario
        private val tvHorario: TextView = itemView.findViewById(R.id.tvNombreProducto) // Posible error: ID incorrecto

        // Método para asignar los valores de un horario a la interfaz
        fun bind(horario: HorarioModel) {
            val horarioTexto = if (horario.diaInicio == horario.diaFin) {
                // Si el horario es de un solo día (ej. "Lunes: 08:00 - 18:00")
                "${horario.diaInicio}: ${horario.horaApertura} - ${horario.horaCierre}"
            } else {
                // Si el horario cubre varios días (ej. "Lunes-Viernes 08:00 - 18:00")
                "${horario.diaInicio}-${horario.diaFin} ${horario.horaApertura} - ${horario.horaCierre}"
            }
            tvHorario.text = horarioTexto // Asigna el texto formateado al TextView
        }
    }

    // Infla el diseño del ítem y crea un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.horario_atencion_revisar, parent, false)
        return HorarioViewHolder(view)
    }

    // Vincula los datos del horario en la posición actual con el ViewHolder
    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(horarios[position])
    }

    // Retorna el número total de elementos en la lista
    override fun getItemCount(): Int = horarios.size
}
