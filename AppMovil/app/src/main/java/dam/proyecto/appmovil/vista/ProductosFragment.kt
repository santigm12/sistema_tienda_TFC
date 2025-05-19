package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dam.proyecto.appmovil.databinding.FragmentProductosBinding
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.viewModel.ProductosFragmentViewModel

class ProductosFragment : Fragment() {

    private lateinit var binding: FragmentProductosBinding
    private val viewModel: ProductosFragmentViewModel by viewModels()
    private lateinit var adapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar RecyclerView
        adapter = ProductoAdapter(emptyList()) {}
        binding.lstProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.lstProductos.adapter = adapter

        // Observar cambios en productos
        viewModel.productos.observe(viewLifecycleOwner, Observer { productos ->
            if (productos.isNotEmpty()) {
                adapter.setListaProductos(productos)
            } else {
                Toast.makeText(requireContext(), "No hay productos disponibles", Toast.LENGTH_SHORT).show()
            }
        })

        // Cargar los productos desde la API
        viewModel.cargarProductos()
    }
}
