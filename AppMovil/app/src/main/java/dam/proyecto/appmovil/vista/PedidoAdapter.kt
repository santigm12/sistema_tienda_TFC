package dam.proyecto.appmovil.vista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta

class PedidoAdapter(
    private var listaVentas: MutableList<Venta>,
    private var listaDetalles: MutableList<DetalleVenta>,
    private var listaProductos: MutableList<Producto>,
    private val clienteId: Int
) : RecyclerView.Adapter<PedidoHolder>() {


    fun actualizarDatos(
        nuevasVentas: MutableList<Venta>,
        nuevosDetalles: MutableList<DetalleVenta>,
        nuevosProductos: MutableList<Producto>
    ) {
        listaVentas.clear()
        listaVentas.addAll(nuevasVentas)

        listaDetalles.clear()
        listaDetalles.addAll(nuevosDetalles)

        listaProductos.clear()
        listaProductos.addAll(nuevosProductos)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoHolder(binding, clienteId, this)
    }

    override fun onBindViewHolder(holder: PedidoHolder, position: Int) {
        val venta = listaVentas[position]


        val detallesParaEstaVenta = listaDetalles.filter { it.venta_id == venta.id }

        holder.mostrarPedido(venta, detallesParaEstaVenta, listaProductos)
    }

    override fun getItemCount(): Int = listaVentas.size

    fun eliminarVenta(ventaId: Int) {
        val ventaIndex = listaVentas.indexOfFirst { it.id == ventaId }
        if (ventaIndex != -1) {
            listaVentas.removeAt(ventaIndex)

            listaDetalles.removeAll { it.venta_id == ventaId }

            notifyItemRemoved(ventaIndex)
            notifyItemRangeChanged(ventaIndex, itemCount)
        }
    }
}