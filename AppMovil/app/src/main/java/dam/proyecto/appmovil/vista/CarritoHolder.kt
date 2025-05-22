package dam.proyecto.appmovil.vista


import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.ItemCarritoBinding
import dam.proyecto.appmovil.modelo.Producto

class CarritoHolder(val binding: ItemCarritoBinding)
    : RecyclerView.ViewHolder(binding.root) {

    lateinit var producto: Producto

    fun mostrarProducto(p: Producto) {
        producto = p
        binding.txtNombreProducto.text = p.nombre
        binding.txtPrecioProducto.text = p.precio_con_iva.toString()
        //binding.txtCantidadCarrito.text = "Cantidad: ${p.cantidad}"


        if (!p.imagenB64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(p.imagenB64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.imagenCarrito.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.imagenCarrito.setImageResource(R.drawable.imagen_error)
            }
        } else {
            binding.imagenCarrito.setImageResource(R.drawable.imagen_por_defecto)
        }
    }
}
