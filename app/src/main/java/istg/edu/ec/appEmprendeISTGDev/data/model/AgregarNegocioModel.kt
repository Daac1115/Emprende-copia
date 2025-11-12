package istg.edu.ec.appEmprendeISTGDev.data.model

// Modelo que representa una solicitud de negocio/emprendimiento enviada por un usuario
data class AgregarNegocioModel(
    var id: String? = null, // ID único generado por Firebase
    var uid: String? = null, // UID del usuario que envió la solicitud
    var nombreUsuario: String = "", // Nombre del usuario que envió la solicitud
    var nombreLocal: String = "",
    var descripcion: String = "",
    var categoria: ItemModel? = null,
    var direccion: String = "",
    var horarioAtencion: List<HorarioModel> = emptyList(), // Lista de horarios de atención
    var descripcionProductosServicios: List<ProductoServicioPrecioModel> = emptyList(), // Lista de productos/servicios con precios
    var enlacesProductos: List<String> = emptyList(), // Lista de enlaces de productos
    var enlacesExternos: List<String> = emptyList(),  // Lista de enlaces externos
    var telefonoWhatsApp: String = "", // <--- NUEVO: número de contacto / WhatsApp
    var estado: String = "Pendiente", // Estado inicial de la solicitud
    var comentario: String = "" // Comentario de la solicitud
)


// Modelo que representa un horario de atención del negocio
data class HorarioModel(
    val diaInicio: String,
    val diaFin: String,
    val horaApertura: String,
    val horaCierre: String
){
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "")
}

// Modelo que representa un producto o servicio con su precio
data class ProductoServicioPrecioModel(
    var nombreProductoServicio: String = "",
    var precioProductoServicio: Double = 0.0
)
