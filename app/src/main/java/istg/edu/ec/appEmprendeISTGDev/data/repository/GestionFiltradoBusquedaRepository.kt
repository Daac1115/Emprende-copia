package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ItemModel
import java.text.Normalizer

// Responsable de obtener, guardar, actualizar y verificar la unicidad de categorías e ítems
class GestionFiltradoBusquedaRepository {

    // Referencia a la base de datos de Firebase
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Normaliza un nombre (quitar acentos y convertir a minúsculas) para facilitar su comparación
    private fun normalizarNombre(nombre: String): String {
        return nombre.lowercase().trim() // Normaliza el nombre solo para comparación
    }

    // Obtiene todas las categorías desde Firebase y las devuelve mediante un callback
    fun obtenerCategorias(callback: (List<GestionFiltradoBusquedaModel>) -> Unit) {
        database.child("emprendeIstg/categoria").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val categorias = mutableListOf<GestionFiltradoBusquedaModel>()
                task.result?.children?.forEach { snapshot ->
                    val categoria = snapshot.getValue(GestionFiltradoBusquedaModel::class.java)
                    categoria?.id = snapshot.key // Asignar el ID
                    categorias.add(categoria!!)
                }
                // Ordenar las categorías alfabéticamente
                categorias.sortBy { it.nombreCategoria }
                callback(categorias)
            } else {
                Log.e("FirebaseError", "Error al obtener categorías: ${task.exception?.message}")
                callback(emptyList())
            }
        }
    }

    // Guarda o actualiza una categoría en Firebase y notifica el resultado mediante un callback
    fun guardarCategoria(categoria: GestionFiltradoBusquedaModel, callback: (Boolean) -> Unit) {
        val categoriaId = categoria.id ?: database.child("emprendeIstg/categoria").push().key
        if (categoriaId != null) {
            // Guardar el nombre original de la categoría
            database.child("emprendeIstg/categoria").child(categoriaId).setValue(categoria)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful)
                }
        } else {
            callback(false)
        }
    }

    // Cambia el estado de una categoría en Firebase y notifica el resultado mediante un callback
    fun cambiarEstadoCategoria(categoriaId: String, nuevoEstado: Boolean, callback: (Boolean) -> Unit) {
        database.child("emprendeIstg/categoria").child(categoriaId).child("estado").setValue(nuevoEstado)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    Log.e("FirebaseError", "Error al cambiar el estado de la categoría: ${task.exception?.message}")
                    callback(false)
                }
            }
    }

    // Verifica si el nombre de una categoría es único en Firebase
    fun verificarCategoriaUnica(nombre: String, callback: (Boolean) -> Unit) {
        database.child("emprendeIstg/categoria").get().addOnSuccessListener { snapshot ->
            var isUnique = true
            for (data in snapshot.children) {
                val categoria = data.getValue(GestionFiltradoBusquedaModel::class.java)
                if (categoria != null && normalizarNombre(categoria.nombreCategoria) == nombre) {
                    isUnique = false
                    break
                }
            }
            callback(isUnique)
        }.addOnFailureListener {
            callback(true) // Si hay un error, asumimos que es único
        }
    }

    // Verifica si un ítem es único dentro de una categoría específica en Firebase
    fun verificarItemUnico(categoriaId: String, nombreItem: String, callback: (Boolean) -> Unit) {
        database.child("emprendeIstg/categoria").child(categoriaId).child("items").get().addOnSuccessListener { snapshot ->
            var isUnique = true
            for (data in snapshot.children) {
                val item = data.getValue(ItemModel::class.java)
                if (item != null && item.nombreItem.equals(nombreItem, ignoreCase = true)) {
                    isUnique = false
                    break
                }
            }
            callback(isUnique)
        }.addOnFailureListener {
            callback(true) // Si hay un error, asumimos que es único
        }
    }

    // Verifica si un ítem es único entre todas las categorías en Firebase
    fun verificarItemUnicoGlobal(nombreItem: String, callback: (Boolean) -> Unit) {
        database.child("emprendeIstg/categoria").get().addOnSuccessListener { snapshot ->
            var isUnique = true
            for (data in snapshot.children) {
                val itemsSnapshot = data.child("items")
                for (itemSnapshot in itemsSnapshot.children) {
                    val item = itemSnapshot.getValue(ItemModel::class.java)
                    if (item != null && item.nombreItem.equals(nombreItem, ignoreCase = true)) {
                        isUnique = false
                        break
                    }
                }
                if (!isUnique) break
            }
            callback(isUnique)
        }.addOnFailureListener {
            callback(true) // Si hay un error, asumimos que es único
        }
    }

    // Guarda un nuevo ítem en una categoría específica en Firebase y notifica el resultado mediante un callback
    fun guardarItem(categoriaId: String, item: ItemModel, callback: (Boolean, String?) -> Unit) {
        val itemId = database.child("emprendeIstg/categoria").child(categoriaId).child("items").push().key
        if (itemId != null) {
            item.id = itemId // Asignar el ID al ítem
            database.child("emprendeIstg/categoria").child(categoriaId).child("items").child(itemId).setValue(item)
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful, itemId)
                }
        } else {
            callback(false, null)
        }
    }

    // Cambia el estado de un ítem en Firebase y notifica el resultado mediante un callback
    fun cambiarEstadoItem(categoriaId: String, itemId: String, nuevoEstado: Boolean, callback: (Boolean) -> Unit) {
        database.child("emprendeIstg/categoria").child(categoriaId).child("items").child(itemId).child("estado").setValue(nuevoEstado)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    Log.e("FirebaseError", "Error al cambiar el estado del ítem: ${task.exception?.message}")
                    callback(false)
                }
            }
    }
}