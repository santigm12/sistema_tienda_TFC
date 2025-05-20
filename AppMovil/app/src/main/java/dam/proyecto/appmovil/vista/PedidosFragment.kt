package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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


        adapter = PedidoAdapter(emptyList(), emptyList(), emptyList());



        setupRecyclerView()

        usuarioViewModel.usuario.observe(viewLifecycleOwner, Observer { usuario ->
            usuario?.let {
                setupObservers()
                pedidosViewModel.cargarDatosCompletos(it.id)
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "No se encontró información del usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.lstPedidos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@PedidosFragment.adapter
        }
    }

    private fun setupObservers() {
        pedidosViewModel.ventas.observe(viewLifecycleOwner, Observer { ventas ->
            ventas?.let { updateAdapterData() }
        })

        pedidosViewModel.productos.observe(viewLifecycleOwner, Observer { productos ->
            productos?.let { updateAdapterData() }
        })

        pedidosViewModel.detallesVenta.observe(viewLifecycleOwner, Observer { detalles ->
            detalles?.let { updateAdapterData() }
        })

        pedidosViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.takeIf { it.isNotEmpty() }?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAdapterData() {
        val ventas = pedidosViewModel.ventas.value ?: emptyList()
        val productos = pedidosViewModel.productos.value ?: emptyList()
        val detalles = pedidosViewModel.detallesVenta.value ?: emptyList()

        if (ventas.isNotEmpty()) {
            adapter.actualizarDatos(ventas, detalles, productos)
        } else {
            Toast.makeText(
                requireContext(),
                "No hay pedidos registrados",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}