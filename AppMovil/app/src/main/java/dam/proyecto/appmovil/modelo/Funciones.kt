package dam.proyecto.appmovil.modelo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dam.proyecto.appmovil.R

fun Fragment.navegarAErrorFragment(mensaje: String) {
    val bundle = Bundle().apply {
        putString("mensajeError", mensaje)
    }
    findNavController().navigate(R.id.errorFragment, bundle)
}