package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.adapters.*
//import istg.edu.ec.appEmprendeISTGDev.ui.fragments.dialogs.ProductoResumenDialogFragment
import istg.edu.ec.appEmprendeISTGDev.viewModel.RevisarPublicacionesViewModel

// Fragmento para revisar publicaciones en la aplicación
class RevisarPublicacionesFragment : Fragment() {

    // Declaración de vistas para mostrar la información de la publicación
    private lateinit var tvCategoria: TextView
    private lateinit var tvNombreNegocio: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var rvHorarioAtencion: RecyclerView
    private lateinit var rvLinksProductosServicios: RecyclerView
    private lateinit var rvLinksExternos: RecyclerView
    private lateinit var btnVisitarPerfil: Button
    private lateinit var lyProductos: LinearLayout // nuevo layout para ver productos

    // ViewModel compartido para manejar los datos de la publicación
    private val viewModel: RevisarPublicacionesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_revisar_publicaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        tvCategoria = view.findViewById(R.id.tvCategoria)
        tvNombreNegocio = view.findViewById(R.id.tvNombreNegocio)
        tvDescripcion = view.findViewById(R.id.tvDescripcion)
        tvUbicacion = view.findViewById(R.id.tvUbicacion)
        rvHorarioAtencion = view.findViewById(R.id.rvHorarioAtencion)
        rvLinksProductosServicios = view.findViewById(R.id.rvLinksProductosServicios)
        rvLinksExternos = view.findViewById(R.id.rvLinksExternos)
        btnVisitarPerfil = view.findViewById(R.id.btnVisitarPerfil)
        lyProductos = view.findViewById(R.id.lyProductos)

        // Obtener los argumentos enviados al fragmento (userId y publicacionId)
        val userId = arguments?.getString("userId") ?: ""
        val publicacionId = arguments?.getString("publicacionId") ?: ""

        // Cargar publicación desde ViewModel
        viewModel.cargarPublicacion(userId, publicacionId)

        // Observar datos del ViewModel
        viewModel.publicacion.observe(viewLifecycleOwner) { publicacion ->
            publicacion?.let {
                // Asignar los valores obtenidos a las vistas de texto
                tvCategoria.text = it.categoria?.nombreItem
                tvNombreNegocio.text = it.nombreLocal
                tvDescripcion.text = it.descripcion
                tvUbicacion.text = it.direccion

                rvHorarioAtencion.layoutManager = LinearLayoutManager(requireContext())
                rvHorarioAtencion.adapter = HorarioAtencionAdapter(it.horarioAtencion)

                rvLinksProductosServicios.layoutManager = LinearLayoutManager(requireContext())
                rvLinksProductosServicios.adapter = LinksProductosServiciosAdapter(it.enlacesProductos)

                rvLinksExternos.layoutManager = LinearLayoutManager(requireContext())
                rvLinksExternos.adapter = LinksExternosAdapter(it.enlacesExternos)

                // 👉 Acción del botón "Ver productos" - numero de telefono
                lyProductos.setOnClickListener { _ ->
                    val dialog = ProductoResumenDialogFragment(
                        productos = it.descripcionProductosServicios ?: emptyList(),
                        numeroWhatsApp = it.telefonoWhatsApp ?: "" // Aquí pasas el número de la publicación
                    )
                    val activity = requireActivity() as? AppCompatActivity
                    activity?.let { act ->
                        dialog.show(act.supportFragmentManager, "ProductosDialog")
                    }
                }

            } ?: run {
                // Mostrar mensaje si la publicación no se encuentra
                Toast.makeText(requireContext(), "Publicación no encontrada", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón visitar perfil
        btnVisitarPerfil.setOnClickListener {
            val args = Bundle().apply { putString("userId", userId) }
            findNavController().navigate(R.id.PerfilPublicacion, args)
        }
    }
}
