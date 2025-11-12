package istg.edu.ec.appEmprendeISTGDev.data.adapters

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.core.text.HtmlCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R

// Son los externos
class LinksExternosAdapter(
    private val enlaces: List<String> // Lista de URLs externas
) : RecyclerView.Adapter<LinksExternosAdapter.LinkViewHolder>() {

    // ViewHolder que representa cada enlace en la lista
    inner class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a los elementos del XML (enlaces_revisar.xml)
        private val iconoImageView: ImageView = itemView.findViewById(R.id.ivLogoEnlace)
        private val textoTextView: TextView = itemView.findViewById(R.id.tvLinks)

        // Método para asignar el enlace, texto abreviado e ícono
        fun bind(enlace: String) {
            // 1) Extraer el host (dominio) del enlace
            val host = try {
                Uri.parse(enlace).host.orEmpty()
            } catch (e: Exception) {
                ""
            }

            // 2) Según el dominio, elegir texto abreviado e ícono correspondiente
            val (texto, iconoResId) = when {
                host.contains("facebook.com") || host.contains("fb.com") ->
                    "Ver Facebook" to R.drawable.facebook
                host.contains("instagram.com") || host.contains("instagr.am") ->
                    "Ver Instagram" to R.drawable.instagram
                host.contains("tiktok.com") || host.contains("vm.tiktok.com") ->
                    "Ver TikTok" to R.drawable.tiktok
                host.contains("youtube.com") || host.contains("youtu.be") ->
                    "Ver YouTube" to R.drawable.youtube
                host.contains("twitter.com") || host.contains("t.co") ->
                    "Ver Twitter" to R.drawable.x
                host.contains("telegram.org") || host.contains("t.me") ->
                    "Ver Telegram" to R.drawable.telegram
                host.contains("https://www.messenger.com/") || host.contains("m.me")->
                    "Ver Messenger" to R.drawable.messenger
                host.contains("wa.me") || host.contains("whatsapp.com") ->
                    "Ver WhatsApp" to R.drawable.whatsapp
                host.contains("threads.net") ->
                    "Ver Threads" to R.drawable.threads
                else ->
                    "Abrir enlace" to R.drawable.enlace // ícono genérico para cualquier otro dominio
            }

            // 3) Asignar el ícono y el texto al item
            iconoImageView.setImageResource(iconoResId)
            textoTextView.text = texto

            // 4) Configurar el clic para abrir el navegador con el enlace real
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(enlace))
                itemView.context.startActivity(intent)
            }
        }
    }

    // Infla el layout (enlaces_revisar.xml) y crea el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.enlaces_revisar, parent, false)
        return LinkViewHolder(view)
    }

    // Vincula el dato (URL) en la posición actual con el ViewHolder
    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(enlaces[position])
    }

    // Retorna el total de enlaces en la lista
    override fun getItemCount(): Int = enlaces.size
}

// arreglar los enlaces xd

// Son los externos
//class LinksExternosAdapter(
//    private val enlaces: List<String> // Lista de enlaces a mostrar
//) : RecyclerView.Adapter<LinksExternosAdapter.LinkViewHolder>() {
//
//    // ViewHolder que representa cada enlace en la lista
//    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        // Referencia al TextView donde se mostrará el enlace
//        private val enlaceTextView: TextView = itemView.findViewById(R.id.tvLinks)
//
//        // Método para asignar el enlace al TextView
//        fun bind(enlace: String) {
//            enlaceTextView.text = enlace
//        }
//    }
//
//    // Infla el diseño del ítem y crea un nuevo ViewHolder
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.enlaces_revisar, parent, false)
//        return LinkViewHolder(view)
//    }
//
//    // Vincula los datos del enlace en la posición actual con el ViewHolder
//    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
//        holder.bind(enlaces[position])
//    }
//
//    // Retorna el número total de elementos en la lista
//    override fun getItemCount(): Int = enlaces.size
//}
