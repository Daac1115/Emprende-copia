package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel

class PerfilPublicacionViewModel : ViewModel() {
    private val _perfilModel = MutableLiveData<PerfilModel?>()
    val PerfilModel: LiveData<PerfilModel?> get() = _perfilModel

    fun getPostsByUser(uid: String) {
        // Consulta a Firebase para obtener el perfil del usuario
        FirebaseDatabase.getInstance().getReference("emprendeIstg/Perfil/$uid")
            .get()
            .addOnSuccessListener { snapshot ->
                val PerfilModelerfil = snapshot.getValue(PerfilModel::class.java)
                _perfilModel.value = PerfilModel()
            }
            .addOnFailureListener {
                _perfilModel.value = null
            }
    }
}