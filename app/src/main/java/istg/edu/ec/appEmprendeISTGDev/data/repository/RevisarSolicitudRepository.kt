package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

// Repositorio para manejar solicitudes y publicaciones en Firebase
class RevisarSolicitudRepository {

    // Instancia de Firebase Database
    private val database = FirebaseDatabase.getInstance()

    /**
     * Obtiene una solicitud específica desde Firebase por su ID de usuario y ID de solicitud.
     *
     * @param userId ID del usuario que realizó la solicitud.
     * @param solicitudId ID de la solicitud a obtener.
     * @param callback Función de retorno para manejar la solicitud obtenida (puede ser null si falla).
     */
    fun obtenerSolicitudPorId(userId: String, solicitudId: String, callback: (RevisarSolicitudModel?) -> Unit) {
        database.getReference("emprendeIstg/solicitud/$userId/$solicitudId")
            .get()
            .addOnSuccessListener { snapshot ->
                val solicitud = snapshot.getValue(RevisarSolicitudModel::class.java)
                Log.d("RevisarSolicitud", "Solicitud obtenida desde Firebase: $solicitud")
                callback(solicitud)
            }
            .addOnFailureListener {
                Log.e("RevisarSolicitud", "Error al cargar la solicitud desde Firebase", it)
                callback(null)
            }
    }

    /**
     * Guarda una publicación en la base de datos en la colección de publicaciones.
     *
     * @param publicacion Objeto de tipo RevisarSolicitudModel que representa la publicación.
     * @param callback Función de retorno que se ejecuta cuando se completa la operación.
     */
    fun guardarPublicacion(publicacion: RevisarSolicitudModel, callback: () -> Unit) {
        val userId = publicacion.uid
        val publicacionId = publicacion.id

        // Verificar que los IDs no sean nulos antes de guardar la publicación
        if (userId != null && publicacionId != null) {
            database.getReference("emprendeIstg/publicacion/$userId/$publicacionId")
                .setValue(publicacion)
                .addOnCompleteListener { callback() }
        }
    }

    /**
     * Actualiza el estado de una solicitud en Firebase.
     *
     * @param userId ID del usuario al que pertenece la solicitud.
     * @param solicitudId ID de la solicitud a actualizar.
     * @param estado Nuevo estado de la solicitud (Ej: "Aprobado", "Rechazado").
     * @param comentario Comentario adicional sobre la decisión.
     * @param callback Función de retorno que se ejecuta cuando se completa la operación.
     */
    fun actualizarEstadoSolicitud(userId: String, solicitudId: String, estado: String, comentario: String, callback: () -> Unit) {
        database.getReference("emprendeIstg/solicitud/$userId/$solicitudId")
            .updateChildren(mapOf("estado" to estado, "comentario" to comentario))
            .addOnCompleteListener { callback() }
    }
}
