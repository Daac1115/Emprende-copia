package istg.edu.ec.appEmprendeISTGDev.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel

// Repositorio para manejar la obtención de datos de negocios desde Firebase
class HomeRepository {

    // Referencia a la base de datos de Firebase (publicaciones aprobadas)
    private val database = FirebaseDatabase.getInstance().getReference("emprendeIstg/publicacion")

    // LiveData para observar los negocios obtenidos
    private val _negocios = MutableLiveData<List<AgregarNegocioModel>>()
    val negocios: LiveData<List<AgregarNegocioModel>> get() = _negocios

    init {
        cargarNegociosAprobados() // Cargar los negocios aprobados al inicializar el repositorio
    }

    // Método para cargar solo los negocios con estado "Aprobado"
    private fun cargarNegociosAprobados() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val negociosList = mutableListOf<AgregarNegocioModel>()

                // Recorrer la estructura de la base de datos
                for (userSnapshot in snapshot.children) { // Nivel de usuario/emprendedor
                    for (publicacionSnapshot in userSnapshot.children) { // Nivel de publicaciones
                        val negocio = publicacionSnapshot.getValue(AgregarNegocioModel::class.java)

                        // Filtrar negocios aprobados
                        if (negocio?.estado == "Aprobado") {
                            negocio?.let {
                                // Asegurar que las listas de productos y enlaces no sean nulas
                                it.descripcionProductosServicios = it.descripcionProductosServicios ?: emptyList()
                                it.enlacesProductos = it.enlacesProductos ?: emptyList()

                                // Agregar negocio a la lista
                                negociosList.add(it)
                            }
                        }
                    }
                }
                // Actualizar LiveData con la nueva lista de negocios aprobados
                _negocios.postValue(negociosList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores en la consulta a Firebase
            }
        })
    }
}
