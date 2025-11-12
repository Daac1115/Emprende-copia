package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import istg.edu.ec.appEmprendeISTGDev.data.repository.PermisosRepository
import kotlinx.coroutines.launch

class PermisosViewModel : ViewModel() {
    private val repository = PermisosRepository()
    private val _esAdmin = MutableLiveData<Boolean>()
    val esAdmin: LiveData<Boolean> = _esAdmin

    fun checkAdminStatus(uid: String) {
        viewModelScope.launch {
            _esAdmin.value = repository.esAdmin(uid)
        }
    }
}