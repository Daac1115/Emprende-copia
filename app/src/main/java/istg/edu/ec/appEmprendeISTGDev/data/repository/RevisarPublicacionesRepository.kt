package istg.edu.ec.appEmprendeISTGDev.data.repository

import com.google.firebase.database.FirebaseDatabase
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

// Repositorio para obtener información de publicaciones específicas desde Firebase
class RevisarPublicacionesRepository {

    // Referencia a la instancia de Firebase Database
    private val database = FirebaseDatabase.getInstance()

    /**
     * Obtiene una publicación específica por su ID de usuario y ID de publicación.
     *
     * @param userId ID del usuario propietario de la publicación.
     * @param publicacionId ID de la publicación a obtener.
     * @param callback Función de retorno para manejar el resultado (puede ser null si falla).
     */
    fun obtenerPublicacionPorId(userId: String, publicacionId: String, callback: (RevisarSolicitudModel?) -> Unit) {
        // Referencia a la publicación dentro de la base de datos
        database.getReference("emprendeIstg/publicacion/$userId/$publicacionId")
            .get()
            .addOnSuccessListener { snapshot ->
                // Convertir el snapshot en un objeto RevisarSolicitudModel y devolverlo en el callback
                callback(snapshot.getValue(RevisarSolicitudModel::class.java))
            }
            .addOnFailureListener {
                // En caso de error, devolver null en el callback
                callback(null)
            }
    }
}
