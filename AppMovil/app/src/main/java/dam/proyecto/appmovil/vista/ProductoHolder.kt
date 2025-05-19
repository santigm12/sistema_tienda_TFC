package dam.proyecto.appmovil.vista

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.ItemProductoBinding
import dam.proyecto.appmovil.modelo.Producto


class ProductoHolder(val binding:ItemProductoBinding)
    : RecyclerView.ViewHolder(binding.root){
    lateinit var producto: Producto
    fun mostrarProducto(p: Producto) {
        producto = p
        binding.txtNombreProducto.text = p.nombre
        binding.txtPrecioProducto.text = p.precio_con_iva.toString()
        binding.txtDescripcionProducto.text = p.descripcion

        // Decodificar imagen en base64 a Bitmap
        if (!p.imagenB64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(p.imagenB64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.imagenProducto.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, puedes poner una imagen por defecto
                binding.imagenProducto.setImageResource(R.drawable.imagen_error)
            }
        } else {
            // Imagen por defecto si no hay imagen
            binding.imagenProducto.setImageResource(R.drawable.imagen_por_defecto)
        }
    }
}