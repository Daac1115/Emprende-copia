package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel

class PerfilRepository {

    // Variable para obtener la referencia en la base de datos
    private val db = FirebaseDatabase.getInstance()

    // Obtiene todos los perfiles almacenados en la base de datos
    fun getPerfil(onComplete: (List<PerfilModel>) -> Unit) {
        val datab = db.getReference("emprendeIstg/Perfil")
        datab.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temp = mutableListOf<PerfilModel>()
                for (dataSnapshotVAL in dataSnapshot.children) {
                    val tarea = dataSnapshotVAL.getValue(PerfilModel::class.java) ?: continue
                    temp.add(tarea)
                }
                onComplete(temp)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("PerfilRepository", "Error al obtener perfiles: ${databaseError.message}")
            }
        })
    }

    // Guarda un perfil en la base de datos utilizando el UID generado por Firebase Authentication
    fun saveUser(user: PerfilModel) {
        val usersRef = db.getReference("emprendeIstg/Perfil")
        usersRef.child(user.uid).setValue(user)
            .addOnFailureListener { exception ->
                Log.e("PerfilRepository", "Error al guardar el perfil: ${exception.message}")
            }
    }

    // Obtiene el perfil de un usuario específico utilizando su UID
    fun getPostsByUser(uid: String, onResult: (PerfilModel?) -> Unit) {
        val usersRef = db.getReference("emprendeIstg/Perfil")
        val query = usersRef.child(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val perfil = dataSnapshot.getValue(PerfilModel::class.java)
                onResult(perfil)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PerfilRepository", "Error al obtener el perfil del usuario: ${error.message}")
            }
        })
    }
}
