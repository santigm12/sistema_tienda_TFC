package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dam.proyecto.appmovil.databinding.FragmentPedidosBinding
import dam.proyecto.appmovil.viewModel.PedidosFragmentViewModel
import dam.proyecto.appmovil.viewModel.UserViewModel

class PedidosFragment : Fragment() {

    private lateinit var binding: FragmentPedidosBinding
    private val pedidosViewModel: PedidosFragmentViewModel by viewModels()
    private val usuarioViewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: PedidoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPedidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        usuarioViewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario != null) {
                Log.d("FRAGMENT_DEBUG", "Usuario obtenido: ${usuario.id}")
                adapter = PedidoAdapter(mutableListOf(), mutableListOf(), mutableListOf(), usuario.id)
                binding.lstPedidos.adapter = adapter
                pedidosViewModel.cargarDatosCompletos(usuario.id)
                setupObservers()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.lstPedidos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        pedidosViewModel.ventas.observe(viewLifecycleOwner) { ventas ->
            ventas?.let {
                adapter.actualizarDatos(
                    ventas.toMutableList(),
                    pedidosViewModel.detallesVenta.value?.toMutableList() ?: mutableListOf(),
                    pedidosViewModel.productos.value?.toMutableList() ?: mutableListOf()
                )
                updateEmptyState()
            }
        }

        pedidosViewModel.productos.observe(viewLifecycleOwner) { productos ->
            productos?.let {
                adapter.actualizarDatos(
                    pedidosViewModel.ventas.value?.toMutableList() ?: mutableListOf(),
                    pedidosViewModel.detallesVenta.value?.toMutableList() ?: mutableListOf(),
                    productos.toMutableList()
                )
            }
        }

        pedidosViewModel.detallesVenta.observe(viewLifecycleOwner) { detalles ->
            detalles?.let {
                adapter.actualizarDatos(
                    pedidosViewModel.ventas.value?.toMutableList() ?: mutableListOf(),
                    detalles.toMutableList(),
                    pedidosViewModel.productos.value?.toMutableList() ?: mutableListOf()
                )
            }
        }

        pedidosViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.takeIf { it.isNotEmpty() }?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEmptyState() {
        val isEmpty = adapter.itemCount == 0
        binding.lstPedidos.visibility = if (isEmpty) View.GONE else View.VISIBLE
        // Aquí puedes agregar la visibilidad de una vista de "lista vacía" si lo deseas
    }
}