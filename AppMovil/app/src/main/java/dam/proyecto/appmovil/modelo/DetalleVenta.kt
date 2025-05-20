package dam.proyecto.appmovil.modelo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetalleVenta (
    val venta_id:Int,
    val producto_id:Int,
    val cantidad: Int,
)