package dam.proyecto.appmovil.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dam.proyecto.appmovil.modelo.Producto

class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("carrito_prefs", 0)
    private val gson = Gson()

    private val _productosEnCarrito = MutableLiveData<MutableList<Producto>>(mutableListOf())
    val productosEnCarrito: LiveData<MutableList<Producto>> = _productosEnCarrito

    init {
        cargarCarritoGuardado()
    }

    fun añadirProducto(producto: Producto) {
        _productosEnCarrito.value?.add(producto)
        _productosEnCarrito.value = _productosEnCarrito.value
        guardarCarrito()
    }

    fun eliminarProductoDelCarrito(producto: Producto) {
        val listaActual = _productosEnCarrito.value?.toMutableList() ?: return
        listaActual.remove(producto)
        _productosEnCarrito.value = listaActual
        guardarCarrito()
    }

    fun vaciarCarrito() {
        _productosEnCarrito.value = mutableListOf()
        guardarCarrito()
    }

    private fun guardarCarrito() {
        val lista = _productosEnCarrito.value ?: mutableListOf()
        val json = gson.toJson(lista)
        prefs.edit().putString("carrito_json", json).apply()
    }

    private fun cargarCarritoGuardado() {
        val json = prefs.getString("carrito_json", null)
        if (json != null) {
            val tipo = object : TypeToken<MutableList<Producto>>() {}.type
            val listaGuardada: MutableList<Producto> = gson.fromJson(json, tipo)
            _productosEnCarrito.value = listaGuardada
        }
    }
}
