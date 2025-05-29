package dam.proyecto.appmovil.modelo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import dam.proyecto.appmovil.R


fun mostrarToastPersonalizado(context: Context, mensaje: String, tipo: String) {
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.toast_custom, null)

    val textView = layout.findViewById<TextView>(R.id.toast_text)
    val imageView = layout.findViewById<ImageView>(R.id.toast_icon)

    textView.text = mensaje

    val iconoResId = when (tipo.lowercase()) {
        "error" -> R.drawable.cruz
        "ok" -> R.drawable.check
        else -> R.drawable.logo
    }

    imageView.setImageResource(iconoResId)

    val fondo = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 32f
        setColor(Color.parseColor("#888888"))
    }
    layout.background = fondo

    with(Toast(context)) {
        duration = Toast.LENGTH_LONG
        view = layout
        setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
        show()
    }
}


