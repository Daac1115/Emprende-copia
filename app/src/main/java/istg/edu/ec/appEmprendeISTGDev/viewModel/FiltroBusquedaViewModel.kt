package istg.edu.ec.appEmprendeISTGDev.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.RevisarSolicitudModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.FiltroBusquedaRepository
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel

class FiltroBusquedaViewModel : ViewModel() {

    private val repository = FiltroBusquedaRepository()

    private val _filteredResults = MutableLiveData<List<RevisarSolicitudModel>>()
    val filteredResults: LiveData<List<RevisarSolicitudModel>> get() = _filteredResults

    private val _allPublicaciones = MutableLiveData<List<RevisarSolicitudModel>>()
    val allPublicaciones: LiveData<List<RevisarSolicitudModel>> get() = _allPublicaciones

    private val _categoriaDatos = MutableLiveData<GestionFiltradoBusquedaModel?>()
    val categoriaDatos: LiveData<GestionFiltradoBusquedaModel?> get() = _categoriaDatos

    fun cargarPublicacionesAprobadas() {
        repository.obtenerPublicacionesAprobadas { publicaciones ->
            _allPublicaciones.value = publicaciones
        }
    }

    fun filtrarPublicaciones(query: String, categoriaSeleccionada: String) {
        val publicaciones = _allPublicaciones.value ?: emptyList()
        repository.filtrarPublicaciones(query, categoriaSeleccionada, publicaciones) { resultados ->
            _filteredResults.value = resultados
        }
    }

    fun cargarDatosCategoria() {
        repository.obtenerDatosCategoria { data ->
            _categoriaDatos.value = data
        }
    }
}
