package dam.proyecto.appmovil.vista

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentDetalleProductoBinding
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado
import dam.proyecto.appmovil.viewModel.CarritoViewModel

class DetalleProductoFragment : Fragment() {
    private var _binding: FragmentDetalleProductoBinding? = null
    private val binding get() = _binding!!
    private val args: DetalleProductoFragmentArgs by navArgs()
    private val carritoViewModel: CarritoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val producto = args.producto
        mostrarDetallesProducto(producto)

        binding.btnAnadirCarrito.setOnClickListener {
            carritoViewModel.añadirProducto(producto)
            mostrarToastPersonalizado(
                requireContext(),
                "${producto.nombre} añadido al carrito",
                "ok"
            )
        }
    }

    private fun mostrarDetallesProducto(producto: Producto) {
        binding.tvNombreDetalle.text = producto.nombre
        binding.tvPrecioDetalle.text = "Precio: ${producto.precio_con_iva} €"
        binding.tvDescripcionDetalle.text = producto.descripcion

        if (!producto.imagenB64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(producto.imagenB64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.ivImagenDetalle.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.ivImagenDetalle.setImageResource(R.drawable.imagen_error)
                mostrarToastPersonalizado(
                    requireContext(),
                    "Error al cargar la imagen del producto",
                    "error"
                )
            }
        } else {
            binding.ivImagenDetalle.setImageResource(R.drawable.imagen_por_defecto)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}