package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ItemModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.AgregarNegocioRepository

// Proporciona datos observables (LiveData) para que la UI se actualice automáticamente cuando cambian los datos
class AgregarNegocioViewModel : ViewModel() {

    // Instancia del repositorio para acceder a los datos y realizar operaciones
    private val repository = AgregarNegocioRepository()
    // LiveData mutable que almacena los datos de las categorías
    private val _categoriaDatos = MutableLiveData<GestionFiltradoBusquedaModel?>()
    // LiveData observable que expone los datos de las categorías para la UI
    val categoriaDatos: LiveData<GestionFiltradoBusquedaModel?> get() = _categoriaDatos
    // LiveData mutable que almacena los datos de una publicación específica
    private val _publicacion = MutableLiveData<RevisarSolicitudModel?>()
    // LiveData observable que expone los datos de una publicación específica para la UI
    val publicacion: LiveData<RevisarSolicitudModel?> get() = _publicacion

    // Carga las categorías disponibles desde Firebase y actualiza el LiveData
    fun cargarDatosCategoria() {
        repository.obtenerDatosCategoria { data ->
            _categoriaDatos.value = data
        }
    }

    // Guarda una nueva solicitud de negocio en Firebase y notifica cuando la operación finaliza
    fun guardarNegocio(negocio: AgregarNegocioModel, onComplete: () -> Unit) {
        repository.guardarNegocio(negocio) {
            onComplete()
        }
    }

    // Carga los datos de una publicación específica desde Firebase y actualiza el LiveData
    fun cargarPublicacion(userId: String, publicacionId: String) {
        repository.obtenerPublicacionPorId(userId, publicacionId) { publicacion ->
            _publicacion.value = publicacion
        }
    }

    // Actualiza una solicitud de negocio existente en Firebase y notifica cuando la operación finaliza
    fun guardarEdicion(negocio: AgregarNegocioModel, onComplete: () -> Unit) {
        repository.guardarEdicion(negocio) {
            onComplete()
        }
    }
}