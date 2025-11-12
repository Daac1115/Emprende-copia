package istg.edu.ec.appEmprendeISTGDev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.RevisarSolicitudRepository

// ViewModel para gestionar la revisión de solicitudes
class RevisarSolicitudViewModel : ViewModel() {

    // Instancia del repositorio para gestionar las solicitudes
    private val repository = RevisarSolicitudRepository()

    // LiveData que almacena la solicitud que se está revisando
    private val _solicitud = MutableLiveData<RevisarSolicitudModel?>()
    val solicitud: LiveData<RevisarSolicitudModel?> get() = _solicitud

    /**
     * Método para cargar una solicitud específica desde el repositorio.
     *
     * @param userId El ID del usuario al que pertenece la solicitud.
     * @param solicitudId El ID de la solicitud a cargar.
     */
    fun cargarSolicitud(userId: String, solicitudId: String) {
        // Llamar al repositorio para obtener la solicitud desde Firebase
        repository.obtenerSolicitudPorId(userId, solicitudId) { solicitud ->
            _solicitud.value = solicitud // Actualizar el LiveData con la solicitud obtenida
        }
    }

    /**
     * Método para aceptar una solicitud.
     *
     * @param publicacion La solicitud a aprobar.
     * @param comentario El comentario que se adjunta a la aprobación.
     * @param callback Función de retorno que se ejecuta al finalizar el proceso.
     */
    fun aceptarSolicitud(publicacion: RevisarSolicitudModel, comentario: String, callback: () -> Unit) {
        // Cambiar el estado de la solicitud a "Aprobado"
        publicacion.estado = "Aprobado"
        publicacion.comentario = comentario // Establecer el comentario de la aprobación

        // Guardar la publicación en la base de datos
        repository.guardarPublicacion(publicacion) {
            // Actualizar el estado de la solicitud en la base de datos
            repository.actualizarEstadoSolicitud(publicacion.uid!!, publicacion.id!!, "Aprobado", comentario, callback)
        }
    }

    /**
     * Método para rechazar una solicitud.
     *
     * @param publicacion La solicitud a rechazar.
     * @param comentario El comentario que se adjunta al rechazo.
     * @param callback Función de retorno que se ejecuta al finalizar el proceso.
     */
    fun rechazarSolicitud(publicacion: RevisarSolicitudModel, comentario: String, callback: () -> Unit) {
        // Cambiar el estado de la solicitud a "Rechazado"
        publicacion.estado = "Rechazado"
        publicacion.comentario = comentario // Establecer el comentario del rechazo

        // Guardar la publicación en la base de datos
        repository.guardarPublicacion(publicacion) {
            // Actualizar el estado de la solicitud en la base de datos
            repository.actualizarEstadoSolicitud(publicacion.uid!!, publicacion.id!!, "Rechazado", comentario, callback)
        }
    }
}
