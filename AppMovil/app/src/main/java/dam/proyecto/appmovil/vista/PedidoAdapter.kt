package dam.proyecto.appmovil.vista

import android.util.Log
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
        listaVentas = nuevasVentas.toMutableList()
        listaDetalles = nuevosDetalles.toMutableList()
        listaProductos = nuevosProductos.toMutableList()

        Log.d("PedidoAdapter", "Datos actualizados - Ventas: ${listaVentas.size}, Detalles: ${listaDetalles.size}, Productos: ${listaProductos.size}")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoHolder {
        Log.d("PedidoAdapter", "Creando nuevo ViewHolder")
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoHolder(binding, clienteId, this)
    }

    override fun onBindViewHolder(holder: PedidoHolder, position: Int) {
        if (listaVentas.isEmpty()) {
            Log.e("PedidoAdapter", "Lista de ventas vacía en onBindViewHolder")
            return
        }

        val venta = listaVentas[position]
        val detallesParaEstaVenta = listaDetalles.filter { it.venta_id == venta.id }
        val productosDisponibles = listaProductos.filter { producto ->
            detallesParaEstaVenta.any { it.producto_id == producto.id }
        }

        Log.d("PedidoAdapter", "Vinculando posición $position - Venta ID: ${venta.id} con ${detallesParaEstaVenta.size} detalles")
        holder.mostrarPedido(venta, detallesParaEstaVenta, productosDisponibles)
    }

    override fun getItemCount(): Int {
        Log.d("PedidoAdapter", "Total items: ${listaVentas.size}")
        return listaVentas.size
    }

    fun eliminarVenta(ventaId: Int) {
        val index = listaVentas.indexOfFirst { it.id == ventaId }
        if (index != -1) {
            listaVentas.removeAt(index)
            listaDetalles.removeAll { it.venta_id == ventaId }
            notifyItemRemoved(index)
            Log.d("PedidoAdapter", "Venta $ventaId eliminada localmente")
        }
    }
}