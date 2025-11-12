package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.databinding.CategoriaMisNegociosBinding

// Este adaptador se utiliza para agrupar las publicaciones del usuario por categoría
// Cada categoría tiene su propio título y una lista de publicaciones asociadas que se muestran utilizando el adaptador MisNegociosAdapter
class CategoriaMisNegociosAdapter(
    // // Mapa que agrupa las publicaciones por categoría
    private val categorias: Map<String, List<RevisarSolicitudModel>>,
    // Callback para manejar la eliminación de una publicación
    private val onDeleteClickListener: (String, String) -> Unit,
    // Callback para manejar la edición de una publicación
    private val onEditClickListener: (RevisarSolicitudModel) -> Unit
) : RecyclerView.Adapter<CategoriaMisNegociosAdapter.ViewHolder>() {

    // Almacena referencias a los elementos de la vista para una categoría
    // Utiliza View Binding para acceder fácilmente a los componentes del layout
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CategoriaMisNegociosBinding.bind(view)
    }

    // Infla el diseño de la vista para una categoría y devuelve su ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categoria_mis_negocios, parent, false)
        return ViewHolder(view)
    }

    // Configura la vista para una categoría específica, incluyendo el título y las publicaciones asociadas
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias.keys.toList()[position]
        val publicaciones = categorias[categoria]!!

        holder.binding.textViewCategoria.text = categoria

        val adapter = MisNegociosAdapter(publicaciones, onDeleteClickListener).apply {
            setOnEditClickListener(onEditClickListener) // Configurar el callback de edición
        }
        holder.binding.recyclerViewMisNegocios.adapter = adapter
        holder.binding.recyclerViewMisNegocios.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    // Devuelve el número total de categorías disponibles
    override fun getItemCount(): Int = categorias.size
}