package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.adapters.ComentarioPublicacionAdapter
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel
import istg.edu.ec.appEmprendeISTGDev.data.model.HorarioModel
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ItemModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ProductoServicioPrecioModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.viewModel.AgregarNegocioViewModel
import java.util.Calendar

// Permite a los usuarios enviar o editar solicitudes de negocios/emprendimientos
class AgregarNegocioFragment : Fragment() {

    // Variable usadas para cargar las categorías del Firebase
    // TextView que muestra el título de la categoría seleccionada
    private lateinit var txt_categoria: TextView
    // Spinner que muestra las categorías disponibles para el negocio
    private lateinit var spinner_categorias: Spinner

    // Instancia del ViewModel
    private val agregarNegocioViewModel: AgregarNegocioViewModel by viewModels()
    // Botón para enviar o actualizar la solicitud del negocio
    private lateinit var btnEnviarSolicitud: Button
    // Botón para cancelar la operación y regresar al fragmento anterior
    private lateinit var btnCancelar: Button

    // Campo de texto para ingresar el nombre del negocio
    private lateinit var nombre_local_edittext: EditText
    // Campo de texto para ingresar una descripción del negocio
    private lateinit var descripcion_edittext: EditText
    // Campo para el número de WhatsApp
    private lateinit var numero_edittext: EditText
    // Campo de texto para ingresar la dirección del negocio
    private lateinit var direccion_edittext: EditText
    // Campo de texto que muestra los horarios de atención del negocio
    private lateinit var horario_atencion_editetxt: EditText
    // Campo de texto que muestra los productos/servicios con sus precios del negocio
    private lateinit var descripcion_productos_servicios_edittext: EditText
    // Campo de texto que muestra los enlaces (Drive) de productos/servicios del negocio
    private lateinit var enlaces_productos_edittext: EditText
    // Campo de texto que muestra los enlaces externos (Redes sociales) del negocio
    private lateinit var enlaces_externos_edittext: EditText

    // Botones (agregar y eliminar) y lista mutable para horarios
    private lateinit var btnAgregarHorario: Button
    private lateinit var btnBorrarHorario: Button
    private var horarios: MutableList<HorarioModel> = mutableListOf()
    // Lista de referencia para el orden de los días
    private val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    // Botones (agregar y eliminar) y lista mutable para descripción de productos/servicios con sus precios
    private lateinit var btnAgregarDescripcionProductoServicio: Button
    private lateinit var btnBorrarDescripcionProductoServicio: Button
    private var descripcion_productos_servicios: MutableList<ProductoServicioPrecioModel> = mutableListOf()

    // Botones (agregar y eliminar) y lista mutable para enlaces (Drive) de productos/servicios
    private lateinit var btnProductoServicio: Button
    private lateinit var btnBorrarProductoServicio: Button
    private var productos_enlaces: MutableList<String> = mutableListOf()

    // Botones (agregar y eliminar) y lista mutable para enlaces externos (Redes sociales)
    private lateinit var btnAgregarEnlace: Button
    private lateinit var btnBorrarEnlace: Button
    private var enlaces: MutableList<String> = mutableListOf()

    // RecyclerView para cargar el comentario del administrador
    private lateinit var recyclerViewComentarios: RecyclerView
    private lateinit var comentarioAdapter: ComentarioPublicacionAdapter
    private val comentarios: MutableList<String> = mutableListOf()

