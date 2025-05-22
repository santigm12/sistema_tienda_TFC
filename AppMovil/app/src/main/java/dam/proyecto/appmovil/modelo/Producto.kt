package dam.proyecto.appmovil.modelo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class Producto(
    val id : Int,
    val nombre: String,
    val precio_con_iva: Double,
    val descripcion: String,
    val imagenB64: String?
): Parcelable

