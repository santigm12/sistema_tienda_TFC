package dam.proyecto.appmovil.modelo

import androidx.lifecycle.ViewModel

data class UsuarioLogin(
    val id:Int,
    val correo:String,
    val password:String,
    val rol:String,
    val nombre:String,
    val apellido:String,
    val direccion:String,
    val telefono:String
)
