package dam.proyecto.appmovil.vista

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado
import dam.proyecto.appmovil.viewModel.PedidosFragmentViewModel

class PedidoHolder(
    val binding: ItemPedidoBinding,
    private val clienteId: Int,
    private val adapter: PedidoAdapter
) : RecyclerView.ViewHolder(binding.root) {

    fun mostrarPedido(venta: Venta, detalles: List<DetalleVenta>, productos: List<Producto>) {
        Log.d("PedidoHolder", "Mostrando venta ID: ${venta.id} con ${detalles.size} detalles")

        binding.txtIdVenta.text = "Pedido #${venta.id}"
        binding.txtFechaPedido.text = "Fecha: ${venta.fecha}"
        binding.txtEstado.text = venta.estado
        binding.txtEstado.setTypeface(null, Typeface.BOLD)

        when (venta.estado) {
            "COMPLETADA" -> binding.txtEstado.setTextColor(Color.parseColor("#357338"))
            "EN PROCESO" -> binding.txtEstado.setTextColor(Color.parseColor("#A66B26"))
            "RECHAZADA" -> binding.txtEstado.setTextColor(Color.parseColor("#852121"))
        }

        mostrarDetallesFlexibles(binding.txtDetallePedido,detalles, productos)

        val total = calcularTotal(detalles, productos)
        binding.txtTotal.text = "Total: %.2f€".format(total)

        setupCancelButton(venta)
    }

    private fun mostrarDetallesFlexibles(
        container: LinearLayout,
        detalles: List<DetalleVenta>,
        productos: List<Producto>
    ) {
        container.removeAllViews()

        container.addView(crearFilaDetalle("Producto", "Cant.", "Subtotal", true))

        container.addView(View(container.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.dpToPx()
            ).apply {
                setMargins(0, 4.dpToPx(), 0, 4.dpToPx())
            }
            setBackgroundColor(Color.LTGRAY)
        })

        detalles.forEach { detalle ->
            productos.find { it.id == detalle.producto_id }?.let { producto ->
                val subtotal = "%.2f€".format(producto.precio_con_iva * detalle.cantidad)
                container.addView(crearFilaDetalle(
                    producto.nombre,
                    detalle.cantidad.toString(),
                    subtotal,
                    false
                ))
            }
        }
    }

    private fun crearFilaDetalle(producto: String, cantidad: String, subtotal: String, isHeader: Boolean): LinearLayout {
        return LinearLayout(binding.root.context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            weightSum = 100f

            addView(TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    60f
                ).apply {
                    setMargins(4.dpToPx(), 2.dpToPx(), 4.dpToPx(), 2.dpToPx())
                }
                text = producto
                setSingleLine(false)
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                textSize = 12f
                if (isHeader) {
                    setTypeface(typeface, Typeface.BOLD)
                }
            })

            addView(TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    20f
                ).apply {
                    setMargins(4.dpToPx(), 2.dpToPx(), 4.dpToPx(), 2.dpToPx())
                }
                text = cantidad
                gravity = Gravity.CENTER
                textSize = 12f
                if (isHeader) {
                    setTypeface(typeface, Typeface.BOLD)
                }
            })

            addView(TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    20f
                ).apply {
                    setMargins(4.dpToPx(), 2.dpToPx(), 4.dpToPx(), 2.dpToPx())
                }
                text = subtotal
                gravity = Gravity.END
                textSize = 12f
                if (isHeader) {
                    setTypeface(typeface, Typeface.BOLD)
                }
            })
        }
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

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

            }
        }
    }

    private fun showCancelDialog(ventaId: Int) {
        val context = binding.root.context
        try {
            AlertDialog.Builder(context)
                .setTitle("Confirmar cancelación")
                .setMessage("¿Estás seguro de que deseas cancelar este pedido?")
                .setPositiveButton("Sí") { _, _ ->
                    adapter.eliminarVenta(ventaId)
                    (context as? AppCompatActivity)?.let {
                        PedidosFragmentViewModel().cancelarPedido(ventaId, clienteId, context)
                    }
                    mostrarToastPersonalizado(context, "Pedido cancelado", "ok")
                }
                .setNegativeButton("No", null)
                .show()
        } catch (e: Exception) {
            Log.e("PedidoHolder", "Error al cancelar pedido", e)
            Toast.makeText(context, "Error al cancelar el pedido", Toast.LENGTH_SHORT).show()
        }
    }
}