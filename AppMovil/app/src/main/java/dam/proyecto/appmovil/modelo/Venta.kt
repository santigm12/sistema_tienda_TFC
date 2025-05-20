package dam.proyecto.appmovil.modelo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Venta(
    val id:Int,
    val fecha:String,
    val total:Double,
    //val metodo_pago: String?,
    val estado:String
)
