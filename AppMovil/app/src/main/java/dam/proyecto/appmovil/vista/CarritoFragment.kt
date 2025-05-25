package dam.proyecto.appmovil.vista

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dam.proyecto.appmovil.databinding.FragmentCarritoBinding
import dam.proyecto.appmovil.viewModel.CarritoViewModel
import okhttp3.OkHttpClient
import android.widget.Toast
import dam.proyecto.appmovil.viewModel.UserViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class CarritoFragment : Fragment() {

    private lateinit var binding: FragmentCarritoBinding
    private val carritoViewModel: CarritoViewModel by activityViewModels()
    private val usuarioViewModel: UserViewModel by activityViewModels()

    private lateinit var adapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CarritoAdapter(
            emptyList(),
            onEliminarClick = { producto ->
                carritoViewModel.eliminarProductoDelCarrito(producto)
            }
        )

        binding.lstCarrito.layoutManager = LinearLayoutManager(requireContext())
        binding.lstCarrito.adapter = adapter

        carritoViewModel.productosEnCarrito.observe(viewLifecycleOwner) { productos ->
            adapter.setListaProductos(productos)
        }

        binding.btnRealizarPedido.setOnClickListener {
            realizarPedido()
        }
    }

    private fun realizarPedido() {
        val productos = carritoViewModel.productosEnCarrito.value ?: return

        if (productos.isEmpty()) {
            Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = usuarioViewModel.usuario.value
        if (usuario == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val clienteId = usuario.id
        val empleadoId = 1
        val metodoPago = "PENDIENTE"
        val tipoVenta = "ONLINE"
        val descripcion = "Compra desde app"
        val total = productos.sumOf { it.precio_con_iva }

        val ventaJson = """
        {
            "cliente_id": $clienteId,
            "empleado_id": $empleadoId,
            "total": $total,
            "metodo_pago": "$metodoPago",
            "tipo_venta": "$tipoVenta",
            "descripcion": "$descripcion"
        }
    """.trimIndent()

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://52.206.9.18/sistema-tienda-api/api/ventas/crear.php")
            .post(RequestBody.create("application/json".toMediaType(), ventaJson))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error al crear venta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "")
                    val ventaId = jsonObject.getInt("venta_id")

                    productos.forEach { producto ->
                        val detalleJson = """
                        {
                            "venta_id": $ventaId,
                            "producto_id": ${producto.id},
                            "cantidad": 1,
                            "precio_unitario": ${producto.precio_con_iva},
                            "subtotal": ${producto.precio_con_iva}
                        }
                    """.trimIndent()

                        val detalleRequest = Request.Builder()
                            .url("http://52.206.9.18/sistema-tienda-api/api/detalle-venta/crear.php")
                            .post(RequestBody.create("application/json".toMediaType(), detalleJson))
                            .build()

                        client.newCall(detalleRequest).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {

                            }

                            override fun onResponse(call: Call, response: Response) {

                            }
                        })
                    }

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Pedido realizado correctamente", Toast.LENGTH_SHORT).show()
                        carritoViewModel.vaciarCarrito()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error al crear la venta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
