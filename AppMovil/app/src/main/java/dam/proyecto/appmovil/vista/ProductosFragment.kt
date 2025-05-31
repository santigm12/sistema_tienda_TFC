package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentProductosBinding
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado
import dam.proyecto.appmovil.viewModel.CarritoViewModel
import dam.proyecto.appmovil.viewModel.ProductosFragmentViewModel

class ProductosFragment : Fragment() {

    private lateinit var binding: FragmentProductosBinding
    private val viewModel: ProductosFragmentViewModel by viewModels()
    private lateinit var adapter: ProductoAdapter
    private val carritoViewModel: CarritoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductoAdapter(emptyList()) { productoSeleccionado ->
            val action = ProductosFragmentDirections.actionProductosFragmentToDetalleProductoFragment(productoSeleccionado)
            findNavController().navigate(action)
        }

        binding.lstProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.lstProductos.adapter = adapter

        viewModel.productos.observe(viewLifecycleOwner, Observer { productos ->
            if (productos.isNotEmpty()) {
                adapter.setListaProductos(productos)
            } else {
                mostrarToastPersonalizado(
                    requireContext(),
                    "No hay productos disponibles",
                    "error"
                )
            }
        })

        val btnCarrito = view.findViewById<FloatingActionButton>(R.id.btnCarrito)
        btnCarrito.setOnClickListener {
            findNavController().navigate(R.id.action_productosFragment_to_carritoFragment)
        }

        viewModel.cargarProductos()
    }
}