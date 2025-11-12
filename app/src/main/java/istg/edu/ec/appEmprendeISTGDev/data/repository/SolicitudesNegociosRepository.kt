package istg.edu.ec.appEmprendeISTGDev.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel

// Repositorio para manejar las solicitudes de negocios desde Firebase
class SolicitudesNegociosRepository {

    // Referencia a la base de datos de Firebase (solicitudes de negocio)
    private val database = FirebaseDatabase.getInstance().getReference("emprendeIstg/solicitud")

    // LiveData para almacenar las solicitudes pendientes y observar cambios
    private val _solicitudesPendientes = MutableLiveData<List<AgregarNegocioModel>>()
    val solicitudesPendientes: LiveData<List<AgregarNegocioModel>> get() = _solicitudesPendientes

    // Inicialización: carga las solicitudes pendientes cuando se crea el repositorio
    init {
        cargarSolicitudesPendientes()
    }

    /**
     * Método para cargar las solicitudes pendientes desde Firebase.
     * Solo se consideran las solicitudes cuyo estado es "Pendiente".
     */
    private fun cargarSolicitudesPendientes() {
        // Escuchar los cambios en la base de datos en tiempo real
        database.addValueEventListener(object : ValueEventListener {
            // Este método se ejecuta cuando se recibe una actualización de datos
            override fun onDataChange(snapshot: DataSnapshot) {
                val solicitudes = mutableListOf<AgregarNegocioModel>()

                // Recorrer todos los nodos de la base de datos
                for (userSnapshot in snapshot.children) { // Nivel de usuario
                    for (solicitudSnapshot in userSnapshot.children) { // Nivel de solicitud
                        val solicitud = solicitudSnapshot.getValue(AgregarNegocioModel::class.java)

                        // Solo agregar solicitudes cuyo estado sea "Pendiente"
                        if (solicitud?.estado == "Pendiente") {
                            solicitudes.add(solicitud)
                        }
                    }
                }

                // Actualizar el LiveData con la lista de solicitudes pendientes
                _solicitudesPendientes.value = solicitudes
            }

            // Este método se ejecuta si ocurre un error al intentar obtener los datos
            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario (se podría agregar log de error)
            }
        })
    }
}
