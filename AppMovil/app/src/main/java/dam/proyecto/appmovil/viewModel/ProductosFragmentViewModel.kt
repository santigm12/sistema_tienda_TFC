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

    // Define la API
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2/sistema-tienda-api/api/") // <-- debe terminar en /
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    interface ProductoApi {
        @GET("productos/leer.php") // <-- ahora sí
        suspend fun obtenerProductos(): List<Producto>
    }


    private val productoApi = retrofit.create(ProductoApi::class.java)

    // Cargar productos desde API
    fun cargarProductos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = productoApi.obtenerProductos()
                if (respuesta.isEmpty()) {
                    _productos.postValue(emptyList())
                } else {
                    _productos.postValue(respuesta)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar error de red u otros
                withContext(Dispatchers.Main) {
                    Log.d("Error", "Error al cargar los productos")
                }
            }
        }
    }
}
