package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.databinding.MisNegociosBinding
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

// Se utiliza para mostrar una lista de publicaciones dentro de una categoría específica
// Cada publicación incluye información básica como el nombre del negocio, la ubicación y su estado
// También permite al usuario interactuar con las publicaciones mediante botones de edición y eliminación
class MisNegociosAdapter(
    // Lista de publicaciones que se mostrarán en esta categoría
    private val publicaciones: List<RevisarSolicitudModel>,
    // Callback para manejar la eliminación de una publicación
    private val onDeleteClickListener: (String, String) -> Unit,
    // Callback para manejar la edición de una publicación
    private var onEditClickListener: (RevisarSolicitudModel) -> Unit = {}
) : RecyclerView.Adapter<MisNegociosAdapter.ViewHolder>() {

    // Almacena referencias a los elementos de la vista para una publicacion
    // Utiliza View Binding para acceder fácilmente a los componentes del layout
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = MisNegociosBinding.bind(view)
    }

    // Infla el diseño de la vista para una publicación y devuelve su ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mis_negocios, parent, false)
        return ViewHolder(view)
    }

    // Configura la vista para una publicación específica, incluyendo su información y botones de acción
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val publicacion = publicaciones[position]

        holder.binding.textViewNombreNegocio.text = publicacion.nombreLocal
        holder.binding.textViewUbicacionNegocio.text = publicacion.direccion
        // Si el estado de la publicación es "Aprobado" se mostrará el imageViewBorrar, si es "Rechazado" no se mostrará
        when (publicacion.estado) {
            "Aprobado" -> {
                holder.binding.textViewEstado.setTextColor(Color.BLUE)
                holder.binding.imageViewBorrar.visibility = View.VISIBLE
                holder.binding.imageViewEditar.visibility = View.VISIBLE
            }
            // Aqui podemos poner si se ve imagen si o no
            "Rechazado" -> {
                holder.binding.textViewEstado.setTextColor(Color.RED)
                holder.binding.imageViewBorrar.visibility = View.GONE
                holder.binding.imageViewEditar.visibility = View.VISIBLE
            }
        }
        holder.binding.textViewEstado.text = publicacion.estado

        // Configurar el listener para el botón de eliminación
        holder.binding.imageViewBorrar.setOnClickListener {
            onDeleteClickListener(publicacion.id!!, publicacion.nombreLocal!!)
        }

        // Listener para el botón de edición
        holder.binding.imageViewEditar.setOnClickListener {
            onEditClickListener(publicacion)
        }
    }

    // Configura el callback para el botón de edición
    fun setOnEditClickListener(listener: (RevisarSolicitudModel) -> Unit) {
        this.onEditClickListener = listener
    }

    // Devuelve el número total de publicaciones disponibles
    override fun getItemCount(): Int = publicaciones.size
}
