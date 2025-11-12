// data/repository/PermisosRepository.kt
package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PermisosRepository {
    private val database = FirebaseDatabase.getInstance().getReference("emprendeIstg/permisos")

    // Verificar si el UID existe como un nodo directamente bajo "emprendeIstg/permisos"
    suspend fun esAdmin(uid: String): Boolean = suspendCancellableCoroutine { continuation ->
        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PermisosRepository", "Error al verificar admin: ${error.message}")
                continuation.resume(false)
            }
        })
    }
}