package es.gabirp.websocket;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;

public class WebSocketClient {
    private URI endpointURI;
    public WebSocketListener websocket;
    public WebSocketClient(URI endpointURI) {
        gson = new Gson();
        client = HttpClient.newHttpClient();
        websocket = new WebSocketListener(gson);
        client.newWebSocketBuilder().buildAsync(endpointURI, websocket);
    }

    private HttpClient client;
    private Gson gson = new Gson();
}
