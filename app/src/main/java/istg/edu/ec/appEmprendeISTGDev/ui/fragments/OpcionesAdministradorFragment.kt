package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import istg.edu.ec.appEmprendeISTGDev.R

// Proporciona botones que permiten al administrador navegar a diferentes funcionalidades relacionadas con la gestión del sistema
class OpcionesAdministradorFragment : Fragment() {

    // Infla el diseño del fragmento y configura los listeners para los botones de navegación
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_opciones_administrador, container, false)

        // Encuentra el botón btn_filtrado_busqueda en tu layout
        val buttonFiltradoBusqueda = view.findViewById<Button>(R.id.btn_filtrado_busqueda)
        // Configura el listener para el buttonFiltradoBusqueda
        buttonFiltradoBusqueda.setOnClickListener {
            // Navega al GestionFiltradoBusquedaFragment donde se gestionan las categorías
            findNavController().navigate(R.id.gestionFiltradoBusquedaFragment)
        }

        // Encuentra el botón btn_solicitudes_negocios en tu layout
        val buttonSolicitudesNegocios = view.findViewById<Button>(R.id.btn_solicitudes_negocios)
        // Configura el listener para el buttonSolicitudesNegocios
        buttonSolicitudesNegocios.setOnClickListener {
            // Navega al SolicitudesNegociosFragment donde se revisan y gestionan las solicitudes de negocios/emprendimientos enviadas por los usuarios
            findNavController().navigate(R.id.solicitudesNegociosFragment)
        }

        return view
    }
}