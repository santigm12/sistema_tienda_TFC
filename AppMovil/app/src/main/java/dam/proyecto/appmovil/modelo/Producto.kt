package dam.proyecto.appmovil.modelo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Producto(
    val id : Int,
    val nombre: String,
    val precio_con_iva: Double,
    val descripcion: String,
    val imagenB64: String?
)

