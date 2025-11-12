package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.HomeRepository

// ViewModel para manejar la lista de negocios aprobados
class HomeViewModel : ViewModel() {

    // Instancia del repositorio para cargar los negocios aprobados
    private val repository = HomeRepository()

    // LiveData que expone la lista de negocios aprobados a la UI
    val negociosAprobados: LiveData<List<AgregarNegocioModel>> = repository.negocios
}
