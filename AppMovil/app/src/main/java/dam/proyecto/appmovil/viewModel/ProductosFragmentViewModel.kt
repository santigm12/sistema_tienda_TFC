package dam.proyecto.appmovil.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.proyecto.appmovil.modelo.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

class ProductosFragmentViewModel : ViewModel() {

    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> get() = _productos

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> get() = _cargando

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://54.173.46.205/sistema-tienda-api/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    interface ProductoApi {
        @GET("productos/leer.php")
        suspend fun obtenerProductos(): List<Producto>
    }


    private val productoApi = retrofit.create(ProductoApi::class.java)


    fun cargarProductos() {
        _cargando.postValue(true)  // Comienza la carga

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = productoApi.obtenerProductos()
                _productos.postValue(respuesta)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Error", "Error al cargar los productos: ${e.message}")
                _productos.postValue(emptyList())
            } finally {
                _cargando.postValue(false)  // Finaliza la carga
            }
        }
    }
}