package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.MisNegociosRepository

// Esta clase gestiona la lógica de presentación y la comunicación entre el repositorio y la interfaz de usuario
class MisNegociosViewModel : ViewModel() {

    // Instancia del repositorio para acceder a los datos y realizar operaciones
    private val repository = MisNegociosRepository()
    // LiveData mutable que almacena las categorías y sus publicaciones asociadas
    private val _categorias = MutableLiveData<Map<String, List<RevisarSolicitudModel>>>()
    // LiveData observable que expone las categorías y sus publicaciones para la UI
    val categorias: LiveData<Map<String, List<RevisarSolicitudModel>>> get() = _categorias
    // Listener de Firebase para las categorías, utilizado para detenerlo cuando sea necesario
    private var categoriasListener: ValueEventListener? = null
    // Listener de Firebase para las publicaciones del usuario, utilizado para detenerlo cuando sea necesario
    private var publicacionesListener: ValueEventListener? = null

    // Carga las categorías y publicaciones del usuario, las procesa y actualiza el LiveData
    fun cargarDatosUsuario(uid: String) {
        // Detener listeners anteriores si existen
        categoriasListener?.let { repository.obtenerCategorias {} }
        publicacionesListener?.let { repository.obtenerPublicacionesUsuario(uid) {} }

        // Escuchar categorías en tiempo real
        categoriasListener = repository.obtenerCategorias { categorias ->
            // Escuchar publicaciones en tiempo real
            publicacionesListener = repository.obtenerPublicacionesUsuario(uid) { publicaciones ->
                // Filtrar publicaciones por estado ("Aprobado" o "Rechazado")
                val publicacionesFiltradas = publicaciones.filter { it.estado in listOf("Aprobado", "Rechazado") }

                // Agrupar publicaciones por categoría
                val publicacionesPorCategoria = publicacionesFiltradas.groupBy { it.categoria?.nombreItem ?: "Sin Categoría" }

                // Ordenar las categorías alfabéticamente
                val categoriasOrdenadas = categorias.sorted()

                // Crear mapa con todas las categorías, excluyendo las que están vacías
                val publicacionesPorCategoriaOrdenada = categoriasOrdenadas.associateWith { categoria ->
                    publicacionesPorCategoria[categoria]?.sortedBy { it.nombreLocal } ?: emptyList()
                }.filter { it.value.isNotEmpty() } // Filtrar categorías vacías

                // Actualizar LiveData solo con categorías que tienen publicaciones
                _categorias.value = publicacionesPorCategoriaOrdenada
            }
        }
    }

    // Elimina una publicación de forma lógica y notifica el resultado mediante un callback
    fun eliminarPublicacion(uid: String, publicacionId: String, callback: (Boolean) -> Unit) {
        repository.eliminarPublicacionLogica(uid, publicacionId) { success ->
            if (success) {
                // No es necesario llamar a cargarDatosUsuario aquí, ya que los listeners actualizan automáticamente
            }
            callback(success)
        }
    }

    // Detiene los listeners de Firebase cuando el ViewModel es destruido
    override fun onCleared() {
        super.onCleared()
        // Detener listeners cuando el ViewModel es destruido
        categoriasListener?.let { repository.obtenerCategorias {} }
        publicacionesListener?.let { repository.obtenerPublicacionesUsuario("") {} }
    }
}