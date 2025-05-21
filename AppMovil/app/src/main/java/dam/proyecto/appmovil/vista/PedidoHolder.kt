package dam.proyecto.appmovil.vista

import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta

class PedidoHolder(val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root) {

    lateinit var lstProducto: List<Producto>
    lateinit var venta: Venta
    lateinit var lstDetalleVenta: List<DetalleVenta>

    fun mostrarPedido(v: Venta, listaD: List<DetalleVenta>, listaP: List<Producto>) {
        venta = v
        lstProducto = listaP
        lstDetalleVenta = listaD

        binding.txtIdVenta.text = "ID: ${v.id}"
        binding.txtFechaPedido.text = "Pedido realizado: ${v.fecha}"
        binding.txtEstado.text = v.estado

        val builder = StringBuilder()
        builder.append(String.format("%-20s %-10s %-10s\n", "Producto", "Cantidad", "Subtotal"))

        var totalPedido = 0.0

        for (detalle in lstDetalleVenta) {
            val producto = lstProducto.find { it.id == detalle.producto_id }
            if (producto != null) {
                val nombre = producto.nombre.take(20) // Limita a 20 caracteres
                val cantidad = detalle.cantidad
                val subtotal = producto.precio_con_iva * cantidad
                builder.append(String.format("%-20s %-10d %-10.2f€\n", nombre, cantidad, subtotal))
                totalPedido += subtotal
            }
        }

        binding.txtDetallePedido.text = builder.toString()
        binding.txtTotal.text = "Total: %.2f€".format(totalPedido)
    }
}