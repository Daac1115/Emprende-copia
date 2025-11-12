package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel
import istg.edu.ec.appEmprendeISTGDev.viewModel.PerfilsViewModel

class PerfilPublicacionFragment : Fragment() {
    private val viewModel: PerfilsViewModel by viewModels()
    private var uid: String? = null

    // Referencias a los TextView
    private lateinit var txtCorreo: TextView
    private lateinit var txtIdentificacion: TextView
    private lateinit var txtNombre: TextView
    private lateinit var txtTelefono: TextView
    private lateinit var txtTercerNivel: TextView
    private lateinit var txtSecundaria: TextView
    private lateinit var txtFacebook: TextView
    private lateinit var txtTiktok: TextView
    private lateinit var txtInstagram: TextView
    private lateinit var circleImageView: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener el UID del argumento recibido
        uid = arguments?.getString("userId")
        uid?.let { viewModel.getPostsByUser(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        txtCorreo = view.findViewById(R.id.txt_correo)
        txtIdentificacion = view.findViewById(R.id.txt_Identificacion)
        txtNombre = view.findViewById(R.id.txt_nombre)
        txtTelefono = view.findViewById(R.id.txt_telefono)
        txtTercerNivel = view.findViewById(R.id.txt_TercerNivel)
        txtSecundaria = view.findViewById(R.id.txt_secundaria)
        txtFacebook = view.findViewById(R.id.txt_facebook)
        txtTiktok = view.findViewById(R.id.txt_tiktok)
        txtInstagram = view.findViewById(R.id.txt_instagram)

        // Configurar la imagen de perfil
        circleImageView = view.findViewById<CircleImageView>(R.id.circleImageView)
        uid?.let { userId ->
            viewModel.getPostsByUser(userId)
        }

        // Observar el perfil del usuario
        viewModel.perfilModel.observe(viewLifecycleOwner) { perfilModel ->
            if (perfilModel != null) {
                setCampos(perfilModel)
            } else {
                // Si no hay perfil, mostrar valores predeterminados
                setCampos(null)
            }
        }
    }

    private fun setCampos(objPerfilModel: PerfilModel?) {
        txtCorreo.text = objPerfilModel?.email ?: "No especificado"

        // --- INICIO DE LA MODIFICACIÓN ---
        // Lógica para enmascarar la cédula
        txtIdentificacion.text = objPerfilModel?.cedula?.let { cedula ->
            if (cedula.length > 4) {
                // Reemplaza los últimos 4 caracteres con "xxxx"
                cedula.replaceRange(cedula.length - 4, cedula.length, "XXXX")
            } else {
                "XXXX" // Si la cédula es muy corta, muestra "xxxx"
            }
        } ?: "No especificado" // Si la cédula es nula, muestra esto
        // --- FIN DE LA MODIFICACIÓN ---

        txtNombre.text = objPerfilModel?.nombre ?: "No especificado"
        txtTelefono.text = objPerfilModel?.telefono ?: "No especificado"
        txtTercerNivel.text = objPerfilModel?.estudiosActuales ?: "No especificado"
        txtSecundaria.text = objPerfilModel?.secundaria ?: "No especificado"
        txtFacebook.text = objPerfilModel?.linkFacebook ?: "No especificado"
        txtTiktok.text = objPerfilModel?.tiktok ?: "No especificado"
        txtInstagram.text = objPerfilModel?.linkInstagram ?: "No especificado"


        if (objPerfilModel != null) {
            val fotoUrl = objPerfilModel?.foto.toString()
            Glide.with(this)
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.icono_persona)
                .circleCrop()
                .override(200, 200)
                .into(circleImageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil_publicacion, container, false)
    }
}