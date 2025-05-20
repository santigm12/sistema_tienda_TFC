package dam.proyecto.appmovil.vista

import androidx.recyclerview.widget.RecyclerView
import dam.proyecto.appmovil.databinding.ItemPedidoBinding
import dam.proyecto.appmovil.modelo.DetalleVenta
import dam.proyecto.appmovil.modelo.Producto
import dam.proyecto.appmovil.modelo.Venta

class PedidoHolder(val binding:ItemPedidoBinding) :RecyclerView.ViewHolder(binding.root) {
    lateinit var lstProducto: List<Producto>
    lateinit var venta: Venta
    lateinit var lstDetalleVenta: List<DetalleVenta>

    fun mostrarPedido(v: Venta, listaD: List<DetalleVenta>, listaP: List<Producto>){
        venta = v
        lstProducto = listaP
        lstDetalleVenta = listaD

        binding.txtFechaPedido.text = v.fecha
        binding.txtEstado.text = v.estado
        var texto:String = "";
        var totalPedido = 0.0;
        for (i in 0 until lstDetalleVenta.size) {
            for (j in 0 until lstProducto.size) {
                if(lstDetalleVenta.get(i).producto_id == lstProducto.get(j).id){
                    texto+=lstProducto.get(j).nombre+" "+lstDetalleVenta.get(i).cantidad+" "+lstProducto.get(j).precio_con_iva*lstDetalleVenta.get(i).cantidad+"\n"
                    totalPedido+=lstProducto.get(j).precio_con_iva*lstDetalleVenta.get(i).cantidad
                }
            }
        }

        binding.txtDetallePedido.text = texto
        binding.txtTotal.text = totalPedido.toString()

    }
}