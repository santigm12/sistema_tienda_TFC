package dam.proyecto.appmovil.modelo

import okhttp3.*
import okio.ByteString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class WebSocketManager(url: String) {

    private val client = OkHttpClient()
    private val request = Request.Builder().url(url).build()
    private var webSocket: WebSocket? = null

    private val _event = MutableLiveData<String>()
    val event: LiveData<String> get() = _event

    fun connect() {
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _event.postValue("Conectado")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                _event.postValue(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Opcional si usas mensajes binarios
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _event.postValue("Error: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _event.postValue("Desconectado")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Cerrando")
    }
}
