package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R

// Adaptador que muestra el comentario del administrador en un RecyclerView en AgregarNegocioFragment (publicación existente)
class ComentarioPublicacionAdapter(
    // Lista de comentarios que se mostrarán en el RecyclerView
    private val comentarios: List<String>
    ) : RecyclerView.Adapter<ComentarioPublicacionAdapter.ViewHolder>() {

    // ViewHolder que contiene las referencias a las vistas de un comentario
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewComentario: TextView = view.findViewById(R.id.etComentarioEdit)
    }

    // Infla el diseño de un comentario y devuelve su ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comentario_negocio, parent, false)
        return ViewHolder(view)
    }

    // Asigna los datos de un comentario al TextView correspondiente y deshabilita la edición
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.textViewComentario.text = comentario
        holder.textViewComentario.isEnabled = false // Deshabilitar edición
    }

    // Devuelve el número total de comentarios disponibles
    override fun getItemCount(): Int = comentarios.size
}