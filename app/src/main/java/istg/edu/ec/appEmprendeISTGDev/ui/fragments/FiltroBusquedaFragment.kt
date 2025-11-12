package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.adapters.PublicacionesNegociosAdapter
import istg.edu.ec.appEmprendeISTGDev.viewModel.FiltroBusquedaViewModel
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import java.io.Console

class FiltroBusquedaFragment : Fragment() {

    private val filtroBusquedaviewModel: FiltroBusquedaViewModel by viewModels()
    private lateinit var adapter: PublicacionesNegociosAdapter
    private lateinit var categoriaSpinner: Spinner
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    private var parametro: String = ""
    private var isInvitado: Boolean = false

    // ¡Nuevas variables para almacenar el estado actual!
    private var currentSelectedCategory: String = "Todas" // Guarda la categoría seleccionada
    private var currentSearchQuery: String = "" // Guarda la consulta de búsqueda actual

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filtro_busqueda, container, false)
        arguments?.let {
            val tipoContenido = it.getString("tipo_contenido", "no especificado")
            println(tipoContenido);
            it.putString("tipo_contenido", null)

        }
        // Inicializar vistas
        categoriaSpinner = view.findViewById(R.id.categorySpinner)
        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.recyclerViewPublicaciones)

        // Cambiar color y tamaño del texto en el SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.black, null))
        searchEditText.setHintTextColor(resources.getColor(R.color.gray, null))
        searchEditText.textSize = 18f

        // **Paso 1: Restaurar el estado guardado al crear la vista**
        // Si hay un estado guardado (por ejemplo, después de un cambio de orientación o
        // una recreación del Fragment por el sistema), lo restauramos aquí.
        savedInstanceState?.let {
            currentSelectedCategory = it.getString("CURRENT_SELECTED_CATEGORY", "Todas")
            currentSearchQuery = it.getString("CURRENT_SEARCH_QUERY", "")
        }

        // Configurar RecyclerView
        adapter = PublicacionesNegociosAdapter(emptyList()) { publicacion ->
            // **Paso 2: Guardar el estado actual antes de navegar**
            // Es crucial guardar la categoría y la consulta de búsqueda justo antes
            // de salir de este Fragment. Esto asegura que tengamos los valores más recientes.
            currentSelectedCategory = categoriaSpinner.selectedItem?.toString() ?: "Todas"
            currentSearchQuery = searchView.query.toString()

            if (isInvitado) {
                // Mostrar el diálogo si es invitado
                val dialogoSesion = SesionDialogFragment()
                dialogoSesion.show(requireActivity().supportFragmentManager, "SesionDialog")
            } else {
                // Navegar a la pantalla de detalles si no es invitado
                val bundle = bundleOf(
                    "publicacionId" to publicacion.id,
                    "userId" to publicacion.uid
                )
                findNavController().navigate(R.id.revisarPublicacionesFragment, bundle)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observar categorías
        filtroBusquedaviewModel.categoriaDatos.observe(viewLifecycleOwner) { gestionModel ->
            gestionModel?.let {
                val itemNames = it.items.values.map { item -> item.nombreItem }.sorted()
                val adapterSpinner = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, itemNames)
                adapterSpinner.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
                categoriaSpinner.adapter = adapterSpinner

                // **Paso 3: Restaurar la categoría seleccionada en el Spinner**
                // Después de que las categorías se cargan y el adaptador del Spinner se configura,
                // buscamos la posición de la categoría que teníamos guardada y la seleccionamos.
                val position = itemNames.indexOf(currentSelectedCategory)
                if (position != -1) {
                    categoriaSpinner.setSelection(position)
                } else {
                    categoriaSpinner.setSelection(0) // Por defecto, la primera si no se encuentra
                }
            }
        }

        // Cargar categorías y publicaciones
        filtroBusquedaviewModel.cargarDatosCategoria()
        filtroBusquedaviewModel.cargarPublicacionesAprobadas()
        // Esto podría necesitar ser llamado después de que las categorías estén cargadas y seleccionadas

        // Observar resultados filtrados
        filtroBusquedaviewModel.filteredResults.observe(viewLifecycleOwner) { results ->
            adapter.updateData(results)
        }

        // Configurar SearchView
        searchView.setOnClickListener {
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Actualiza la consulta de búsqueda actual
                currentSearchQuery = newText.orEmpty()
                ejecutarFiltro(currentSearchQuery)
                return true
            }
        })

        // **Paso 4: Restaurar la consulta de búsqueda en el SearchView**
        // Establece la consulta guardada en el SearchView. 'false' significa que no la envíe inmediatamente.
        searchView.setQuery(currentSearchQuery, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            parametro = it.getString("miParametro").toString()
            isInvitado = parametro == "invitado"
            Log.d("FiltroBusquedaFragment", "Modo Invitado: $isInvitado")
        }

        // Configurar Listener del Spinner
        // Este Listener debe ir en onViewCreated para asegurar que las vistas ya están listas.
        categoriaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Actualiza la categoría seleccionada y vuelve a filtrar con la consulta existente
                currentSelectedCategory = categoriaSpinner.selectedItem.toString()
                searchView.queryHint = "Buscar en $currentSelectedCategory"
                ejecutarFiltro(currentSearchQuery) // Usa la consulta de búsqueda almacenada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                searchView.queryHint = "Buscar negocio"
            }
        }

        // **Paso 5: Aplicar el filtro inicial al crear la vista**
        // Llama a ejecutarFiltro con la consulta de búsqueda actual para asegurar
        // que las publicaciones se filtren correctamente al inicio.
        ejecutarFiltro(currentSearchQuery)
    }

    // **Paso 6: Guardar el estado del Fragment cuando se destruye temporalmente**
    // Este método se llama cuando el sistema necesita destruir el Fragment
    // (por ejemplo, para liberar memoria o por un cambio de configuración como rotación).
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("CURRENT_SELECTED_CATEGORY", currentSelectedCategory)
        outState.putString("CURRENT_SEARCH_QUERY", currentSearchQuery)
    }

    // `onViewStateRestored` y `onSaveInstanceState` duplicados pueden eliminarse
    // Ya estás manejando la restauración en `onCreateView`.
    // El método `onViewStateRestored` es útil si necesitas restaurar la vista
    // después de que el estado guardado se haya aplicado, pero para tu caso,
    // `onCreateView` y `onViewCreated` son suficientes con la lógica agregada.

    private fun ejecutarFiltro(query: String) {
        // Asegúrate de que el spinner tenga un elemento seleccionado antes de intentar obtener su valor
        val categoryToFilter = if (::categoriaSpinner.isInitialized && categoriaSpinner.selectedItem != null) {
            categoriaSpinner.selectedItem.toString()
        } else {
            "Todas" // Valor por defecto si el spinner no está listo
        }
        filtroBusquedaviewModel.filtrarPublicaciones(query, categoryToFilter)
    }
}
