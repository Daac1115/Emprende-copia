package istg.edu.ec.appEmprendeISTGDev.data.model

// Modelo de datos para representar el perfil de un usuario en la aplicación
data class PerfilModel(
    var uid: String, // uid ahora es obligatorio
    var nombre: String="",
    var email: String="",
    var telefono: String="",
    val cedula : String="",
    val estudiosActuales: String="",
    val secundaria: String="",
    val linkFacebook: String = "",
    val linkInstagram: String= "",
    val tiktok: String="",
    val edad: Int=0,
    var foto: String=""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", "",
                    "", "", "", "",
                    "", 0, "")
}
