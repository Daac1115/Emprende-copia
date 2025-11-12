package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R

// Adaptador para mostrar enlaces de Drive (solo Drive), pero ocultando la URL y mostrando "Ver catálogo"
class LinksProductosServiciosAdapter(
    private val enlaces: List<String>
) : RecyclerView.Adapter<LinksProductosServiciosAdapter.LinkViewHolder>() {

    inner class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val enlaceTextView: TextView = itemView.findViewById(R.id.tvLinks)

        fun bind(enlace: String) {
            // 1) Siempre mostramos este texto fijo
            enlaceTextView.text = "Ver catálogo"

            // 2) Quitamos cualquier autoLink residual (por si acaso)
            enlaceTextView.autoLinkMask = 0
            enlaceTextView.movementMethod = null

            // 3) Al hacer clic, abrimos la URL real en el navegador
            enlaceTextView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(enlace))
                it.context.startActivity(intent)
            }
        }
    }

    // Infla el diseño del ítem y crea un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.enlaces_revisar, parent, false)
        return LinkViewHolder(view)
    }

    // Vincula los datos del enlace en la posición actual con el ViewHolder
    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(enlaces[position])
    }

    // Retorna el número total de elementos en la lista
    override fun getItemCount(): Int = enlaces.size
}
