package dam.proyecto.appmovil.vista

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentLoginBinding
import dam.proyecto.appmovil.modelo.UsuarioLogin
import dam.proyecto.appmovil.viewModel.UserViewModel
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado // ← Importa tu función personalizada
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val usuarioViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inicializarBinding(inflater, container)
        inicializarEventos()
        return binding.root
    }

    private fun inicializarBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
    }

    private fun inicializarEventos() {
        binding.btnRegistrarse.setOnClickListener {
            val navigationController = findNavController()
            val accion = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            navigationController.navigate(accion)
        }

        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.txtCorreo.text.toString()
            val password = binding.txtPassword.text.toString()
            Log.d("LoginDebug", "Password enviada: $password")

            lifecycleScope.launch {
                val usuario = login(email, password, requireContext())
                if (usuario != null) {
                    usuarioViewModel.setUsuario(usuario)
                    val navigationController = findNavController()
                    val accion = LoginFragmentDirections
                        .actionLoginFragmentToHomeFragment(
                            idUsuarioLogin = usuario.id,
                            correoUsuarioLogin = usuario.correo,
                            rolUsuarioLogin = usuario.rol,
                            nombreUsuarioLogin = usuario.nombre,
                            apellidoUsuarioLogin = usuario.apellido
                        )
                    binding.txtCorreo.text.clear()
                    binding.txtPassword.text.clear()
                    navigationController.navigate(accion)
                } else {
                    mostrarToastPersonalizado(requireContext(), "Usuario o contraseña incorrectos", "error")
                }
            }
        }
    }

    suspend fun login(correo: String, password: String, context: Context): UsuarioLogin? {
        return withContext(Dispatchers.IO) {
            try {
                val cliente = OkHttpClient()
                val json = JSONObject().apply {
                    put("correo", correo)
                    put("password", password)
                }
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("http://54.173.46.205/sistema-tienda-api/api/usuarios/autenticar.php")
                    .post(body)
                    .build()

                val response = cliente.newCall(request).execute()
                val respuestaTexto = response.body?.string()

                Log.d("Login", "Respuesta: $respuestaTexto")

                val jsonResponse = JSONObject(respuestaTexto ?: "")

                if (jsonResponse.getBoolean("autenticado")) {
                    val usuario = jsonResponse.getJSONObject("usuario")
                    UsuarioLogin(
                        id = usuario.getInt("id"),
                        correo = usuario.getString("correo"),
                        rol = usuario.getString("rol"),
                        nombre = usuario.getString("nombre"),
                        apellido = usuario.getString("apellido"),
                        telefono = usuario.getString("telefono"),
                        direccion = usuario.getString("direccion"),
                        password = usuario.getString("password_hash")
                    )
                } else {
                    withContext(Dispatchers.Main) {
                        mostrarToastPersonalizado(context, "Usuario o contraseña incorrectos", "error")
                    }
                    null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarToastPersonalizado(context, "Error al iniciar sesión", "error")
                }
                null
            }
        }
    }
}
