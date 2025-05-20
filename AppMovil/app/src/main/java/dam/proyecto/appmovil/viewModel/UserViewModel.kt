package dam.proyecto.appmovil.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dam.proyecto.appmovil.modelo.UsuarioLogin


class UserViewModel : ViewModel() {
    private val _usuario = MutableLiveData<UsuarioLogin>()
    val usuario: LiveData<UsuarioLogin> get() = _usuario

    fun setUsuario(usuarioLogin: UsuarioLogin) {
        _usuario.value = usuarioLogin
    }
}
