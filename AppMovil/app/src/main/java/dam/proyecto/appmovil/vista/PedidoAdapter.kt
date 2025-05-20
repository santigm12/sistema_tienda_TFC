package dam.proyecto.appmovil.vista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta

class PedidoAdapter(
    private var listaVentas: List<Venta>,
    private var listaDetalles: List<DetalleVenta>,
    private var listaProductos: List<Producto>
) : RecyclerView.Adapter<PedidoHolder>() {


    fun actualizarDatos(
        nuevasVentas: List<Venta>,
        nuevosDetalles: List<DetalleVenta>,
        nuevosProductos: List<Producto>
    ) {
        listaVentas = nuevasVentas
        listaDetalles = nuevosDetalles
        listaProductos = nuevosProductos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoHolder, position: Int) {
        val venta = listaVentas[position]


        val detallesParaEstaVenta = listaDetalles.filter { it.venta_id == venta.id }

        holder.mostrarPedido(venta, detallesParaEstaVenta, listaProductos)
    }

    override fun getItemCount(): Int = listaVentas.size
}