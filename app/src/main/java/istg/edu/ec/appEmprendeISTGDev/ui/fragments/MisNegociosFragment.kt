package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importa FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.adapters.CategoriaMisNegociosAdapter
import istg.edu.ec.appEmprendeISTGDev.data.adapters.MisNegociosAdapter
import istg.edu.ec.appEmprendeISTGDev.viewModel.MisNegociosViewModel

// Muestra las publicaciones del usuario agrupadas por categoría
// Permite al usuario agregar nuevos negocios, editar publicaciones existentes y eliminar publicaciones de forma lógica
class MisNegociosFragment : Fragment() {

    // RecyclerView que muestra las categorías y sus publicaciones asociadas
    private lateinit var recyclerViewMisNegocios: RecyclerView
    // ViewModel que gestiona los datos y la lógica de negocio del fragmento
    private lateinit var viewModel: MisNegociosViewModel
    private lateinit var fabGuia: FloatingActionButton // Declarar el FloatingActionButton

    // Infla el diseño del fragmento y configura los listeners para los botones
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_negocios, container, false)

        // Encuentra el botón "Agregar Negocio" en tu layout
        val buttonAgregarNegocio = view.findViewById<Button>(R.id.btn_agregar_negocio)
        // Configura el listener para el botón "Agregar Negocio"
        buttonAgregarNegocio.setOnClickListener {
            findNavController().navigate(R.id.agregarNegocioFragment)
        }

        // Encuentra el FloatingActionButton en tu layout
        fabGuia = view.findViewById(R.id.fab) // Asegúrate que el ID sea correcto (@id/fab)
        // Configura el listener para el FloatingActionButton
        fabGuia.setOnClickListener {
            findNavController().navigate(R.id.guiaFragment) // Navega al GuiaFragment
        }

        return view
    }

    // Configura el RecyclerView y observa los cambios en las categorías para actualizar la UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewMisNegocios = view.findViewById(R.id.recyclerViewMisNegocios)
        recyclerViewMisNegocios.layoutManager = LinearLayoutManager(requireContext())

        // Obtener el UID del usuario actual
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        viewModel = ViewModelProvider(this)[MisNegociosViewModel::class.java]
        viewModel.cargarDatosUsuario(uid)

        // Observar los cambios en las categorías
        viewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            if (categorias.isEmpty()) {
                Toast.makeText(requireContext(), "No hay publicaciones disponibles", Toast.LENGTH_SHORT).show()
            } else {
                val adapter = CategoriaMisNegociosAdapter(categorias, { publicacionId, nombreLocal ->
                    mostrarDialogoConfirmacion(publicacionId, nombreLocal)
                }) { publicacion ->
                    // Navegar a AgregarNegocioFragment con los datos de la publicación
                    val bundle = Bundle().apply {
                        putString("userId", publicacion.uid)
                        putString("publicacionId", publicacion.id)
                    }
                    findNavController().navigate(R.id.agregarNegocioFragment, bundle)
                }
                recyclerViewMisNegocios.adapter = adapter
            }
        }
    }

    // Muestra un diálogo de confirmación antes de eliminar una publicación
    private fun mostrarDialogoConfirmacion(publicacionId: String, nombreLocal: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Publicación")
            .setMessage("¿Desea eliminar la publicación de su negocio '$nombreLocal'? ¡Advertencia: Esta acción no se puede revertir!")
            .setPositiveButton("Sí") { _, _ ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                viewModel.eliminarPublicacion(uid, publicacionId) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Publicación eliminada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar la publicación", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}