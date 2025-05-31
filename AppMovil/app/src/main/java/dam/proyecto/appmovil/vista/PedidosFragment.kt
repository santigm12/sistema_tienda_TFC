package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dam.proyecto.appmovil.databinding.FragmentPedidosBinding
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado
import dam.proyecto.appmovil.viewModel.PedidosFragmentViewModel
import dam.proyecto.appmovil.viewModel.UserViewModel

class PedidosFragment : Fragment() {

    private var _binding: FragmentPedidosBinding? = null
    private val binding get() = _binding!!
    private val pedidosViewModel: PedidosFragmentViewModel by viewModels()
    private val usuarioViewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: PedidoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = PedidoAdapter(mutableListOf(), mutableListOf(), mutableListOf(), usuarioViewModel.usuario.value?.id ?: 0)
        binding.lstPedidos.apply {
            adapter = this@PedidosFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        pedidosViewModel.ventas.observe(viewLifecycleOwner) { ventas ->
            Log.d("PedidosFragment", "Ventas recibidas: ${ventas?.size ?: 0}")
            if (ventas.isNullOrEmpty()) {
                showEmptyState(true, "No hay pedidos registrados")
            } else {
                showEmptyState(false)
                actualizarAdapter()
            }
        }

        pedidosViewModel.detallesVenta.observe(viewLifecycleOwner) { detalles ->
            Log.d("PedidosFragment", "Detalles recibidos: ${detalles?.size ?: 0}")
            actualizarAdapter()
        }

        pedidosViewModel.productos.observe(viewLifecycleOwner) { productos ->
            Log.d("PedidosFragment", "Productos recibidos: ${productos?.size ?: 0}")
            actualizarAdapter()
        }

        pedidosViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showEmptyState(true, it)
            }
        }
    }

    private fun loadData() {
        usuarioViewModel.usuario.value?.let { cliente ->
            Log.d("PedidosFragment", "Cargando datos para cliente: ${cliente.id}")
            pedidosViewModel.cargarDatosCompletos(cliente.id)
        } ?: run {
            Log.e("PedidosFragment", "No se encontró ID de cliente")
            showEmptyState(true, "No se pudo identificar al usuario")
            mostrarToastPersonalizado(requireContext(), "No se pudo identificar al usuario", "error")
        }
    }

    private fun actualizarAdapter() {
        val ventas = pedidosViewModel.ventas.value ?: return
        val detalles = pedidosViewModel.detallesVenta.value ?: return
        val productos = pedidosViewModel.productos.value ?: return

        Log.d("PedidosFragment", "Actualizando adapter con: ${ventas.size} ventas, ${detalles.size} detalles, ${productos.size} productos")
        adapter.actualizarDatos(ventas.toMutableList(), detalles.toMutableList(), productos.toMutableList())
    }

    private fun showEmptyState(show: Boolean, message: String = "") {
        if (show) {
            binding.lstPedidos.visibility = View.GONE
            if (message.isNotEmpty()) {
                mostrarToastPersonalizado(requireContext(), message, "info")
            }
        } else {
            binding.lstPedidos.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}