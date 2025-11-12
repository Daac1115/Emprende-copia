package istg.edu.ec.appEmprendeISTGDev.viewModel

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import istg.edu.ec.appEmprendeISTGDev.data.model.GestionFiltradoBusquedaModel
import istg.edu.ec.appEmprendeISTGDev.data.model.ItemModel
import istg.edu.ec.appEmprendeISTGDev.data.repository.GestionFiltradoBusquedaRepository

// Gestiona las operaciones relacionadas con categorías e ítems
class GestionFiltradoBusquedaViewModel(
    // Contexto de la aplicación utilizado para mostrar mensajes de confirmación o error
    private val context: Context
    ) : ViewModel() {

    // Instancia del repositorio para acceder a los datos y realizar operaciones
    private val repository = GestionFiltradoBusquedaRepository()
    // LiveData mutable que almacena las categorías disponibles
    private val _categorias = MutableLiveData<List<GestionFiltradoBusquedaModel>>()
    // LiveData observable que expone las categorías para la UI
    val categorias: LiveData<List<GestionFiltradoBusquedaModel>> get() = _categorias

    // Carga todas las categorías desde Firebase y actualiza el LiveData
    fun cargarCategorias() {
        repository.obtenerCategorias { categorias ->
            _categorias.value = categorias
        }
    }

    // Guarda o actualiza una categoría en Firebase y recarga las categorías
    fun guardarCategoria(categoria: GestionFiltradoBusquedaModel) {
        // Guardar el nombre original sin normalizar
        repository.guardarCategoria(categoria) { success ->
            if (success) {
                cargarCategorias() // Recargar categorías después de guardar
            } else {
                // Manejar error al guardar
            }
        }
    }

    // Cambia el estado de una categoría en Firebase y notifica el resultado mediante un callback
    fun cambiarEstadoCategoria(categoriaId: String, nuevoEstado: Boolean, callback: (Boolean) -> Unit) {
        repository.cambiarEstadoCategoria(categoriaId, nuevoEstado) { success ->
            if (success) {
                cargarCategorias()
            }
            callback(success)
        }
    }


    // Verifica si el nombre de una categoría es único en Firebase
    fun verificarCategoriaUnica(nombre: String, callback: (Boolean) -> Unit) {
        // Normalizar solo para la verificación
        val normalizedNombre = normalizarNombre(nombre)
        repository.verificarCategoriaUnica(normalizedNombre, callback)
    }

    // Guarda un nuevo ítem en una categoría específica en Firebase y recarga las categorías
    fun guardarItem(categoriaId: String, item: ItemModel) {
        repository.guardarItem(categoriaId, item) { success, itemId ->
            if (success && itemId != null) {
                // Recargar categorías después de guardar el ítem
                cargarCategorias()
                Toast.makeText(context, "Ítem guardado exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                // Manejar error al guardar
            }
        }
    }

    // Verifica si un ítem es único dentro de una categoría específica en Firebase
    fun verificarItemUnico(categoriaId: String, nombreItem: String, callback: (Boolean) -> Unit) {
        repository.verificarItemUnico(categoriaId, nombreItem, callback)
    }

    // Verifica si un ítem es único entre todas las categorías en Firebase
    fun verificarItemUnicoGlobal(nombreItem: String, callback: (Boolean) -> Unit) {
        repository.verificarItemUnicoGlobal(nombreItem, callback)
    }

    // Cambia el estado de un ítem en Firebase y notifica el resultado mediante un callback
    fun cambiarEstadoItem(categoriaId: String, itemId: String, nuevoEstado: Boolean, callback: (Boolean) -> Unit) {
        repository.cambiarEstadoItem(categoriaId, itemId, nuevoEstado) { success ->
            if (success) {
                cargarCategorias()
            }
            callback(success)
        }
    }

    // Normaliza un nombre (quitar acentos y convertir a minúsculas) para facilitar su comparación
    fun normalizarNombre(nombre: String): String {
        return nombre.lowercase().trim() // Normaliza el nombre
    }
}
