package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import istg.edu.ec.appEmprendeISTGDev.R // Asegúrate de que R se importe correctamente

class GuiaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.fragment_guia, container, false)
    }

    // No necesitamos el método newInstance ni los parámetros si no se van a usar
    // Puedes eliminar el companion object si no lo usas para otros propósitos
    // Si en el futuro necesitas pasar datos a GuiaFragment, puedes agregarlos
    // pero para una guía estática, no son necesarios.
}