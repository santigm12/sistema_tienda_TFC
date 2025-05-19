package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt


class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inicializarBinding(inflater, container)
        inicializarEventos()
        return binding.root
    }




    private fun inicializarBinding(inflater: LayoutInflater,container: ViewGroup?){
        binding = FragmentLoginBinding.inflate(inflater, container,false)
    }

    private fun inicializarEventos(){
        binding.btnRegistrarse.setOnClickListener{
            val navigationController = findNavController()
            val accion = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            navigationController.navigate(accion)
        }

        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.txtCorreo.text.toString()
            val password = binding.txtPassword.text.toString()
            Log.d("LoginDebug", "Password enviada: ${binding.txtPassword.text}")

            lifecycleScope.launch {
                val exito = login(email, password)
                if (exito) {
                    val navigationController = findNavController()
                    val accion = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    navigationController.navigate(accion)
                } else {
                    Toast.makeText(requireContext(), "El usuario o la contraseña son incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    suspend fun login(correo: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val cliente = OkHttpClient()
                val json = JSONObject().apply {
                    put("correo", correo)
                    put("password", password)
                }
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("http://10.0.2.2/sistema-tienda-api/api/usuarios/autenticar.php")
                    .post(body)
                    .build()

                val response = cliente.newCall(request).execute()
                val respuestaTexto = response.body?.string()

                Log.d("Login", "Respuesta: $respuestaTexto")

                respuestaTexto?.contains("exito", ignoreCase = true) == true
            } catch (e: Exception) {
                Log.e("Login", "Error en login: ${e.message}")
                false
            }
        }
    }
}