    // Infla el diseño del fragmento e inicializa las vistas y listeners
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_agregar_negocio, container, false)

        // Inicializar vistas para cargar las categorias
        txt_categoria = view.findViewById(R.id.txt_categoria)
        spinner_categorias = view.findViewById(R.id.spinner_categorias)

        // Inicializar RecyclerView para comentarios
        recyclerViewComentarios = view.findViewById(R.id.recyclerViewComentario)
        recyclerViewComentarios.layoutManager = LinearLayoutManager(requireContext())
        comentarioAdapter = ComentarioPublicacionAdapter(comentarios)
        recyclerViewComentarios.adapter = comentarioAdapter

        // Observar cambios en categoryData del ViewModel
        agregarNegocioViewModel.categoriaDatos.observe(viewLifecycleOwner, Observer { gestionModel ->
            gestionModel?.let {
                txt_categoria.text = it.nombreCategoria
                // Obtener los nombres de los items para el spinner
                val itemNames = it.items.values.map { item -> item.nombreItem }.sorted()
                // Configurar el adaptador del Spinner
                val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, itemNames)
                adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
                spinner_categorias.adapter = adapter
            }
        })

        // Cargar los datos al iniciar el fragmento
        agregarNegocioViewModel.cargarDatosCategoria()

        // Inicializar campos de texto (EditText)
        nombre_local_edittext = view.findViewById(R.id.nombre_local_edittext)
        descripcion_edittext = view.findViewById(R.id.descripcion_edittext)
        numero_edittext = view.findViewById(R.id.numero_edittext)
        direccion_edittext = view.findViewById(R.id.direccion_edittext)
        horario_atencion_editetxt = view.findViewById(R.id.horario_atencion_editetxt)
        descripcion_productos_servicios_edittext = view.findViewById(R.id.descripcion_productos_servicios_edittext)
        enlaces_productos_edittext = view.findViewById(R.id.enlaces_productos_edittext)
        enlaces_externos_edittext = view.findViewById(R.id.enlaces_externos_edittext)

        // Botones del Horario de atención
        btnAgregarHorario = view.findViewById(R.id.btn_horario_atencion)
        btnBorrarHorario = view.findViewById(R.id.btn_borrar_horario)
        btnAgregarHorario.setOnClickListener { mostrarSelectorDia() }
        btnBorrarHorario.setOnClickListener { eliminarUltimoHorario() }

        // Botones de la Descripción de los productos/servicios
        btnAgregarDescripcionProductoServicio = view.findViewById(R.id.btn_descripcion_productos_servicios)
        btnBorrarDescripcionProductoServicio = view.findViewById(R.id.btn_borrar_descripcion_producto_servicio)
        btnAgregarDescripcionProductoServicio.setOnClickListener { agregarProductoServicio() }
        btnBorrarDescripcionProductoServicio.setOnClickListener { eliminarUltimoProductoServicio() }

        // Botones de los enlaces Productos/Servicios
        btnProductoServicio = view.findViewById(R.id.btn_productos_servicios)
        btnBorrarProductoServicio = view.findViewById(R.id.btn_borrar_producto_servicio)
        btnProductoServicio.setOnClickListener { showLinkInputDialogProduct() }
        btnBorrarProductoServicio.setOnClickListener { deleteLastLinkProduct() }

        // Botones de los Enlaces externos
        btnAgregarEnlace = view.findViewById(R.id.btn_enlaces_externos)
        btnBorrarEnlace = view.findViewById(R.id.btn_borrar_enlace)
        btnAgregarEnlace.setOnClickListener { showLinkInputDialog() }
        btnBorrarEnlace.setOnClickListener { deleteLastLink() }

        // Botón para guardar la solicitud de los negocios
        btnEnviarSolicitud = view.findViewById<Button>(R.id.btn_enviar)
        // Configura el listener para el botón
        btnEnviarSolicitud.setOnClickListener {
            // Llamar a la función de guardado
            guardarDatosNegocio()
        }

        // Botón para cancelar
        btnCancelar = view.findViewById<Button>(R.id.btn_cancelar)
        // Configura el listener para el botón
        btnCancelar.setOnClickListener {
            // Regresa al fragmento anterior
            findNavController().navigateUp()
        }

        return view
    }

    // <-- Inicio Sección de Código para editar una publicación existente -->
    // Carga los datos necesarios y configura la lógica para editar una publicación existente
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar argumentos
        val userId = arguments?.getString("userId") ?: ""
        val publicacionId = arguments?.getString("publicacionId")

        // Cargar solicitud si existe un publicacionId
        if (publicacionId != null) {
            agregarNegocioViewModel.cargarPublicacion(userId, publicacionId)
            agregarNegocioViewModel.publicacion.observe(viewLifecycleOwner) { publicacion ->
                publicacion?.let {
                    cargarDatosEnCampos(it)
                }
            }
        } else {
            agregarNegocioViewModel.cargarDatosCategoria()
        }

        // Observar cambios en categoryData del ViewModel
        agregarNegocioViewModel.categoriaDatos.observe(viewLifecycleOwner) { gestionModel ->
            gestionModel?.let {
                txt_categoria.text = it.nombreCategoria
                // Obtener los nombres de los items para el spinner
                val itemNames = it.items.values.map { item -> item.nombreItem }.sorted()
                // Configurar el adaptador del Spinner
                val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, itemNames)
                adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
                spinner_categorias.adapter = adapter

                // Una vez que las categorías están cargadas, cargar la publicación
                val userId = arguments?.getString("userId") ?: ""
                val publicacionId = arguments?.getString("publicacionId")
                if (publicacionId != null) {
                    agregarNegocioViewModel.cargarPublicacion(userId, publicacionId)
                    agregarNegocioViewModel.publicacion.observe(viewLifecycleOwner) { publicacion ->
                        publicacion?.let {
                            cargarDatosEnCampos(it)
                        }
                    }
                }
            }
        }

        // Cargar los datos al iniciar el fragmento
        agregarNegocioViewModel.cargarDatosCategoria()

        // Configura el listener para el botón de publicar
        btnEnviarSolicitud.setOnClickListener {
            if (publicacionId != null) {
                editarPublicacion(userId, publicacionId)
            } else {
                guardarDatosNegocio()
            }
        }
    }

    // Llena los campos del formulario con los datos de una publicación existente
    private fun cargarDatosEnCampos(publicacion: RevisarSolicitudModel) {
        nombre_local_edittext.setText(publicacion.nombreLocal)
        descripcion_edittext.setText(publicacion.descripcion)
        direccion_edittext.setText(publicacion.direccion)
        numero_edittext.setText(publicacion.telefonoWhatsApp) // Cargar número de teléfono
//        numero_edittext.setText(publicacion.telefono ?: "") // Cargar número de teléfono

        // Cargar horarios
        horarios.clear()
        horarios.addAll(publicacion.horarioAtencion)
        mostrarTodosLosHorarios()

        // Cargar productos/servicios
        descripcion_productos_servicios.clear()
        descripcion_productos_servicios.addAll(publicacion.descripcionProductosServicios)
        mostrarTodosLosProductosServicios()

        // Cargar enlaces de productos
        productos_enlaces.clear()
        productos_enlaces.addAll(publicacion.enlacesProductos)
        displayAllLinksProducts()

        // Cargar enlaces externos
        enlaces.clear()
        enlaces.addAll(publicacion.enlacesExternos)
        displayAllLinks()

        // Configurar el Spinner de categorías
        val indiceCategoria = obtenerIndiceCategoria(publicacion.categoria?.nombreItem)
        if (indiceCategoria != -1) {
            spinner_categorias.setSelection(indiceCategoria)
        }

        // Cargar el comentario del administrador en el RecyclerView
        comentarios.clear()
        publicacion.comentario?.let { comentarios.add(it) }
        comentarioAdapter.notifyDataSetChanged()
    }

    // Función para obtener el índice de la categoría seleccionada en el Spinner
    private fun obtenerIndiceCategoria(nombreCategoria: String?): Int {
        val adapter = spinner_categorias.adapter as? ArrayAdapter<String>
        return if (adapter != null) {
            adapter.getPosition(nombreCategoria.orEmpty())
        } else {
            -1
        }
    }

    // Función para validar campos de una publicación existente antes de guardar los cambios en Firebase
    private fun editarPublicacion(userId: String, publicacionId: String) {
        // Obtener los valores de los campos y eliminar espacios en blanco al inicio y al final
        val nombreLocal = nombre_local_edittext.text.toString().trim()
        val descripcion = descripcion_edittext.text.toString().trim()
        val categoriaSeleccionada = spinner_categorias.selectedItem as String
        val direccion = direccion_edittext.text.toString().trim()

        // Validar número de WhatsApp
        val numeroWhats = numero_edittext.text.toString().trim()
        val numeroLimpio = numeroWhats.replace("\\D".toRegex(), "") // Limpiar el número PRIMERO

        // Validar el número YA LIMPIO
        if (numeroLimpio.isEmpty() || numeroLimpio.length != 10) { // Comprobar si no tiene exactamente 10
            Toast.makeText(requireContext(), "Ingrese un número de WhatsApp válido (exactamente 10 dígitos).", Toast.LENGTH_SHORT).show()
            return
        }


        // Longitud mínima requerida para los campos nombre_local_edittext, descripcion_edittext y direccion_edittext
        val longitudMinima = 10

        // Validar que todos los campos estén llenos y cumplan con la longitud mínima
        if (nombreLocal.isEmpty() || nombreLocal.length < longitudMinima) {
            Toast.makeText(requireContext(), "El nombre del local debe tener al menos $longitudMinima caracteres.", Toast.LENGTH_SHORT).show()
            return
        }
        if (descripcion.isEmpty() || descripcion.length < longitudMinima) {
            Toast.makeText(requireContext(), "La descripción debe tener al menos $longitudMinima caracteres.", Toast.LENGTH_SHORT).show()
            return
        }
        if (direccion.isEmpty() || direccion.length < longitudMinima) {
            Toast.makeText(requireContext(), "La dirección debe tener al menos $longitudMinima caracteres.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que los demás campos no estén vacíos
        if (categoriaSeleccionada.isEmpty() || horarios.isEmpty() || descripcion_productos_servicios.isEmpty() ||
            productos_enlaces.isEmpty() || enlaces.isEmpty()) {
            Toast.makeText(requireContext(), "Todos los campos deben estar llenos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear el objeto AgregarNegocioModel con los datos actualizados
        val negocio = crearNegocioDesdeCampos(userId, numeroLimpio)
        negocio.id = publicacionId
        negocio.estado = "Pendiente" // Cambiar el estado a Pendiente para revisión del admin

        // Mostrar el cuadro de diálogo de confirmación
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmación")
            .setMessage("¿Está seguro/a de enviar esta solicitud editada?")
            .setPositiveButton("Sí") { _, _ ->
                agregarNegocioViewModel.guardarEdicion(negocio) {
                    Toast.makeText(requireContext(), "Solicitud enviada para su revisión.", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                    findNavController().navigateUp()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Actualiza una solicitud de negocio existente en Firebase
    private fun crearNegocioDesdeCampos(userId: String, numeroLimpio: String): AgregarNegocioModel {
        val categoriaSeleccionada = spinner_categorias.selectedItem as String

        return AgregarNegocioModel(
            uid = userId,
            nombreUsuario = FirebaseAuth.getInstance().currentUser?.displayName ?: "",
            nombreLocal = nombre_local_edittext.text.toString().trim(),
            descripcion = descripcion_edittext.text.toString().trim(),
            telefonoWhatsApp = numeroLimpio,
            categoria = ItemModel(
                id = obtenerIdCategoria(categoriaSeleccionada),
                nombreItem = categoriaSeleccionada,
                estado = true
            ),
            direccion = direccion_edittext.text.toString().trim(),
            horarioAtencion = horarios.sortedBy { diasSemana.indexOf(it.diaInicio) },
            descripcionProductosServicios = descripcion_productos_servicios.sortedBy { it.nombreProductoServicio.lowercase() },
            enlacesProductos = productos_enlaces.sorted(),
            enlacesExternos = enlaces.sorted(),
            estado = "Pendiente",
            comentario = comentarios.firstOrNull().orEmpty() // Incluir el primer comentario de la lista
        )
    }
    // <-- Fin Sección de Código para editar una publicación existente -->

    // Manejar el Horario de atención
    // Función para seleccionar el horario de atención
    private fun mostrarSelectorDia() {
        val diasSemana = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        var diaInicioSeleccionado: String? = null

        // Primera selección: Día inicial
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona el día inicial")
            .setItems(diasSemana) { _, indice ->
                diaInicioSeleccionado = diasSemana[indice]

                // Segunda selección: Día final
                AlertDialog.Builder(requireContext())
                    .setTitle("Selecciona el día final")
                    .setItems(diasSemana) { _, indiceFinal ->
                        val diaFinalSeleccionado = diasSemana[indiceFinal]

                        // Validar el rango de días
                        if (esRangoValido(diaInicioSeleccionado!!, diaFinalSeleccionado)) {
                            // Si el rango es válido, pedir la hora de inicio
                            mostrarSelectorHoraApertura(diaInicioSeleccionado!!, diaFinalSeleccionado)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "El rango de días no es válido. Por favor, selecciona un rango correcto.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .show()
            }
            .show()
    }

    // Función para validar el rango de días
    private fun esRangoValido(diaInicio: String, diaFin: String): Boolean {
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

        val indiceInicio = diasSemana.indexOf(diaInicio)
        val indiceFin = diasSemana.indexOf(diaFin)

        // Validar que el rango sea de menor a mayor
        return when {
            indiceInicio > indiceFin -> false // Rango inválido (ejemplo: martes a lunes)
            indiceInicio == indiceFin -> true // Caso especial: mismo día (ejemplo: sábado a sábado)
            else -> true // Rango válido
        }
    }

    // Función para seleccionar la hora de apertura del negocio
    private fun mostrarSelectorHoraApertura(diaInicio: String, diaFin: String) {
        val calendario = Calendar.getInstance()
        val horaActual = calendario.get(Calendar.HOUR_OF_DAY)
        val minutoActual = calendario.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, horaSeleccionada, minutoSeleccionado ->
                val horaApertura = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)
                mostrarSelectorHoraCierre(diaInicio, diaFin, horaApertura)
            },
            horaActual,
            minutoActual,
            true
        ).show()
    }

    // Función para seleccionar la hora de cierre del negocio
    private fun mostrarSelectorHoraCierre(diaInicio: String, diaFin: String, horaApertura: String) {
        val calendario = Calendar.getInstance()
        val horaActual = calendario.get(Calendar.HOUR_OF_DAY)
        val minutoActual = calendario.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, horaSeleccionada, minutoSeleccionado ->
                val horaCierre = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)

                if (esHorarioValido(diaInicio, diaFin, horaApertura, horaCierre)) {
                    // Guardar el horario si es válido
                    horarios.add(HorarioModel(diaInicio, diaFin, horaApertura, horaCierre))
                    mostrarTodosLosHorarios()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Conflicto de horarios. Por favor selecciona otro horario.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            horaActual + 1, // Se suma 1 a la hora actual para evitar seleccionar antes de la apertura
            minutoActual,
            true
        ).show()
    }

    // Función para validar el horario
    private fun esHorarioValido(diaInicio: String, diaFin: String, horaApertura: String, horaCierre: String): Boolean {
        // Lista ordenada de días de la semana
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

        // Obtener índices de los días seleccionados
        val indiceInicio = diasSemana.indexOf(diaInicio)
        val indiceFin = diasSemana.indexOf(diaFin)

        // Validar que la hora de apertura sea menor que la hora de cierre
        if (horaApertura >= horaCierre) {
            return false // Conflicto de horarios
        }

        // Verificar que no haya conflictos con horarios existentes
        for (horario in horarios) {
            val indiceHorarioInicio = diasSemana.indexOf(horario.diaInicio)
            val indiceHorarioFin = diasSemana.indexOf(horario.diaFin)

            // Verificar si hay superposición de días
            if (indiceInicio <= indiceHorarioFin && indiceFin >= indiceHorarioInicio) {
                // Si hay superposición de días, verificar las horas
                if (haySuperposicionHoras(horaApertura, horaCierre, horario.horaApertura, horario.horaCierre)) {
                    return false // Conflicto de horarios
                }
            }
        }

        return true // No hay conflicto
    }

    // Función auxiliar para verificar superposición de horas
    private fun haySuperposicionHoras(
        horaInicio1: String,
        horaFin1: String,
        horaInicio2: String,
        horaFin2: String
    ): Boolean {
        return !(horaFin1 <= horaInicio2 || horaInicio1 >= horaFin2)
    }

    // Función para mostrar en el EditText los horarios agregados
    // Función para mostrar todos los horarios ordenados por días de la semana
    private fun mostrarTodosLosHorarios() {
        val textoHorarios = horarios
            .sortedBy { diasSemana.indexOf(it.diaInicio) } // Ordenar por el índice del día de inicio
            .joinToString("\n") { horario ->
                if (horario.diaInicio == horario.diaFin) {
                    "${horario.diaInicio}: ${horario.horaApertura} - ${horario.horaCierre}"
                } else {
                    "${horario.diaInicio} - ${horario.diaFin}: ${horario.horaApertura} - ${horario.horaCierre}"
                }
            }
        horario_atencion_editetxt.setText(textoHorarios) // Actualiza el EditText con todos los horarios
    }

    // Función para eliminar el último horario agregado
    private fun eliminarUltimoHorario() {
        if (horarios.isNotEmpty()) {
            horarios.removeAt(horarios.size - 1) // Eliminar el último horario agregado
            mostrarTodosLosHorarios() // Actualizar la visualización
            Toast.makeText(requireContext(), "Último horario eliminado.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No hay ningún horario para eliminar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Manejo de la Descripción de los productos/servicios
    // Función para normalizar el nombre del producto/servicio eliminando espacios adicionales y convirtiéndolo a minúsculas
    private fun normalizarNombreProductoServicio(nombre: String): String {
        return nombre.trim().lowercase().replace("\\s+".toRegex(), " ")
    }

    // Función para agregar el nombre del producto
    private fun agregarProductoServicio() {
        val dialogNombre = AlertDialog.Builder(requireContext())
        val inputNombre = EditText(requireContext())
        dialogNombre.setTitle("Agregar Producto/Servicio y Precio")
        dialogNombre.setMessage("Ingrese el nombre del producto/servicio:")
        dialogNombre.setView(inputNombre)
        dialogNombre.setPositiveButton("Agregar") { _, _ ->
            val nombreProducto = inputNombre.text.toString().trim()
            if (nombreProducto.isEmpty()) {
                Toast.makeText(requireContext(), "El nombre del producto no puede estar vacío.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Normalizar el nombre del producto
            val nombreNormalizado = normalizarNombreProductoServicio(nombreProducto)

            // Verificar si el nombre ya existe en la lista
            if (descripcion_productos_servicios.any { normalizarNombreProductoServicio(it.nombreProductoServicio) == nombreNormalizado }) {
                Toast.makeText(requireContext(), "El producto '$nombreProducto' ya ha sido agregado previamente.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Si es único, continuar con el proceso
            mostrarDialogoPrecio(nombreProducto)
        }
        dialogNombre.setNegativeButton("Cancelar", null)
        dialogNombre.show()
    }

    // Función para agregar el precio del producto
    private fun mostrarDialogoPrecio(nombreProducto: String) {
        val dialogPrecio = AlertDialog.Builder(requireContext())
        val inputPrecio = EditText(requireContext())
        dialogPrecio.setTitle("Agregar Precio")
        dialogPrecio.setMessage("Ingrese el precio del producto/servicio (con dos decimales):")
        dialogPrecio.setView(inputPrecio)
        dialogPrecio.setPositiveButton("Agregar") { _, _ ->
            val precioTexto = inputPrecio.text.toString().trim()
            if (precioTexto.isEmpty()) {
                Toast.makeText(requireContext(), "El precio no puede estar vacío.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            val precio = precioTexto.toDoubleOrNull()
            if (precio == null || precio <= 0.0 || !precioTexto.matches(Regex("\\d+\\.\\d{2}"))) {
                Toast.makeText(requireContext(), "El precio debe ser un valor positivo con dos decimales.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            // Agregar el producto/servicio a la lista
            descripcion_productos_servicios.add(ProductoServicioPrecioModel(nombreProducto, precio))
            mostrarTodosLosProductosServicios()
        }
        dialogPrecio.setNegativeButton("Cancelar", null)
        dialogPrecio.show()
    }

    // Función para mostrar en el EditText los productos/servicios con sus precios agregados
    // Función para mostrar todos los productos/servicios ordenados alfabéticamente
    private fun mostrarTodosLosProductosServicios() {
        val textoProductos = descripcion_productos_servicios
            .sortedBy { it.nombreProductoServicio.lowercase() } // Ordenar por nombre (insensible a mayúsculas/minúsculas)
            .joinToString("\n") { producto ->
                "${producto.nombreProductoServicio}: $${producto.precioProductoServicio}"
            }
        descripcion_productos_servicios_edittext.setText(textoProductos) // Actualiza el EditText con todos los productos/servicios
    }

    // Función para eliminar el último producto/servicio con sus precio agregado
    private fun eliminarUltimoProductoServicio() {
        if (descripcion_productos_servicios.isNotEmpty()) {
            descripcion_productos_servicios.removeAt(descripcion_productos_servicios.size - 1)
            mostrarTodosLosProductosServicios()
            Toast.makeText(requireContext(), "Último producto/servicio eliminado.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No hay productos/servicios para eliminar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Manejo de los enlaces de los Productos/Servicios (Drive)
    // Función para agregar el enlace drive del producto/servicio
    private fun showLinkInputDialogProduct() {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Enlace Producto/Servicio")
            .setMessage("Introduce un enlace (debe ser del Drive):")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val producto_enlace = input.text.toString().trim() // Eliminar espacios en blanco
                if (producto_enlace.isEmpty()) {
                    Toast.makeText(requireContext(), "Debe ingresar un enlace Drive.", Toast.LENGTH_SHORT).show()
                } else if (!isValidLinkProduct(producto_enlace)) {
                    Toast.makeText(requireContext(), "Enlace Drive no válido.", Toast.LENGTH_SHORT).show()
                } else if (productos_enlaces.contains(producto_enlace)) {
                    Toast.makeText(requireContext(), "El enlace ya ha sido agregado previamente.", Toast.LENGTH_SHORT).show()
                } else {
                    productos_enlaces.add(producto_enlace)
                    displayAllLinksProducts()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Función para validar el enlace Drive del producto/servicio
    private fun isValidLinkProduct(linkProduct: String): Boolean {
        return linkProduct.startsWith("https://drive.google.com/")
    }

    // Función para agregar el enlace Drive del producto/servicio a la lista (EditText)
    // Función para mostrar todos los enlaces de productos ordenados alfabéticamente
    private fun displayAllLinksProducts() {
        val linksTextProducts = productos_enlaces
            .sorted() // Ordenar alfabéticamente
            .joinToString("\n") { it }
        enlaces_productos_edittext.setText(linksTextProducts) // Actualiza el EditText con todos los enlaces
    }

    // Función para eliminar el último enlace Drive del producto/servicio agregado
    private fun deleteLastLinkProduct() {
        if (productos_enlaces.isNotEmpty()) {
            productos_enlaces.removeAt(productos_enlaces.size - 1) // Eliminar el último enlace agregado
            displayAllLinksProducts() // Actualizar la visualización
            Toast.makeText(requireContext(), "Último enlace eliminado.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No hay ningún enlace para eliminar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Manejo de los Enlaces externos
    // Función para agregar el enlace de las redes sociales del producto/servicio o del negocio
    private fun showLinkInputDialog() {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Enlace Externo")
            .setMessage("Introduce un enlace (debe ser de una red social):")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val enlace = input.text.toString().trim() // Eliminar espacios en blanco
                if (enlace.isEmpty()) {
                    Toast.makeText(requireContext(), "Debe ingresar un enlace.", Toast.LENGTH_SHORT).show()
                } else if (!isValidLink(enlace)) {
                    Toast.makeText(requireContext(), "Enlace no válido.", Toast.LENGTH_SHORT).show()
                } else if (enlaces.contains(enlace)) {
                    Toast.makeText(requireContext(), "El enlace ya ha sido agregado previamente.", Toast.LENGTH_SHORT).show()
                } else {
                    enlaces.add(enlace)
                    displayAllLinks()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Función para validar el enlace de las redes sociales del producto/servicio o del negocio
    private fun isValidLink(link: String): Boolean {
        return link.startsWith("https://www.youtube.com/") ||
                link.startsWith("https://youtu.be/") ||
                link.startsWith("https://www.facebook.com/") ||
                link.startsWith("https://fb.com/") ||
                link.startsWith("https://www.instagram.com/") ||
                link.startsWith("https://instagr.am/") ||
                link.startsWith("https://www.twitter.com/") ||
                link.startsWith("https://t.co/") ||
                link.startsWith("https://www.tiktok.com/") ||
                link.startsWith("https://vm.tiktok.com/") ||
                link.startsWith("https://www.whatsapp.com/") ||
                link.startsWith("https://wa.me/") ||
                link.startsWith("https://telegram.org/") ||
                link.startsWith("https://t.me/") ||
                link.startsWith("https://www.messenger.com/") ||
                link.startsWith("https://m.me/") ||
                link.startsWith("https://www.threads.net/")
    }

    // Función para agregar el enlace de las redes sociales del producto/servicio o del negocio a la lista (EditText)
    // Función para mostrar todos los enlaces externos ordenados alfabéticamente
    private fun displayAllLinks() {
        val linksText = enlaces
            .sorted() // Ordenar alfabéticamente
            .joinToString("\n") { it }
        enlaces_externos_edittext.setText(linksText) // Actualiza el EditText con todos los enlaces
    }

    // Función para eliminar el último enlace de las redes sociales del producto/servicio o del negocio agregado
    private fun deleteLastLink() {
        if (enlaces.isNotEmpty()) {
            enlaces.removeAt(enlaces.size - 1) // Eliminar el último enlace agregado
            displayAllLinks() // Actualizar la visualización
            Toast.makeText(requireContext(), "Último enlace eliminado.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No hay ningún enlace para eliminar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para obtener el ID de la categoría seleccionada
    private fun obtenerIdCategoria(nombreCategoria: String): String? {
        agregarNegocioViewModel.categoriaDatos.value?.items?.forEach { (_, item) ->
            if (item.nombreItem == nombreCategoria) {
                return item.id
            }
        }
        return null
    }

    // Función para obtener el ID del usuario actual
    private fun obtenerUidUsuario(): String? {
        // Implementa esta función según tu lógica para obtener el UID del usuario actual
        // Por ejemplo, si usas Firebase Authentication:
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    // Función para guardar los datos del negocio
    private fun guardarDatosNegocio() {
        // Obtener los valores de los campos y eliminar espacios en blanco al inicio y al final
        val nombreLocal = nombre_local_edittext.text.toString().trim()
        val descripcion = descripcion_edittext.text.toString().trim()
        val categoriaSeleccionada = spinner_categorias.selectedItem as String
        val direccion = direccion_edittext.text.toString().trim()

    // Validar número de WhatsApp
        val numeroWhats = numero_edittext.text.toString().trim()
        val numeroLimpio = numeroWhats.replace("\\D".toRegex(), "") // Limpiar el número PRIMERO

        // Validar el número YA LIMPIO
        if (numeroLimpio.isEmpty() || numeroLimpio.length != 10) { // Comprobar si no tiene exactamente 10
            Toast.makeText(requireContext(), "Ingrese un número de WhatsApp válido (exactamente 10 dígitos).", Toast.LENGTH_SHORT).show()
            return
        }


        // Longitud mínima requerida para los campos nombre_local_edittext, descripcion_edittext y direccion_edittext
        val longitudMinima = 10

        // Validar que todos los campos estén llenos y cumplan con la longitud mínima
        if (nombreLocal.isEmpty() || nombreLocal.length < longitudMinima) {
            Toast.makeText(requireContext(), "El nombre del local debe tener al menos $longitudMinima caracteres.", Toast.LENGTH_SHORT).show()
            return
        }
        if (descripcion.isEmpty() || descripcion.length < longitudMinima) {
            Toast.makeText(requireContext(), "La descripción debe tener al menos $longitudMinima caracteres.", Toast.LENGTH_SHORT).show()
            return
        }
        if (direccion.isEmpty() || direccion.length < longitudMinima) {
            Toast.makeText(requireContext(), "La dirección debe tener al menos $longitudMinima caracteres.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que los demás campos no estén vacíos
        if (categoriaSeleccionada.isEmpty() || horarios.isEmpty() || descripcion_productos_servicios.isEmpty() ||
            productos_enlaces.isEmpty() || enlaces.isEmpty()) {
            Toast.makeText(requireContext(), "Todos los campos deben estar llenos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el UID del usuario actual
        val uidUsuario = obtenerUidUsuario()
        if (uidUsuario == null) {
            Toast.makeText(requireContext(), "No se pudo obtener el UID del usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar el cuadro de diálogo de confirmación
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmación")
            .setMessage("¿Está seguro/a de enviar esta solicitud de su negocio/emprendimiento?")
            .setPositiveButton("Sí") { _, _ ->
                // Si el usuario confirma, proceder a guardar los datos
                // Obtener el nombre del usuario autenticado
                val nombreUsuario = FirebaseAuth.getInstance().currentUser?.displayName ?: ""

                // Crear el objeto AgregarNegocioModel
                val negocio = AgregarNegocioModel(
                    uid = uidUsuario, // Incluir el UID del usuario
                    nombreUsuario = nombreUsuario,
                    nombreLocal = nombreLocal,
                    descripcion = descripcion,
                    telefonoWhatsApp = numeroLimpio,
                    categoria = ItemModel(
                        id = obtenerIdCategoria(categoriaSeleccionada),
                        nombreItem = categoriaSeleccionada,
                        estado = true
                    ),
                    direccion = direccion,
                    horarioAtencion = horarios.sortedBy { diasSemana.indexOf(it.diaInicio) }, // Ordenar horarios
                    descripcionProductosServicios = descripcion_productos_servicios.sortedBy { it.nombreProductoServicio.lowercase() }, // Ordenar productos/servicios
                    enlacesProductos = productos_enlaces.sorted(), // Ordenar enlaces de productos
                    enlacesExternos = enlaces.sorted(), // Ordenar enlaces externos
                    estado = "Pendiente",
                    comentario = ""
                )

                // Guardar el negocio en Firebase
                agregarNegocioViewModel.guardarNegocio(negocio) {
                    Toast.makeText(requireContext(), "Solicitud enviada exitosamente.", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // Si el usuario cancela, mostrar un mensaje amigable
                Toast.makeText(
                    requireContext(),
                    "Si no está seguro/a de la información que enviará, puede revisarla nuevamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    // Función para limpiar todos los campos
    private fun limpiarCampos() {
        // Limpiar los campos de texto
        nombre_local_edittext.text.clear() // Limpiar el campo del nombre del local
        descripcion_edittext.text.clear() // Limpiar el campo de la descripción del local
        numero_edittext.text.clear() // Limpiar el campo del número de Whatsapp
        direccion_edittext.text.clear() // Limpiar el campo de la dirección del local
        horario_atencion_editetxt.text.clear() // Limpiar el campo del horario de atención
        descripcion_productos_servicios_edittext.text.clear() // Limpiar el campo de productos/servicios
        enlaces_productos_edittext.text.clear() // Limpiar el campo de los enlaces de los productos/servicios
        enlaces_externos_edittext.text.clear() // Limpiar el campo de los enlaces de las redes sociales

        // Limpiar las listas
        horarios.clear() // Limpiar la lista de horarios
        descripcion_productos_servicios.clear() // Limpiar la lista de productos/servicios
        productos_enlaces.clear() // Limpiar la lista de los enlaces drive de los productos/servicios
        enlaces.clear()  // Limpiar la lista de los enlaces de las redes sociales
    }
}