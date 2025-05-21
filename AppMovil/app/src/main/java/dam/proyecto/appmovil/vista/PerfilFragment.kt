package dam.proyecto.appmovil.vista

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentPedidosBinding
import dam.proyecto.appmovil.databinding.FragmentPerfilBinding
import dam.proyecto.appmovil.viewModel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class PerfilFragment : Fragment() {
    private lateinit var binding: FragmentPerfilBinding
    private val usuarioViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        inicializarEventos()
        return binding.root
    }

    suspend fun actualizarUsuario(
        id: Int,
        nombre: String,
        apellido: String,
        telefono: String,
        direccion: String,
        correo: String,  // Añadir correo
        password: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val cliente = OkHttpClient()

            val json = JSONObject().apply {
                put("id", id)
                put("nombre", nombre)
                put("apellido", apellido)
                put("telefono", telefono)
                put("direccion", direccion)
                put("correo", correo)  // Añadir correo
                if (!password.isNullOrBlank()) {
                    put("password", password)
                }
            }

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("http://10.0.2.2/sistema-tienda-api/api/usuarios/actualizar_usuario.php")
                .put(body)  // Cambiar a PUT para coincidir con API
                .build()

            val response = cliente.newCall(request).execute()
            val responseBody = response.body?.string()
            val jsonResponse = JSONObject(responseBody ?: "")

            if (!response.isSuccessful) {
                Log.e("Perfil", "Error en respuesta: ${jsonResponse.optString("mensaje")}")
            }

            return@withContext response.isSuccessful && jsonResponse.getBoolean("actualizado")
        } catch (e: Exception) {
            Log.e("Perfil", "Error al actualizar: ${e.message}")
            return@withContext false
        }
    }

    private fun inicializarEventos() {
        val usuario = usuarioViewModel.usuario.value

        usuario?.let {
            binding.etNombre.setText(it.nombre)
            binding.etApellidos.setText(it.apellido)
            binding.etTelefono.setText(it.telefono)
            binding.etDireccion.setText(it.direccion)
            binding.etCorreo.setText(it.correo)
            binding.etCorreo.isEnabled = false

            binding.etPassword.isEnabled = binding.chkEditarPassword.isChecked
            binding.etPassword.visibility = if (binding.chkEditarPassword.isChecked) View.VISIBLE else View.GONE

            // Evento para el checkbox
            binding.chkEditarPassword.setOnCheckedChangeListener { _, isChecked ->
                binding.etPassword.isEnabled = isChecked
                binding.etPassword.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
        }

        binding.btnActualizarPerfil.setOnClickListener {
            val usuario = usuarioViewModel.usuario.value

            usuario?.let {
                val nombre = binding.etNombre.text.toString()
                val apellido = binding.etApellidos.text.toString()
                val telefono = binding.etTelefono.text.toString()
                val direccion = binding.etDireccion.text.toString()
                val correo = binding.etCorreo.text.toString() // Obtener correo
                val password = if (binding.chkEditarPassword.isChecked) binding.etPassword.text.toString() else null

                lifecycleScope.launch {
                    val actualizado = actualizarUsuario(
                        id = it.id,
                        nombre = nombre,
                        apellido = apellido,
                        telefono = telefono,
                        direccion = direccion,
                        correo = correo, // Añadir correo
                        password = password
                    )

                    if (actualizado) {
                        usuarioViewModel.setUsuario(
                            it.copy(
                                nombre = nombre,
                                apellido = apellido,
                                telefono = telefono,
                                direccion = direccion
                            )
                        )
                        Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
