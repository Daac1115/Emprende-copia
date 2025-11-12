package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.PerfilRepository
import kotlinx.coroutines.launch

class PerfilsViewModel : ViewModel() {
    // Instancia un objeto de la clase Perfil Repository para manejar a lógica de acceso a datos del perfil del usuario
    private val repository = PerfilRepository()

    // LiveData para observar la lista de perfiles
    private val _perfil = MutableLiveData<List<PerfilModel>>()
    val perfil: LiveData<List<PerfilModel>> = _perfil

    // LiveData para observar un perfil específico
    private val _objPerfilModel = MutableLiveData<PerfilModel?>()
    val perfilModel: LiveData<PerfilModel?> = _objPerfilModel

    // LiveData para manejar el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para manejar errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    //Obtiene todos los perfiles almacenados en la base de datos
    fun getPerfil() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.getPerfil { perfil ->
                    _perfil.value = perfil
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error al obtener perfiles: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    //Guarda un perfil en la base de datos utilizando el UID generado por Firebase Authentication
    fun savePerfil(user: PerfilModel) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.saveUser(user)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error al guardar el perfil: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    //Obtiene el perfil de un usuario específico utilizando su UID
    fun getPostsByUser(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.getPostsByUser(userId) { perfil ->
                    _objPerfilModel.value = perfil
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error al obtener el perfil del usuario: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}