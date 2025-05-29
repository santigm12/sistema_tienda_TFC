package com.proyectobase.modelo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import org.java_websocket.drafts.Draft;

public class WSClient extends WebSocketClient {

    private final Consumer<String> mensajeCallback;

    public WSClient (String serverUri, Consumer<String> mensajeCallback) throws URISyntaxException {
        super(new URI(serverUri));
        this.mensajeCallback = mensajeCallback;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Conectado al WebSocket de ventas.");
    }

    @Override
    public void onMessage(String message) {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String tipo = json.get("tipo").getAsString();

        if ("ventas_actualizadas".equals(tipo)) {
            mensajeCallback.accept("ventas_actualizadas");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Desconectado del WebSocket: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
