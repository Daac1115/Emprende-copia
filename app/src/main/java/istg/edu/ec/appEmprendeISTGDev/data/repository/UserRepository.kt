package istg.edu.ec.appEmprendeISTGDev.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.UserModel


class UserRepository {

    // Variable para acceder a la Firebase Realtime Database
    private val db = FirebaseDatabase.getInstance()

    // Guarda un usuario en la base de datos utilizando el UID generado por Firebase Authentication
    fun saveUsuario(user: UserModel) {
        val usersRef = db.getReference("emprendeIstg/User")
        // Usa el UID del usuario como clave principal
        usersRef.child(user.uid).setValue(user)
            .addOnFailureListener { exception ->
                Log.e("UserRepository", "Error al guardar el usuario: ${exception.message}")
            }
    }

    // Obtiene una lista de usuarios cuyo correo coincida con el valor proporcionado
    fun listaUsuarioPorCorreo(correo: String, onResult: (List<UserModel>) -> Unit) {
        val usersRef = db.getReference("emprendeIstg")
        val query = usersRef.child("User").orderByChild("correo").equalTo(correo)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val posts = mutableListOf<UserModel>()
                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(UserModel::class.java)
                    post?.let { posts.add(it) }
                }
                onResult(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", "Error al consultar usuarios por correo: ${error.message}")
            }
        })
    }

    // Obtiene un usuario cuyo correo coincida con el valor proporcionado
    fun obtenerUsuarioPorCorreo(correo: String, onResult: (UserModel?) -> Unit) {
        val usersRef = db.getReference("emprendeIstg")
        val query = usersRef.child("User").orderByChild("correo").equalTo(correo)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("Firebase", "UserRepositoryConsultar:$dataSnapshot")
                val user = dataSnapshot.children.firstOrNull()?.getValue(UserModel::class.java)
                onResult(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserViewModel", "Error al obtener perfil: ${error.message}")
            }
        })
    }
}