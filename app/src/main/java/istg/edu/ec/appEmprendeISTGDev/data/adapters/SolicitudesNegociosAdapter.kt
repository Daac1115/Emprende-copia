package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel

// Adaptador para mostrar una lista de solicitudes de negocios en un RecyclerView
class SolicitudesNegociosAdapter(
    private val listaNegocios: List<AgregarNegocioModel>, // Lista de negocios a mostrar
    private val onClick: (AgregarNegocioModel) -> Unit // Callback para manejar clics en un negocio
) : RecyclerView.Adapter<SolicitudesNegociosAdapter.NegocioViewHolder>() {

    // ViewHolder que representa cada negocio en la lista
    class NegocioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a los elementos de la UI dentro de cada ítem
        val nombre: TextView = itemView.findViewById(R.id.textViewNombreNegocio)
        val propietario: TextView = itemView.findViewById(R.id.textViewPropietario)
        val ubicacionLocal: TextView = itemView.findViewById(R.id.textViewUbicacionLocal)
    }

    // Infla el diseño del ítem y crea un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NegocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.solicitud_negocio, parent, false)
        return NegocioViewHolder(view)
    }

    // Vincula los datos del negocio en la posición actual con el ViewHolder
    override fun onBindViewHolder(holder: NegocioViewHolder, position: Int) {
        val negocio = listaNegocios[position] // Obtener el negocio en la posición actual

        // Asignar valores a los elementos de la UI
        holder.nombre.text = negocio.nombreLocal
        holder.propietario.text = negocio.nombreUsuario
        holder.ubicacionLocal.text = negocio.direccion

        // Configurar un clic en el ítem para ejecutar la función onClick
        holder.itemView.setOnClickListener { onClick(negocio) }
    }

    // Retorna el número total de elementos en la lista
    override fun getItemCount(): Int = listaNegocios.size
}
