package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.adapters.SolicitudesNegociosAdapter
import istg.edu.ec.appEmprendeISTGDev.ui.viewmodel.SolicitudesNegocioViewModel

// Fragmento que maneja la vista de las solicitudes de negocios
class SolicitudesNegociosFragment : Fragment() {

    // Se obtiene el ViewModel compartido entre actividades para gestionar los datos
    private val viewModel: SolicitudesNegocioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Se infla el layout correspondiente al fragmento
        return inflater.inflate(R.layout.fragment_solicitudes_negocios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se obtiene la referencia del RecyclerView desde la vista
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Se establece un LinearLayoutManager para organizar los elementos en forma de lista
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Se observa la lista de solicitudes pendientes en el ViewModel
        viewModel.solicitudesPendientes.observe(viewLifecycleOwner) { solicitudes ->
            // Se crea un adaptador con la lista de solicitudes y un manejador de clics
            val adapter = SolicitudesNegociosAdapter(solicitudes) { negocio ->
                // Se crea un Bundle con los datos del negocio seleccionado
                val bundle = Bundle().apply {
                    putString("id", negocio.id) // ID del negocio
                    putString("userId", negocio.uid) // UID del usuario que envió la solicitud
                }
                // Se navega al fragmento de revisión de solicitud con los datos seleccionados
                findNavController().navigate(
                    R.id.revisarSolicitudFragment,
                    bundle
                )
            }
            // Se asigna el adaptador al RecyclerView
            recyclerView.adapter = adapter
        }
    }
}
