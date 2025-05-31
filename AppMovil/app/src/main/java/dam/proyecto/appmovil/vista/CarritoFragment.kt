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
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado
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
                mostrarToastPersonalizado(requireContext(), "Producto eliminado del carrito", "ok")
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
            mostrarToastPersonalizado(requireContext(), "El carrito está vacío", "error")
            return
        }

        val usuario = usuarioViewModel.usuario.value
        if (usuario == null) {
            mostrarToastPersonalizado(requireContext(), "Usuario no autenticado", "error")
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
            .url("http://54.173.46.205/sistema-tienda-api/api/ventas/crear.php")
            .post(RequestBody.create("application/json".toMediaType(), ventaJson))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    mostrarToastPersonalizado(requireContext(), "Error al conectar con el servidor", "error")
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
                            .url("http://54.173.46.205/sistema-tienda-api/api/detalle-venta/crear.php")
                            .post(RequestBody.create("application/json".toMediaType(), detalleJson))
                            .build()

                        client.newCall(detalleRequest).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                requireActivity().runOnUiThread {
                                    mostrarToastPersonalizado(requireContext(), "Error al guardar detalles", "error")
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                if (!response.isSuccessful) {
                                    requireActivity().runOnUiThread {
                                        mostrarToastPersonalizado(requireContext(), "Error en detalle de venta", "error")
                                    }
                                }
                            }
                        })
                    }

                    requireActivity().runOnUiThread {
                        mostrarToastPersonalizado(requireContext(), "Pedido realizado con éxito", "ok")
                        carritoViewModel.vaciarCarrito()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        mostrarToastPersonalizado(requireContext(), "Error al procesar la venta", "error")
                    }
                }
            }
        })
    }
}
