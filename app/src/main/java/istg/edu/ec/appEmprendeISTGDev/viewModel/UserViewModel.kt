package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel
import istg.edu.ec.appEmprendeISTGDev.data.model.UserModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.PerfilRepository
import istg.edu.ec.appEmprendeISTGDev.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    // Instancia un objeto de la clase User Repository para manejar a lógica de acceso a datos del usuario
    private val userRepository = UserRepository()
    // Almacena un objeto de tipo User Model, permitiendo que su valor pueda ser modificado
    private val _usuario = MutableLiveData<UserModel?>()
    // Se expone un LiveData inmutable que permite observar cambios en _usuario sin poder modificarlo directamente
    val usuario: LiveData<UserModel?> = _usuario

    // Se define un MutableLiveData que indica si se está cargando información (booleano)
    private val _isLoading = MutableLiveData<Boolean>()
    // Se expone un LiveData inmutable para observar el estado de carga
    val isLoading: LiveData<Boolean> = _isLoading

    // Se define un MutableLiveData que puede contener un mensaje de error (cadena o nulo)
    private val _error = MutableLiveData<String?>()
    // Se expone un LiveData inmutable para observar el mensaje de error
    val error: LiveData<String?> = _error

    // Guarda un usuario en la base de datos utilizando el UID generado por Firebase Authentication
    fun saveUsuario(usuario: UserModel) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userRepository.saveUsuario(usuario) // Usa la instancia de UserRepository
                _isLoading.value = false
            } catch (e: Exception) { // Especificar el tipo de excepción
                _error.value = "Error al guardar el usuario: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Consulta un usuario por correo electrónico
    fun consultarUsuario(correo: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                userRepository.obtenerUsuarioPorCorreo(correo) { usuario -> // Usa la instancia de UserRepository
                    _usuario.value = usuario
                    _isLoading.value = false
                }
            } catch (e: Exception) { // Especificar el tipo de excepción
                _error.value = "Error al consultar el usuario: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}