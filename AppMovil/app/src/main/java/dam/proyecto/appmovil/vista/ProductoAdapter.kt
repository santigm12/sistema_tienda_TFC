package dam.proyecto.appmovil.vista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemProductoBinding
import dam.proyecto.appmovil.modelo.Producto

class ProductoAdapter (
    var productos:List<Producto>,
    val lambda: (Producto) -> Unit
):RecyclerView.Adapter<ProductoHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductoBinding.inflate(inflater,parent,false)
        return ProductoHolder(binding)
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ProductoHolder, position: Int) {
        val producto = productos[position]
        holder.mostrarProducto(producto)
        holder.binding.root.setOnClickListener {
            lambda(producto)
        }
    }

    fun setListaProductos(p:List<Producto>){
        productos = p
        notifyDataSetChanged()
    }

}