package dam.proyecto.appmovil.vista

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dam.proyecto.appmovil.MainActivity
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inicializarBinding(inflater, container)
        inicializarEventos()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.supportActionBar?.show()

        val title = view.findViewById<TextView>(R.id.textView19)
        title.text = Html.fromHtml(getString(R.string.titulo_novamarket), Html.FROM_HTML_MODE_LEGACY)

        // Configurar el manejo del botón back
        configurarBackPressedHandler()
    }

    private fun inicializarBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
    }

    private fun inicializarEventos() {
        binding.btnProductos.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductosFragment()
            findNavController().navigate(action)
        }
    }

    private fun configurarBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mostrarDialogoConfirmacionSalida()
                }
            })
    }

    private fun mostrarDialogoConfirmacionSalida() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Salir de la aplicación")
            .setMessage("¿Estás seguro que quieres salir de NovaMarket?")
            .setPositiveButton("Sí") { _, _ ->
                // Cerrar la actividad cuando el usuario confirma
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}