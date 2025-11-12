package istg.edu.ec.appEmprendeISTGDev.data.model

// Modelo de datos para representar un usuario en la aplicación
data class UserModel(
    var correo: String = "",
    var nombre: String = "",
    var uid: String = ""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "")
}
