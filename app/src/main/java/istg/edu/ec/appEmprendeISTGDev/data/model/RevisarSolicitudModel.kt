package istg.edu.ec.appEmprendeISTGDev.data.model

data class RevisarSolicitudModel(
    var id: String? = null,
    var uid: String? = null,
    var nombreUsuario: String = "",
    var nombreLocal: String = "",
    var descripcion: String = "",
    var categoria: ItemModel? = null,
    var direccion: String = "",
    var horarioAtencion: List<HorarioModel> = emptyList(),
    var descripcionProductosServicios: List<ProductoServicioPrecioModel> = emptyList(),
    var enlacesProductos: List<String> = emptyList(),
    var enlacesExternos: List<String> = emptyList(),
    var telefonoWhatsApp: String? = "", // <--- NUEVO: número de contacto / WhatsApp
    var estado: String = "Pendiente",
    var comentario: String = ""
)

//val telefono: String = null,