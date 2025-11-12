package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.RevisarPublicacionesRepository

// ViewModel para manejar la revisión de publicaciones
class RevisarPublicacionesViewModel : ViewModel() {

    // Instancia del repositorio que gestiona las publicaciones
    private val repository = RevisarPublicacionesRepository()

    // LiveData que expone la publicación actual a la UI
    private val _publicacion = MutableLiveData<RevisarSolicitudModel?>()
    val publicacion: LiveData<RevisarSolicitudModel?> get() = _publicacion

    // Función para cargar una publicación específica desde el repositorio
    fun cargarPublicacion(userId: String, publicacionId: String) {
        // Llamada al repositorio para obtener la publicación por ID
        repository.obtenerPublicacionPorId(userId, publicacionId) { publicacion ->
            // Actualiza el LiveData con la publicación obtenida
            _publicacion.value = publicacion
        }
    }
}
