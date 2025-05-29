package dam.proyecto.appmovil.vista

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta
import dam.proyecto.appmovil.viewModel.PedidosFragmentViewModel

class PedidoHolder(
    private val binding: ItemPedidoBinding,
    private val clienteId: Int,
    private val adapter: PedidoAdapter
) : RecyclerView.ViewHolder(binding.root) {

    fun mostrarPedido(venta: Venta, detalles: List<DetalleVenta>, productos: List<Producto>) {
        Log.d("PedidoHolder", "Mostrando venta ID: ${venta.id} con ${detalles.size} detalles y ${productos.size} productos")

        binding.txtIdVenta.text = "Pedido #${venta.id}"
        binding.txtFechaPedido.text = "Fecha: ${venta.fecha}"
        binding.txtEstado.text = venta.estado
        binding.txtEstado.setTypeface(null, Typeface.BOLD)

        when (venta.estado) {
            "COMPLETADA" -> binding.txtEstado.setTextColor(Color.parseColor("#357338"))
            "EN PROCESO" -> binding.txtEstado.setTextColor(Color.parseColor("#A66B26"))
            "RECHAZADA" -> binding.txtEstado.setTextColor(Color.parseColor("#852121"))
        }

        val detallesTexto = buildDetallesText(detalles, productos)
        binding.txtDetallePedido.text = detallesTexto

        val total = calcularTotal(detalles, productos)
        binding.txtTotal.text = "Total: %.2f€".format(total)

        setupCancelButton(venta)
    }

    private fun buildDetallesText(detalles: List<DetalleVenta>, productos: List<Producto>): String {
        val builder = StringBuilder().apply {
            append(String.format("%-20s %-10s %-10s\n", "Producto", "Cantidad", "Subtotal"))
            append("----------------------------------------\n")

            detalles.forEach { detalle ->
                productos.find { it.id == detalle.producto_id }?.let { producto ->
                    val nombre = producto.nombre.take(20)
                    val subtotal = producto.precio_con_iva * detalle.cantidad
                    append(String.format("%-20s %-10d %-10.2f€\n", nombre, detalle.cantidad, subtotal))
                }
            }
        }
        return builder.toString()
    }

    private fun calcularTotal(detalles: List<DetalleVenta>, productos: List<Producto>): Double {
        return detalles.sumOf { detalle ->
            productos.find { it.id == detalle.producto_id }?.let {
                it.precio_con_iva * detalle.cantidad
            } ?: 0.0
        }
    }

    private fun setupCancelButton(venta: Venta) {
        binding.btnCancelarPedido.visibility = if (venta.estado == "EN PROCESO") View.VISIBLE else View.GONE

        binding.btnCancelarPedido.setOnClickListener {
            if (venta.estado == "EN PROCESO") {
                showCancelDialog(venta.id)
            } else {
                Toast.makeText(
                    binding.root.context,
                    "No se puede cancelar un pedido ${venta.estado}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCancelDialog(ventaId: Int) {
        val contexto = binding.root.context
        try {
            val actividad = contexto as AppCompatActivity
            val viewModel = PedidosFragmentViewModel()

            AlertDialog.Builder(contexto)
                .setTitle("Confirmar cancelación")
                .setMessage("¿Estás seguro de que deseas cancelar este pedido?")
                .setPositiveButton("Sí") { _, _ ->
                    adapter.eliminarVenta(ventaId)
                    viewModel.cancelarPedido(ventaId, clienteId, contexto)
                    Toast.makeText(contexto, "Pedido cancelado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        } catch (e: Exception) {
            Log.e("PedidoHolder", "Error al cancelar pedido", e)
            Toast.makeText(contexto, "Error al cancelar el pedido", Toast.LENGTH_SHORT).show()
        }
    }
}