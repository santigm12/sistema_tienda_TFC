package dam.proyecto.appmovil.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dam.proyecto.appmovil.modelo.Producto

class CarritoViewModel : ViewModel(){
    private val _productosEnCarrito = MutableLiveData<MutableList<Producto>>(mutableListOf())
    val productosEnCarrito: LiveData<MutableList<Producto>> = _productosEnCarrito

    fun añadirProducto(producto: Producto) {
        _productosEnCarrito.value?.add(producto)
        _productosEnCarrito.value = _productosEnCarrito.value
    }

    fun eliminarProductoDelCarrito(producto: Producto) {
        val listaActual = _productosEnCarrito.value?.toMutableList() ?: return
        listaActual.remove(producto)
        _productosEnCarrito.value = listaActual
    }


    fun vaciarCarrito() {
        _productosEnCarrito.value = mutableListOf()
    }
}