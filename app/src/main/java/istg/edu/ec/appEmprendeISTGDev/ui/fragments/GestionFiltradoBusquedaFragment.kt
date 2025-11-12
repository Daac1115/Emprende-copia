package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ItemModel
import istg.edu.ec.appEmprendeISTGDev.viewModel.GestionFiltradoBusquedaViewModel

// Permite a los administradores gestionar categorías e ítems en la aplicación
// Proporciona una interfaz para agregar, editar, habilitar/deshabilitar y guardar categorías e ítems, asegurando que los datos sean únicos y consistentes antes de ser enviados a Firebase
class GestionFiltradoBusquedaFragment : Fragment(R.layout.fragment_gestion_filtrado_busqueda) {

    // Botón para crear una nueva categoría
    private lateinit var btnNuevaCategoria: Button
    // Contenedor principal donde se muestran las categorías e ítems
    private lateinit var containerCategorias: LinearLayout
    // ViewModel que maneja la lógica de negocio y el estado de la interfaz
    private lateinit var viewModel: GestionFiltradoBusquedaViewModel

    // Inicializa el ViewModel y carga las categorías desde Firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GestionFiltradoBusquedaViewModel(requireContext()) as T
            }
        }).get(GestionFiltradoBusquedaViewModel::class.java)
        // Se llama al método del viewModel para cargar las categorías en la interfaz
        viewModel.cargarCategorias()
    }

    // Infla el diseño del fragmento y devuelve la vista raíz
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestion_filtrado_busqueda, container, false)
    }

    // Configura la interfaz de usuario y observa los cambios en los datos del ViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se inicializan los componentes UI
        btnNuevaCategoria = view.findViewById(R.id.btn_agregar_categoria)
        containerCategorias = view.findViewById(R.id.container_categorias)

        // Configuración del Click Listener para llamar a la función que muestra el diálogo
        // para crear una nueva categoría
        btnNuevaCategoria.setOnClickListener {
            mostrarDialogoNuevaCategoria() // Llamar a la función para mostrar el diálogo
        }

        // Observa los cambios en las categorías y actualiza la UI.
        viewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            containerCategorias.removeAllViews() // Limpiar vistas existentes.

            if (categorias != null) {
                categorias.forEach { categoria ->
                    agregarCategoriaUI(categoria)
                }
            } else {
                Toast.makeText(requireContext(), "No se encontraron categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Muestra un diálogo para crear una nueva categoría y verifica su unicidad
    private fun mostrarDialogoNuevaCategoria() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        input.hint = "Ingrese el nombre de la categoría"

        dialogBuilder.setTitle("Nueva Categoría")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, which ->
                val nombreCategoria = input.text.toString().trim()
                if (nombreCategoria.isNotEmpty()) {
                    // Verificar si la categoría es única antes de guardarla
                    viewModel.verificarCategoriaUnica(nombreCategoria) { isUnique ->
                        if (isUnique) {
                            // Crear la nueva categoría
                            val nuevaCategoria = GestionFiltradoBusquedaModel(nombreCategoria = nombreCategoria)
                            viewModel.guardarCategoria(nuevaCategoria)
                        } else {
                            Toast.makeText(requireContext(), "La categoría ya existe", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingrese un nombre", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }

        dialogBuilder.show()
    }

    // Muestra un diálogo para crear un nuevo ítem y verifica su unicidad
    private fun mostrarDialogoNuevoItem(categoriaId: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        input.hint = "Ingrese el nombre del ítem"

        dialogBuilder.setTitle("Nuevo Ítem")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, which ->
                val nombreItem = input.text.toString().trim()
                if (nombreItem.isNotEmpty()) {
                    // Verificar si el ítem es único entre todas las categorías
                    viewModel.verificarItemUnicoGlobal(nombreItem) { isUnique ->
                        if (isUnique) {
                            // Verificar si el ítem es único en la categoría actual
                            viewModel.verificarItemUnico(categoriaId, nombreItem) { isUniqueInCategory ->
                                if (isUniqueInCategory) {
                                    // Crear el nuevo ítem
                                    val nuevoItem = ItemModel(nombreItem = nombreItem)
                                    viewModel.guardarItem(categoriaId, nuevoItem)
                                } else {
                                    Toast.makeText(requireContext(), "El ítem ya existe en esta categoría", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "El ítem ya existe en otra categoría", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingrese un nombre", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, which -> dialog.cancel() }

        dialogBuilder.show()
    }

    // Crea y muestra la interfaz de usuario para una categoría y sus ítems
    private fun agregarCategoriaUI(categoria: GestionFiltradoBusquedaModel) {
        // Instancia de tipo contenedor para las categorías
        val categoriaLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            tag = categoria.id // Asignar el ID de la categoría al layout
        }

        // EditText para mostrar el nombre de la categoría (editable)
        val categoriaEditText = EditText(requireContext()).apply {
            setText(categoria.nombreCategoria)
            hint = "Nombre de la categoría"
            textSize = 24f // Tamaño en SP
            setTextColor(Color.parseColor("#005585")) // Color del texto ingresado
            setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START) // Alineación del texto
            // Establecer el color del hint programáticamente
            setHintTextColor(ContextCompat.getColor(context, R.color.textHint))
            // Establecer el backgroundTint programáticamente
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
            // Establecer LayoutParams
            val params = LinearLayout.LayoutParams(
                (350 * resources.displayMetrics.density).toInt(), // Convertir dp a píxeles
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(50, 0, 0, 0) // Ajustar margen izquierdo si es necesario
            }
            layoutParams = params
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    categoria.nombreCategoria = s.toString().trim() // Actualizar nombre en modelo
                }
            })
        }

        // Botón para borrar o cambiar estado de la categoría
        val btnBorrarCategoria = layoutInflater.inflate(R.layout.button_activar_desactivar, null) as Button
        val paramsBtnBorrarCategoria = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(50, 0, 0, 0) // Ajusta el margen izquierdo para desplazar el botón
        }
        btnBorrarCategoria.layoutParams = paramsBtnBorrarCategoria
        //btnBorrarCategoria.text = "Borrar Categoría"
        btnBorrarCategoria.setOnClickListener { cambiarEstadoCategoria(categoriaLayout) }

        // TextView para mostrar el estado de la categoría
        val estadoTextView = TextView(requireContext()).apply {
            text = crearTextoEstado(categoria.estado)
            textSize = 20f // Cambia el tamaño de texto a 20sp
            // Establecer LayoutParams con márgenes
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(50, 0, 0, 0) // Ajusta el margen izquierdo para simular el sesgo
            }
            layoutParams = params
        }

        // Crear un Layout horizontal para los botones "Nuevo Item" y "Guardar"
        val buttonContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        // Botón para agregar nuevos ítems
        val btnNuevoItem = LayoutInflater.from(requireContext()).inflate(R.layout.button_agregar, null) as Button
        btnNuevoItem.apply {
            text = "Nuevo Item" // Cambia el texto si es necesario
            textSize = 20f // Tamaño en SP
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(50, 0, 25, 0) // Ajusta el margen izquierdo para simular el sesgo
            }
            layoutParams = params
            setOnClickListener { mostrarDialogoNuevoItem(categoria.id!!) }
        }

        // Botón para guardar la categoría y sus ítems
        val btnGuardarCategoria = LayoutInflater.from(requireContext()).inflate(R.layout.button_guardar, null) as Button
        btnGuardarCategoria.apply {
            text = "Guardar" // Cambia el texto si es necesario
            textSize = 20f // Tamaño en SP
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
            setOnClickListener { guardarCategoria(categoria) }
        }

        // Agregar los botones al contenedor horizontal
        buttonContainer.addView(btnNuevoItem)
        buttonContainer.addView(btnGuardarCategoria)

        // Agregar todos los elementos al layout de la categoría
        categoriaLayout.addView(categoriaEditText)
        categoriaLayout.addView(btnBorrarCategoria)
        categoriaLayout.addView(estadoTextView)

        // Agregar ítems existentes a la UI en orden alfabético.
        categoria.items.values.sortedBy { it.nombreItem }.forEach { item ->
            agregarItemUI(categoriaLayout, item.id!!, item)
        }

        // Agregar el contenedor de botones al layout de la categoría
        categoriaLayout.addView(buttonContainer)

        // Añadir el layout de la categoría al contenedor principal
        containerCategorias.addView(categoriaLayout)
    }

    // Crea y muestra la interfaz de usuario para un ítem dentro de una categoría
    private fun agregarItemUI(categoriaLayout: LinearLayout, itemId: String, item: ItemModel) {
        // Instancia de tipo contenedor para los ítems
        val itemLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            tag = itemId // Asignar el ID del ítem al layout
        }

        // EditText para el nombre del ítem
        val itemEditText = EditText(requireContext()).apply {
            setText(item.nombreItem)
            textSize = 20f // Tamaño en SP
            hint = "Nombre del ítem"
            setTextColor(if (item.estado) Color.BLUE else Color.RED) // Cambiar color según estado
            // Establecer el color del hint programáticamente
            setHintTextColor(ContextCompat.getColor(context, R.color.textHint))
            // Establecer el backgroundTint programáticamente
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
            val params = LinearLayout.LayoutParams(
                (225 * resources.displayMetrics.density).toInt(), // Convertir dp a píxeles
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(50, 0, 25, 0) // Ajusta el margen izquierdo para simular el sesgo
            }
            layoutParams = params
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // No se necesita implementar nada aquí
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Aquí puedes implementar lógica si es necesario antes de que cambie el texto
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.nombreItem = s.toString().trim() // Actualizar nombre en modelo
                }
            })
        }

        //Boton cambiar estado del ítem
        val btnBorrarItem = layoutInflater.inflate(R.layout.button_activar_desactivar, null) as Button
        val paramsBtnBorrarItem = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        btnBorrarItem.layoutParams = paramsBtnBorrarItem
        btnBorrarItem.setOnClickListener {
            // Obtener el ID del ítem desde el layout
            val itemId = itemLayout.tag as? String
            if (itemId != null) {
                cambiarEstadoItem(itemEditText, itemId, item)
            }
        }

        itemLayout.addView(itemEditText)
        itemLayout.addView(btnBorrarItem)

        // Agregar el layout del ítem al layout de la categoría
        categoriaLayout.addView(itemLayout)
    }

    // Crea un texto formateado para mostrar el estado de una categoría o ítem
    private fun crearTextoEstado(estado: Boolean): SpannableString {
        val estadoTexto = if (estado) "Estado: Habilitado" else "Estado: Inhabilitado"
        val spannableString = SpannableString(estadoTexto)

        // Define start and end indices for the label "Estado: "
        val startIndexLabel = 0
        val endIndexLabel = 7 // Length of "Estado: "

        // Check if endIndexLabel is within bounds
        if (endIndexLabel <= estadoTexto.length) {
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndexLabel,
                endIndexLabel,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLACK),
                startIndexLabel,
                endIndexLabel,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Determine start and end indices for "Habilitado" or "Inhabilitado"
        val startIndexState = endIndexLabel + 1 // +1 for space
        val endIndexState = if (estado) startIndexState + "Habilitado".length else startIndexState + "Inhabilitado".length

        // Check if endIndexState is within bounds
        if (endIndexState <= estadoTexto.length) {
            spannableString.setSpan(
                ForegroundColorSpan(if (estado) Color.BLUE else Color.RED),
                startIndexState,
                endIndexState,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }

    // Alterna el estado de una categoría (de habilitada a inhabilitada o viceversa) y actualiza la UI y Firebase
    private fun cambiarEstadoCategoria(categoriaLayout: LinearLayout) {
        val categoriaId = categoriaLayout.tag as? String

        if (categoriaId != null) {
            // Obtener el estado actual de la categoría
            val estadoActual = (categoriaLayout.getChildAt(2) as TextView).text.contains("Habilitado")

            // Alternar el estado
            val nuevoEstado = !estadoActual

            // Actualizar el estado en Firebase
            viewModel.cambiarEstadoCategoria(categoriaId, nuevoEstado) { success ->
                if (success) {
                    val estadoTexto = if (nuevoEstado) "habilitada" else "inhabilitada"
                    Toast.makeText(requireContext(), "Categoría ${estadoTexto} exitosamente", Toast.LENGTH_SHORT).show()
                    // Actualizar la UI con el nuevo estado
                    (categoriaLayout.getChildAt(2) as TextView).text = crearTextoEstado(nuevoEstado)
                } else {
                    Toast.makeText(requireContext(), "Error al cambiar el estado de la categoría", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "No se pudo obtener el ID de la categoría", Toast.LENGTH_SHORT).show()
        }
    }

    // Alterna el estado de un ítem (habilitar o deshabilitar) y actualiza la UI y Firebase
    private fun cambiarEstadoItem(itemEditText: EditText, itemId: String, item: ItemModel) {
        // Alternar el estado del ítem
        item.estado = !item.estado // Cambiar el estado a su opuesto

        // Cambiar el color del texto según el nuevo estado
        val color = if (item.estado) Color.BLUE else Color.RED
        itemEditText.setTextColor(color) // Cambiar color del texto

        // Actualizar el estado en Firebase
        val categoriaLayout = (itemEditText.parent as LinearLayout).parent as LinearLayout
        val categoriaId = categoriaLayout.tag as? String
        if (categoriaId != null) {
            viewModel.cambiarEstadoItem(categoriaId, itemId, item.estado) { success ->
                if (success) {
                    val estadoTexto = if (item.estado) "habilitado" else "inhabilitado"
                    Toast.makeText(requireContext(), "Ítem ${estadoTexto} exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al cambiar el estado del ítem", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Verifica la unicidad de los nombres de los ítems, excluyendo la categoría actual
    private fun verificarItemsUnicos(nombresItems: List<String>, categoriaId: String?, callback: (Boolean) -> Unit) {
        val trimmedNames = nombresItems.map { it.lowercase().trim() }

        val database = FirebaseDatabase.getInstance().reference
        database.child("emprendeIstg/categoria").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val existingItems = mutableSetOf<String>()
                task.result!!.children.forEach { categorySnapshot ->
                    if (categorySnapshot.key != categoriaId) { // Evitar verificar la misma categoría
                        categorySnapshot.child("items").children.forEach { itemSnapshot ->
                            existingItems.add(itemSnapshot.child("nombreItem").value.toString().lowercase().trim())
                        }
                    }
                }

                // Comprobar si hay duplicados
                val hasDuplicates = trimmedNames.any { existingItems.contains(it) }
                callback(!hasDuplicates)
            } else {
                Log.e("FirebaseError", "Error al verificar ítems: ${task.exception?.message}")
                callback(false)
            }
        }
    }

    // Guarda una categoría en Firebase si todos sus ítems son únicos
    private fun guardarCategoria(categoria: GestionFiltradoBusquedaModel) {
        // Verifica si los nombres de los ítems son únicos
        verificarItemsUnicos(categoria.items.values.map { it.nombreItem }, categoria.id) { allUnique ->
            if (allUnique) {
                // Si todos los ítems son únicos, guarda la categoría en el ViewModel
                viewModel.guardarCategoria(categoria)
                Toast.makeText(requireContext(), "Categoría guardada exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                // Si hay ítems duplicados, muestra un mensaje de error
                Toast.makeText(requireContext(), "Los ítems deben ser únicos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}