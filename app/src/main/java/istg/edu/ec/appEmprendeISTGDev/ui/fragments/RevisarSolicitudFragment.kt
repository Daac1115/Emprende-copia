package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.adapters.HorarioAtencionAdapter
import istg.edu.ec.appEmprendeISTGDev.data.adapters.LinksExternosAdapter
import istg.edu.ec.appEmprendeISTGDev.data.adapters.LinksProductosServiciosAdapter
//import istg.edu.ec.appEmprendeISTGDev.ui.fragments.dialogs.ProductoResumenDialogFragment
import istg.edu.ec.appEmprendeISTGDev.ui.viewmodel.RevisarSolicitudViewModel

class RevisarSolicitudFragment : Fragment() {

    private lateinit var tvCategoria: TextView
    private lateinit var tvNombreNegocio: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var rvHorarioAtencion: RecyclerView
    private lateinit var rvLinksProductosServicios: RecyclerView
    private lateinit var rvLinksExternos: RecyclerView
    private lateinit var etComentarioEdit: EditText
    private lateinit var btnAceptar: Button
    private lateinit var btnRechazar: Button
    private lateinit var btnVisitarPerfil: Button

    // Nuevo layout para ver productos
    private lateinit var lyProductos: LinearLayout

    private val viewModel: RevisarSolicitudViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_revisar_solicitud, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización de vistas
        tvCategoria = view.findViewById(R.id.tvCategoria)
        tvNombreNegocio = view.findViewById(R.id.tvNombreNegocio)
        tvDescripcion = view.findViewById(R.id.tvDescripcion)
        tvUbicacion = view.findViewById(R.id.tvUbicacion)
        rvHorarioAtencion = view.findViewById(R.id.rvHorarioAtencion)
        rvLinksProductosServicios = view.findViewById(R.id.rvLinksProductosServicios)
        rvLinksExternos = view.findViewById(R.id.rvLinksExternos)
        etComentarioEdit = view.findViewById(R.id.etComentarioEdit)
        btnAceptar = view.findViewById(R.id.btnAceptar)
        btnRechazar = view.findViewById(R.id.btnRechazar)
        btnVisitarPerfil = view.findViewById(R.id.btvVisitarPerfil)
        lyProductos = view.findViewById(R.id.lyProductos)

        val userId = arguments?.getString("userId") ?: ""
        val solicitudId = arguments?.getString("id") ?: ""

        // Acción del botón visitar perfil
        btnVisitarPerfil.setOnClickListener {
            val bundle = Bundle().apply { putString("userId", userId) }
            findNavController().navigate(R.id.action_revisarSolicitudFragment_to_perfilPublicacionFragment, bundle)
        }

        // Cargar datos desde el ViewModel
        viewModel.cargarSolicitud(userId, solicitudId)

        viewModel.solicitud.observe(viewLifecycleOwner) { solicitud ->
            solicitud?.let {
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

                // 👉 Listener para abrir diálogo de productos - CORREGIDO
                lyProductos.setOnClickListener { _ ->
                    val dialog = ProductoResumenDialogFragment(
                        productos = it.descripcionProductosServicios ?: emptyList(),
                        numeroWhatsApp = it.telefonoWhatsApp ?: "" // Aquí pasas el número de la solicitud
                    )
                    val activity = requireActivity() as? AppCompatActivity
                    activity?.let { act ->
                        dialog.show(act.supportFragmentManager, "ProductosDialog")
                    }
                }

            } ?: run {
                Toast.makeText(requireContext(), "No se encontró la solicitud.", Toast.LENGTH_SHORT).show()
            }
        }

        // Acciones de aprobación/rechazo
        setupAcciones()
    }

    private fun setupAcciones() {
        btnAceptar.setOnClickListener {
            val comentario = etComentarioEdit.text.toString().trim()
            if (comentario.isEmpty()) {
                Toast.makeText(requireContext(), "El comentario no puede estar vacío.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Aprobación")
                .setMessage("¿Desea guardar esta publicación como aprobada?")
                .setPositiveButton("Aceptar") { _, _ ->
                    viewModel.solicitud.value?.let {
                        viewModel.aceptarSolicitud(it, comentario) {
                            Toast.makeText(requireContext(), "Solicitud aprobada.", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnRechazar.setOnClickListener {
            val comentario = etComentarioEdit.text.toString().trim()
            if (comentario.isEmpty()) {
                Toast.makeText(requireContext(), "El comentario no puede estar vacío.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Rechazo")
                .setMessage("¿Desea guardar esta publicación como rechazada?")
                .setPositiveButton("Aceptar") { _, _ ->
                    viewModel.solicitud.value?.let {
                        viewModel.rechazarSolicitud(it, comentario) {
                            Toast.makeText(requireContext(), "Solicitud rechazada.", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
