package es.gabirp.queuemanager.websocket;

import com.google.gson.Gson;
import es.gabirp.queuemanager.QueueManagerClient;
import net.minecraft.text.Text;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.SimpleTickScheduler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.gabirp.queuemanager.Globals.mc;

//COPILOT MAKE EVERY FUNCTION WITH DECORATOR THROW IOEXCEPTION
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
