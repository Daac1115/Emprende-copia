package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ItemModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

// Clase que obtiene las categorías disponibles, guarda nuevas solicitudes de negocios y actualiza solicitudes existentes
class AgregarNegocioRepository {

    // Referencia a la base de datos de Firebase
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("emprendeIstg")

    // Obtiene las categorías disponibles desde Firebase y las devuelve mediante un callback
    fun obtenerDatosCategoria(onDataLoaded: (GestionFiltradoBusquedaModel?) -> Unit) {
        database.child("categoria").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val categoria = snapshot.children.firstOrNull()
                    val gestionModel = categoria?.getValue(GestionFiltradoBusquedaModel::class.java)
                    onDataLoaded(gestionModel)
                } else {
                    onDataLoaded(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onDataLoaded(null) // Manejar el error según sea necesario
            }
        })
    }

    // Guarda una nueva solicitud de negocio en Firebase bajo el UID del usuario
    fun guardarNegocio(negocio: AgregarNegocioModel, onComplete: () -> Unit) {
        val uid = negocio.uid ?: return // Asegurarse de que el UID no sea nulo
        val solicitudRef = database.child("solicitud").child(uid).push() // Guardar bajo el UID del usuario
        val negocioId = solicitudRef.key // Obtener el ID único generado por Firebase

        // Crear un mapa para guardar los datos del negocio con los nuevos cambios
        val negocioMap = hashMapOf(
            "id" to negocioId, // Guardar el ID único dentro de la solicitud
            "uid" to negocio.uid, // Guardar el UID del usuario
            "nombreUsuario" to negocio.nombreUsuario, // Guardar el nombre del usuario
            "nombreLocal" to negocio.nombreLocal, // Guardar el nombre del local del negocio/emprendimiento
            "descripcion" to negocio.descripcion, // Guardar la descripción del negocio/emprendimiento
            "categoria" to mapOf(
                "id" to negocio.categoria?.id, // Guardar el ID del ítem seleccionado
                "nombreItem" to negocio.categoria?.nombreItem, // Guardar el nombre del ítem seleccionado
                "estado" to negocio.categoria?.estado // Guardar el estado del ítem seleccionado
            ),
            "direccion" to negocio.direccion, // Guardar la dirección del negocio/emprendimiento
            "horarioAtencion" to negocio.horarioAtencion.map { horario ->
                mapOf(
                    "diaInicio" to horario.diaInicio,
                    "diaFin" to horario.diaFin,
                    "horaApertura" to horario.horaApertura,
                    "horaCierre" to horario.horaCierre
                )
            }, // Guardar el horario de atención
            "descripcionProductosServicios" to negocio.descripcionProductosServicios.map { producto ->
                mapOf(
                    "nombreProductoServicio" to producto.nombreProductoServicio,
                    "precioProductoServicio" to producto.precioProductoServicio
                )
            }, // Guardar el nombre de los productos y sus precios
            "enlacesProductos" to negocio.enlacesProductos, // Guardar los enlaces Drive de los productos/servicios
            "enlacesExternos" to negocio.enlacesExternos, // Guardar los enlaces externos de redes sociales de los productos/servicios
            "telefonoWhatsApp" to negocio.telefonoWhatsApp, // <--- NUEVO: Guardar el número de teléfono / WhatsApp
            "estado" to negocio.estado, // Guardar el estado de la solicitud, por defecto "Pendiente"
            "comentario" to negocio.comentario // Guardar el comentario de la solicitud, por defecto vacío
        )

        // Guardar el negocio en Firebase bajo el UID del usuario
        solicitudRef.setValue(negocioMap).addOnCompleteListener {
            onComplete()
        }
    }

    // Obtiene los datos de una publicación específica desde Firebase y los devuelve mediante un callback
    fun obtenerPublicacionPorId(userId: String, publicacionId: String, callback: (RevisarSolicitudModel?) -> Unit) {
        database.child("publicacion/$userId/$publicacionId")
            .get()
            .addOnSuccessListener { snapshot ->
                val publicacion = snapshot.getValue(RevisarSolicitudModel::class.java)
                callback(publicacion)
            }
            .addOnFailureListener {
                Log.e("FirebaseError", "Error al cargar la publicación desde Firebase", it)
                callback(null)
            }
    }

    // Actualiza una solicitud de negocio existente en Firebase
    fun guardarEdicion(negocio: AgregarNegocioModel, callback: () -> Unit) {
        val userId = negocio.uid
        val solicitudId = negocio.id
        if (userId != null && solicitudId != null) {
            // Guardamos el objeto completo (incluyendo telefono) para mantener compatibilidad
            database.child("solicitud/$userId/$solicitudId")
                .setValue(negocio)
                .addOnCompleteListener { callback() }
        }
    }
}
