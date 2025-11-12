package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel

// Clase responsable de obtener datos (categorías y publicaciones)
// Realiza operaciones relacionadas con las publicaciones del usuario
class MisNegociosRepository {

        // Referencia a la base de datos de Firebase
        private val database = FirebaseDatabase.getInstance().reference

        // Obtiene las categorías desde Firebase y las devuelve mediante un callback
        fun obtenerCategorias(callback: (List<String>) -> Unit): ValueEventListener {
                val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                                val categorias = mutableListOf<String>()
                                for (child in snapshot.children) {
                                        val nombreCategoria = child.child("nombreItem").getValue(String::class.java)
                                        nombreCategoria?.let { categorias.add(it) }
                                }
                                callback(categorias)
                        }

                        override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error al cargar las categorías: ${error.message}")
                                callback(emptyList())
                        }
                }
                database.child("emprendeIstg/categoria/-OGRn5Qiu1zo9KQVBdSx/items").addValueEventListener(listener)
                return listener
        }

        // Obtiene las publicaciones de un usuario desde Firebase y las devuelve mediante un callback
        fun obtenerPublicacionesUsuario(uid: String, callback: (List<RevisarSolicitudModel>) -> Unit): ValueEventListener {
                val listener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                                val publicaciones = mutableListOf<RevisarSolicitudModel>()
                                for (child in snapshot.children) {
                                        val publicacion = child.getValue(RevisarSolicitudModel::class.java)
                                        publicacion?.let {
                                                it.id = child.key // Asignar el ID de la publicación
                                                publicaciones.add(it)
                                        }
                                }
                                callback(publicaciones)
                        }

                        override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error al cargar las publicaciones del usuario: ${error.message}")
                                callback(emptyList())
                        }
                }
                database.child("emprendeIstg/publicacion/$uid").addValueEventListener(listener)
                return listener
        }

        // Realiza una eliminación lógica de una publicación actualizando su estado a "Rechazado"
        fun eliminarPublicacionLogica(uid: String, publicacionId: String, callback: (Boolean) -> Unit) {
                val publicacionRef = database.child("emprendeIstg/publicacion/$uid/$publicacionId")
                publicacionRef.child("estado").setValue("Rechazado")
                        .addOnSuccessListener {
                                callback(true) // Éxito al actualizar el estado
                        }
                        .addOnFailureListener {
                                Log.e("FirebaseError", "Error al eliminar la publicación: ${it.message}")
                                callback(false) // Error al actualizar el estado
                        }
        }
}