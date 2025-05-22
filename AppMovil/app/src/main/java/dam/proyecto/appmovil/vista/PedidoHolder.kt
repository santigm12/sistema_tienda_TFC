package dam.proyecto.appmovil.vista

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta
import dam.proyecto.appmovil.viewModel.PedidosFragmentViewModel

class PedidoHolder(
    val binding: ItemPedidoBinding,
    private val clienteId: Int,
    private val adapter: PedidoAdapter
) : RecyclerView.ViewHolder(binding.root) {

    fun mostrarPedido(v: Venta, listaD: List<DetalleVenta>, listaP: List<Producto>) {
        binding.txtIdVenta.text = "ID: ${v.id}"
        binding.txtFechaPedido.text = "Pedido realizado: ${v.fecha}"
        binding.txtEstado.text = v.estado
        binding.txtEstado.setTypeface(null, Typeface.BOLD)

        when(v.estado) {
            "COMPLETADA" -> binding.txtEstado.setTextColor(Color.parseColor("#357338"))
            "EN PROCESO" -> binding.txtEstado.setTextColor(Color.parseColor("#A66B26"))
            "RECHAZADA" -> binding.txtEstado.setTextColor(Color.parseColor("#852121"))
        }

        val builder = StringBuilder().apply {
            append(String.format("%-20s %-10s %-10s\n", "Producto", "Cantidad", "Subtotal"))

            listaD.forEach { detalle ->
                listaP.find { it.id == detalle.producto_id }?.let { producto ->
                    val nombre = producto.nombre.take(20)
                    val subtotal = producto.precio_con_iva * detalle.cantidad
                    append(String.format("%-20s %-10d %-10.2f€\n", nombre, detalle.cantidad, subtotal))
                }
            }
        }

        binding.txtDetallePedido.text = builder.toString()
        val total = listaD.sumOf { detalle ->
            listaP.find { it.id == detalle.producto_id }?.let { it.precio_con_iva * detalle.cantidad } ?: 0.0
        }
        binding.txtTotal.text = "Total: %.2f€".format(total)

        binding.btnCancelarPedido.setOnClickListener {
            if (v.estado == "EN PROCESO") {
                val contexto = binding.root.context
                try {
                    val actividad = contexto as AppCompatActivity
                    val viewModel = ViewModelProvider(actividad)
                        .get(PedidosFragmentViewModel::class.java)

                    AlertDialog.Builder(contexto)
                        .setTitle("Confirmar cancelación")
                        .setMessage("¿Estás seguro de que deseas cancelar este pedido?")
                        .setPositiveButton("Sí") { _, _ ->
                            // Eliminar localmente primero
                            adapter.eliminarVenta(v.id)
                            // Enviar solicitud al servidor
                            viewModel.cancelarPedido(v.id, clienteId, contexto)
                        }
                        .setNegativeButton("No", null)
                        .show()
                } catch (e: ClassCastException) {
                    Toast.makeText(contexto, "Error al cancelar el pedido", Toast.LENGTH_SHORT).show()
                    Log.e("CancelarPedido", "Error de contexto", e)
                }
            } else {
                Toast.makeText(binding.root.context,
                    "No se puede cancelar un pedido ${v.estado}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelarPedido.visibility = if (v.estado == "EN PROCESO") View.VISIBLE else View.GONE
    }
}