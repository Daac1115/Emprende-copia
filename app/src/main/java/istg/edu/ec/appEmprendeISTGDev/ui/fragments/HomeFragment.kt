package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.data.model.AgregarNegocioModel
import istg.edu.ec.appEmprendeISTGDev.databinding.FragmentHomeBinding
import istg.edu.ec.appEmprendeISTGDev.viewModel.HomeViewModel
import istg.edu.ec.appEmprendeISTGDev.ui.adapters.HomeAdapter

// Fragmento principal que muestra los negocios aprobados en la pantalla de inicio
class HomeFragment : Fragment() {

    // Binding para acceder a los elementos de la vista sin necesidad de findViewById
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel para manejar la lógica de la pantalla de inicio
    private lateinit var homeViewModel: HomeViewModel

    // Adaptador para el RecyclerView
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el layout utilizando ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Configurar el RecyclerView
        setupRecyclerView()

        // Configurar los observadores para actualizar la UI cuando haya cambios en los datos
        setupObservers()
    }

    // Configura el RecyclerView con un adaptador vacío inicialmente
    private fun setupRecyclerView() {
        adapter = HomeAdapter(emptyList()) // Se inicializa con una lista vacía
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context) // Establece el diseño en lista vertical
            adapter = this@HomeFragment.adapter // Asigna el adaptador al RecyclerView
        }
    }

    // Observa los cambios en la lista de negocios aprobados y actualiza el adaptador
    private fun setupObservers() {
        homeViewModel.negociosAprobados.observe(viewLifecycleOwner) { negocios ->
            negocios?.let {
                adapter.updateList(negocios) // Se actualiza la lista mostrada en el RecyclerView
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Liberar la referencia de binding para evitar memory leaks
    }
}

