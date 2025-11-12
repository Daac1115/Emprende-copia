package istg.edu.ec.appEmprendeISTGDev.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

class FiltroBusquedaRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("emprendeIstg")

    // Función para obtener las categorías del Firebase
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

    // Función para obtener todas las publicaciones aprobadas
    fun obtenerPublicacionesAprobadas(onDataLoaded: (List<RevisarSolicitudModel>) -> Unit) {
        database.child("publicacion").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val publicaciones = mutableListOf<RevisarSolicitudModel>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key ?: "" // Obtener UID del creador
                        for (pubSnapshot in userSnapshot.children) {
                            val publicacion = pubSnapshot.getValue(RevisarSolicitudModel::class.java)
                            publicacion?.uid = userId // Asignar UID al modelo
                            if (publicacion != null && publicacion.estado == "Aprobado") {
                                publicaciones.add(publicacion)
                            }
                        }
                    }
                }
                onDataLoaded(publicaciones)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataLoaded(emptyList())
            }
        })
    }

    // Función para filtrar publicaciones en tiempo real
    fun filtrarPublicaciones(
        query: String,
        categoriaSeleccionada: String,
        publicaciones: List<RevisarSolicitudModel>,
        onFiltered: (List<RevisarSolicitudModel>) -> Unit
    ) {
        val resultadosFiltrados = publicaciones.filter { publicacion ->
            val matchesQuery = query.isEmpty() || publicacion.nombreLocal.contains(query, ignoreCase = true)
            val matchesCategoria = categoriaSeleccionada == "Todas" ||
                    publicacion.categoria?.nombreItem == categoriaSeleccionada
            matchesQuery && matchesCategoria
        }
        onFiltered(resultadosFiltrados)
    }
}
