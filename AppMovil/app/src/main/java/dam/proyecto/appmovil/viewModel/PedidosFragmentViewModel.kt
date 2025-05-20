package dam.proyecto.appmovil.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class PedidosFragmentViewModel : ViewModel() {


    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> get() = _productos

    private val _ventas = MutableLiveData<List<Venta>>()
    val ventas: LiveData<List<Venta>> get() = _ventas

    private val _detallesVenta = MutableLiveData<List<DetalleVenta>>()
    val detallesVenta: LiveData<List<DetalleVenta>> get() = _detallesVenta

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2/sistema-tienda-api/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()


    interface ProductoApi {
        @GET("productos/leer.php")
        suspend fun obtenerProductos(): List<Producto>
    }

    interface VentaApi {
        @GET("ventas/leer.php")
        suspend fun obtenerVentasPorCliente(@Query("cliente_id") clienteId: Int): List<Venta>
    }

    interface DetalleVentaApi {
        @GET("detalle-venta/leer.php")
        suspend fun obtenerDetallesPorVenta(@Query("venta_id") ventaId: Int): List<DetalleVenta>
    }

    private val productoApi = retrofit.create(ProductoApi::class.java)
    private val ventaApi = retrofit.create(VentaApi::class.java)
    private val detalleVentaApi = retrofit.create(DetalleVentaApi::class.java)


    fun cargarProductos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = productoApi.obtenerProductos()
                _productos.postValue(respuesta)
            } catch (e: Exception) {
                manejarError("Error al cargar productos", e)
            }
        }
    }


    fun cargarVentas(clienteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = ventaApi.obtenerVentasPorCliente(clienteId)
                _ventas.postValue(respuesta)

                // Si hay ventas, cargar sus detalles
                respuesta.forEach { venta ->
                    cargarDetallesVenta(venta.id)
                }
            } catch (e: Exception) {
                manejarError("Error al cargar ventas", e)
            }
        }
    }


    private suspend fun cargarDetallesVenta(ventaId: Int) {
        try {
            val respuesta = detalleVentaApi.obtenerDetallesPorVenta(ventaId)


            val detallesActuales = _detallesVenta.value ?: emptyList()
            _detallesVenta.postValue(detallesActuales + respuesta)

        } catch (e: Exception) {
            manejarError("Error al cargar detalles de venta", e)
        }
    }


    private fun manejarError(mensaje: String, e: Exception) {
        e.printStackTrace()
        _error.postValue("$mensaje: ${e.localizedMessage}")
        Log.d("PedidosError", mensaje, e)
    }


    fun cargarDatosCompletos(clienteId: Int) {
        cargarProductos()
        cargarVentas(clienteId)
    }
}