package istg.edu.ec.appEmprendeISTGDev.data.model

// Contiene modelos de datos que representan la estructura de las categorías y sus ítems
// Modelo que representa una categoría con sus ítems asociados
data class GestionFiltradoBusquedaModel(
    var id: String? = null,
    var nombreCategoria: String = "",
    var items: MutableMap<String, ItemModel> = mutableMapOf(), // Changed to a map for item storage
    var estado: Boolean = true
)

// Modelo que representa un ítem dentro de una categoría
data class ItemModel(
    var id: String? = null,
    var nombreItem: String = "",
    var estado: Boolean = true
)