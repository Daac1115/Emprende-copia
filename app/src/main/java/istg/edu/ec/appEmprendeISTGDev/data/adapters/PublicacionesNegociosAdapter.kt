package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

class PublicacionesNegociosAdapter(
    private var listapublicaciones: List<RevisarSolicitudModel>,
    private val onItemClickListener: (RevisarSolicitudModel) -> Unit // Listener pasado como argumento
) : RecyclerView.Adapter<PublicacionesNegociosAdapter.publicacionesViewHolder>() {

    // ViewHolder para cada elemento del RecyclerView
    class publicacionesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textViewNombreNegocio)
        val propietario: TextView = itemView.findViewById(R.id.textViewPropietario)
        val ubicacionLocal: TextView = itemView.findViewById(R.id.textViewUbicacionLocal)
    }

    // Crear un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): publicacionesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.publicacion_negocio, parent, false)
        return publicacionesViewHolder(view)
    }

    // Vincular datos al ViewHolder
    override fun onBindViewHolder(holder: publicacionesViewHolder, position: Int) {
        val publicacion = listapublicaciones[position]
        holder.nombre.text = publicacion.nombreLocal
        holder.propietario.text = publicacion.nombreUsuario
        holder.ubicacionLocal.text = publicacion.direccion
        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(publicacion) // Invocar el listener al hacer clic
        }
    }

    // Función para actualizar los datos del adaptador
    fun updateData(newData: List<RevisarSolicitudModel>) {
        listapublicaciones = newData
        notifyDataSetChanged()
    }

    // Obtener el número de elementos
    override fun getItemCount(): Int = listapublicaciones.size
}