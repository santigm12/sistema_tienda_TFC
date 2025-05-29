package dam.proyecto.appmovil.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta
import dam.proyecto.appmovil.modelo.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

class PedidosFragmentViewModel : ViewModel() {

    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> get() = _productos

    private val _ventas = MutableLiveData<List<Venta>>()
    val ventas: LiveData<List<Venta>> get() = _ventas

    private val _detallesVenta = MutableLiveData<List<DetalleVenta>>()
    val detallesVenta: LiveData<List<DetalleVenta>> get() = _detallesVenta

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://54.173.46.205/sistema-tienda-api/api/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @JsonClass(generateAdapter = true)
    data class ApiResponse(
        @Json(name = "success") val success: Boolean,
        @Json(name = "message") val message: String?,
        @Json(name = "error") val error: String?,
        @Json(name = "venta_id") val ventaId: Int?
    )

    interface ProductoApi {
        @GET("productos/leer.php")
        suspend fun obtenerProductos(): List<Producto>
    }

    interface VentaApi {
        @GET("ventas/leer.php")
        suspend fun obtenerVentasPorCliente(@Query("cliente_id") clienteId: Int): List<Venta>

        @DELETE("ventas/eliminar.php")
        suspend fun eliminarVenta(@Query("id") ventaId: Int): Response<ApiResponse>
    }

    interface DetalleVentaApi {
        @GET("detalle-venta/leer.php")
        suspend fun obtenerDetallesPorVenta(@Query("venta_id") ventaId: Int): List<DetalleVenta>
    }

    private val productoApi = retrofit.create(ProductoApi::class.java)
    private val ventaApi = retrofit.create(VentaApi::class.java)
    private val detalleVentaApi = retrofit.create(DetalleVentaApi::class.java)

    private val webSocketManager = WebSocketManager("ws://54.173.46.205:3001") // URL corregida

    private var usuarioId: Int? = null

    init {
        webSocketManager.connect()
        webSocketManager.event.observeForever { mensaje ->
            Log.d("WebSocket", "Mensaje recibido: $mensaje")
            if (mensaje.contains("ventas_actualizadas")) {

                usuarioId?.let {
                    Log.d("WebSocket", "Actualizando ventas por notificación")
                    cargarVentas(it)

                }
            }
        }
    }

    fun cargarDatosCompletos(clienteId: Int) {
        usuarioId = clienteId
        cargarProductos()
        cargarVentas(clienteId)
    }

    private fun cargarProductos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val productos = productoApi.obtenerProductos()
                Log.d("ViewModel", "Productos cargados: ${productos.size}")
                _productos.postValue(productos)
            } catch (e: Exception) {
                manejarError("Error al cargar productos", e)
            }
        }
    }

    fun cargarVentas(clienteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("ViewModel", "Cargando ventas para cliente: $clienteId")
                val respuesta = ventaApi.obtenerVentasPorCliente(clienteId)
                Log.d("ViewModel", "Ventas recibidas: ${respuesta.size}")

                withContext(Dispatchers.Main) {
                    _ventas.value = respuesta
                    _detallesVenta.value = emptyList()

                    if (respuesta.isEmpty()) {
                        _error.value = "No hay pedidos registrados"
                    } else {
                        respuesta.forEach { venta ->
                            cargarDetallesVenta(venta.id)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al cargar ventas", e)
                withContext(Dispatchers.Main) {
                    _error.value = "Error al cargar los pedidos: ${e.message}"
                }
            }
        }
    }

    private suspend fun cargarDetallesVenta(ventaId: Int) {
        try {
            val respuesta = detalleVentaApi.obtenerDetallesPorVenta(ventaId)
            Log.d("ViewModel", "Detalles cargados para venta $ventaId: ${respuesta.size}")
            withContext(Dispatchers.Main) {
                _detallesVenta.value = (_detallesVenta.value ?: emptyList()) + respuesta
            }
        } catch (e: Exception) {
            Log.e("ViewModel", "Error al cargar detalles para venta $ventaId", e)
        }
    }

    fun cancelarPedido(ventaId: Int, clienteId: Int, contexto: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ventaApi.eliminarVenta(ventaId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            if (apiResponse.success) {
                                Toast.makeText(
                                    contexto,
                                    apiResponse.message ?: "Pedido cancelado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                cargarVentas(clienteId) // Recargar datos después de cancelar
                            } else {
                                Toast.makeText(
                                    contexto,
                                    apiResponse.error ?: "Error al cancelar el pedido",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Error sin mensaje"
                        Toast.makeText(
                            contexto,
                            "Error ${response.code()}: $errorMsg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        contexto,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun manejarError(mensaje: String, e: Exception) {
        _error.postValue("$mensaje: ${e.localizedMessage}")
        Log.e("ViewModelError", mensaje, e)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}