package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel
import istg.edu.ec.appEmprendeISTGDev.viewModel.PerfilsViewModel

class PerfilFragment : Fragment() {
    private val viewModel: PerfilsViewModel by viewModels()
    private var uid: String? = null
    private lateinit var txtCorreo: EditText
    private lateinit var txt_Identificacion: EditText
    private lateinit var txt_nombre: EditText
    private lateinit var txt_telefono: EditText
    private lateinit var txt_TercerNivel: EditText
    private lateinit var txt_secundaria: EditText
    private lateinit var txt_facebook: EditText
    private lateinit var txt_tiktok: EditText
    private lateinit var txt_instagram: EditText
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        val sharedPreferences = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        uid = sharedPreferences.getString("uid", null) ?: mAuth?.currentUser?.uid
        uid?.let { viewModel.getPostsByUser(it) }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        txtCorreo = view.findViewById(R.id.txt_correo)
        txt_Identificacion = view.findViewById(R.id.txt_Identificacion)
        txt_nombre = view.findViewById(R.id.txt_nombre)
        txt_telefono = view.findViewById(R.id.txt_telefono)
        txt_TercerNivel = view.findViewById(R.id.txt_TercerNivel)
        txt_secundaria = view.findViewById(R.id.txt_secundaria)
        txt_facebook = view.findViewById(R.id.txt_facebook)
        txt_tiktok = view.findViewById(R.id.txt_tiktok)
        txt_instagram = view.findViewById(R.id.txt_instagram)

        // Observar el perfil del usuario
        viewModel.perfilModel.observe(viewLifecycleOwner) { perfilModel ->
            if (perfilModel != null) {
                setCampos(perfilModel)
           }else{
                txtCorreo.setText(mAuth?.currentUser?.email.toString())
                txt_nombre.setText(mAuth?.currentUser?.displayName.toString())
                val objperfil = PerfilModel()
                objperfil.uid = uid!!
                objperfil.nombre = mAuth?.currentUser?.displayName.toString()
                objperfil.email = mAuth?.currentUser?.email.toString()
                objperfil.foto = mAuth?.currentUser?.photoUrl.toString()
                viewModel.savePerfil(objperfil)
            }
        }

        // Configurar la imagen de perfil
        val circleImageView = view.findViewById<CircleImageView>(R.id.circleImageView)
        circleImageView.setOnClickListener {
            val dialog = ImagenSeleccionFragment()
            dialog.setOnImageSelectedListener(object : ImagenSeleccionFragment.OnImageSelectedListener {
                override fun onImageSelected(imageUri: Bitmap?) {
                    circleImageView.setImageBitmap(imageUri)
                }

                override fun onImageSelected2(imageUri2: Uri?) {
                    circleImageView.setImageURI(imageUri2)
                }
            })
            dialog.show(childFragmentManager, "imageSelectionDialog")
        }

        var fotoUrl = ""

        // Cargar datos del usuario autenticado
        val currentUser: FirebaseUser? = mAuth?.currentUser
        if (currentUser != null) {
            fotoUrl = currentUser.photoUrl.toString()
            Glide.with(this)
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.icono_persona)
                .circleCrop()
                .override(200, 200)
                .into(circleImageView)
        }

        // Guardar datos del perfil
        val myButton = view.findViewById<Button>(R.id.btnGuardarDatos)
        myButton.setOnClickListener {
            val identificacion = txt_Identificacion.text.toString()
            val nombre = txt_nombre.text.toString()
            val tercernivel = txt_TercerNivel.text.toString()
            val secundaria = txt_secundaria.text.toString()
            val correo = txtCorreo.text.toString()
            val telefono = txt_telefono.text.toString()
            val facebook = txt_facebook.text.toString()
            val instagram = txt_instagram.text.toString()
            val tiktok = txt_tiktok.text.toString()

            val perfil = PerfilModel(
                uid = uid.orEmpty(),
                nombre = nombre,
                email = correo,
                telefono = telefono,
                cedula = identificacion,
                estudiosActuales = tercernivel,
                secundaria = secundaria,
                linkFacebook = facebook,
                linkInstagram = instagram,
                tiktok = tiktok,
                edad = 0,
                foto = fotoUrl
            )
            viewModel.savePerfil(perfil)
            Toast.makeText(requireContext(), "Se ha guardado con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCampos(objPerfilModel: PerfilModel) {
        txtCorreo.setText(objPerfilModel.email.ifEmpty { mAuth?.currentUser?.email })
        txt_Identificacion.setText(objPerfilModel.cedula)
        txt_nombre.setText(objPerfilModel.nombre.ifEmpty { mAuth?.currentUser?.displayName })
        txt_telefono.setText(objPerfilModel.telefono.ifEmpty { mAuth?.currentUser?.phoneNumber })
        txt_TercerNivel.setText(objPerfilModel.estudiosActuales)
        txt_secundaria.setText(objPerfilModel.secundaria)
        txt_facebook.setText(objPerfilModel.linkFacebook)
        txt_tiktok.setText(objPerfilModel.tiktok)
        txt_instagram.setText(objPerfilModel.linkInstagram)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }
}