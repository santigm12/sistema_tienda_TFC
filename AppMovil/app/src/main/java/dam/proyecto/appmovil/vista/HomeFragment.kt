package dam.proyecto.appmovil.vista

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import dam.proyecto.appmovil.MainActivity
import dam.proyecto.appmovil.R
import dam.proyecto.appmovil.databinding.FragmentHomeBinding
import dam.proyecto.appmovil.databinding.FragmentLoginBinding

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
    }

    private fun inicializarBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
    }

    fun inicializarEventos(){
        binding.btnProductos.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductosFragment()
            findNavController().navigate(action)
        }

    }
}