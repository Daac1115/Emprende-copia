package istg.edu.ec.appEmprendeISTGDev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.SolicitudesNegociosRepository

// ViewModel para manejar las solicitudes de negocios en la UI
class SolicitudesNegocioViewModel : ViewModel() {

    // Instancia del repositorio que maneja las solicitudes de negocio
    private val repository = SolicitudesNegociosRepository()

    // LiveData que proporciona las solicitudes pendientes observadas desde el repositorio
    val solicitudesPendientes: LiveData<List<AgregarNegocioModel>> = repository.solicitudesPendientes

    /**
     * Método para obtener una solicitud específica por su ID.
     *
     * @param id El ID de la solicitud a buscar.
     * @return La solicitud correspondiente, o null si no se encuentra.
     */
    fun obtenerSolicitudPorId(id: String): AgregarNegocioModel? {
        // Buscar la solicitud en la lista de solicitudes pendientes
        return solicitudesPendientes.value?.find { it.id == id }
    }
}
