package dam.proyecto.appmovil.vista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemCarritoBinding
import dam.proyecto.appmovil.modelo.Producto

class CarritoAdapter(
    var productos: List<Producto>,
    val onEliminarClick: (Producto) -> Unit
) : RecyclerView.Adapter<CarritoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCarritoBinding.inflate(inflater, parent, false)
        return CarritoHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoHolder, position: Int) {
        val producto = productos[position]
        holder.mostrarProducto(producto)


        holder.binding.btnEliminar.setOnClickListener {
            onEliminarClick(producto)
        }
    }

    override fun getItemCount(): Int = productos.size

    fun setListaProductos(p: List<Producto>) {
        productos = p
        notifyDataSetChanged()
    }
}