package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dam.proyecto.appmovil.databinding.FragmentRegisterBinding
import dam.proyecto.appmovil.modelo.mostrarToastPersonalizado
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inicializarBinding(inflater, container)
        inicializarEventos();
        return binding.root
    }

    private fun inicializarBinding(inflater: LayoutInflater,container: ViewGroup?){
        binding = FragmentRegisterBinding.inflate(inflater, container,false)
    }

    private fun inicializarEventos() {
        binding.btnCrearCuenta.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val nombre = binding.txtNombre.text.toString().trim()
        val apellido = binding.txtApellidos.text.toString().trim()
        val telefono = binding.txtTelefono.text.toString().trim()
        val direccion = binding.txtDireccion.text.toString().trim()
        val correo = binding.txtCorreoRegistrarse.text.toString().trim()
        val password = binding.txtPasswordRegistrarse.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() ||
            direccion.isEmpty() || correo.isEmpty() || password.isEmpty()
        ) {
            mostrarToastPersonalizado(requireContext(), "Por favor, rellena todos los campos", "error")
            return
        }

        if (!telefono.matches(Regex("^\\d{9}\$"))) {
            mostrarToastPersonalizado(requireContext(), "El teléfono debe contener exactamente 9 números", "error")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val (correoValido, mensajeValidacion) = withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()
                    val url = "http://54.173.46.205/sistema-tienda-api/api/usuarios/verificar_correo.php?correo=$correo"
                    val request = Request.Builder().url(url).get().build()
                    client.newCall(request).execute().use { response ->
                        val json = JSONObject(response.body?.string() ?: "")
                        val valido = json.getBoolean("valido")
                        val mensaje = json.getString("mensaje")
                        Pair(valido, mensaje)
                    }
                } catch (e: Exception) {
                    Log.e("VerificarCorreo", "Error: ${e.message}")
                    Pair(false, "Error al verificar el correo.")
                }
            }

            if (!correoValido) {
                mostrarToastPersonalizado(requireContext(), mensajeValidacion, "error")
                return@launch
            }

            // Crear el usuario si el correo es válido
            val json = JSONObject().apply {
                put("correo", correo)
                put("password", password)
                put("rol", "cliente")
                put("nombre", nombre)
                put("apellido", apellido)
                put("telefono", telefono)
                put("direccion", direccion)
            }

            try {
                val resultado = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val body = json.toString().toRequestBody(mediaType)
                    val request = Request.Builder()
                        .url("http://54.173.46.205/sistema-tienda-api/api/usuarios/crear.php")
                        .post(body)
                        .build()
                    client.newCall(request).execute().use { it.body?.string() }
                }

                if (isAdded) {
                    mostrarToastPersonalizado(requireContext(), "Cuenta creada correctamente", "ok")
                    limpiarCampos()
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                if (isAdded) {
                    mostrarToastPersonalizado(requireContext(), "Error: ${e.message}", "error")
                }
            }
        }
    }



    private fun limpiarCampos() {
        binding.txtNombre.text.clear()
        binding.txtApellidos.text.clear()
        binding.txtTelefono.text.clear()
        binding.txtDireccion.text.clear()
        binding.txtCorreoRegistrarse.text.clear()
        binding.txtPasswordRegistrarse.text.clear()
    }
}