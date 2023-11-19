package es.gabirp.queuemanager.websocket;

import com.google.gson.Gson;
import es.gabirp.queuemanager.QueueManagerClient;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.gabirp.queuemanager.Globals.mc;

public class WebSocketListener implements Listener {

    private Gson gson;
    public WebSocketListener(Gson gson){
        this.gson = gson;
    }
    public List<Runnable> runnables;
    private Boolean responding = false;
    private WebSocket webSocket;

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
        webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.HEARTBEAT, 0, null)), true);
        responding = true;
        this.webSocket = webSocket;
        runnables = new ArrayList<Runnable>();
        setupRunnables();
    }

    @Override
    public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
        WebSocketMessage msg = gson.fromJson(data.toString(), WebSocketMessage.class);

        if(!msg.type.equals(MessageType.HEARTBEAT_RESPONSE))
        {
            WebSocket.Listener.super.onText(socket, data, last);
            return null;
        }

        responding = true;

        WebSocket.Listener.super.onText(socket, data, last);
        return null;
    }
    Boolean isConnected = false;
    Boolean endedQueue = false;
    private void setupRunnables()
    {
        Runnable heartbeatRunnable = () -> {
            try{
                if(responding){
                    responding = false;
                    webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.HEARTBEAT, 0, null)), true);
                }else if(webSocket != null){
                    QueueManagerClient.LOGGER.warn("Websocket server is not responding...");
                }
            }catch(Exception e){
                QueueManagerClient.LOGGER.error(e.toString());
            }
        };
        Runnable queueRunnable = () -> {

            if(mc.world == null || mc.inGameHud.getPlayerListHud() == null || mc.getCurrentServerEntry() == null){
                if(isConnected){
                    webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.SERVERDISCONNECT, 0, null)), true);
                    isConnected = false;
                }
                QueueManagerClient.LOGGER.info("Server or world is null");
                endedQueue = false;
                return;
            }
            if(!mc.getCurrentServerEntry().address.contains("2b2t.org")){
                QueueManagerClient.LOGGER.info("Server is not 2b2t");
                return;
            }
            isConnected = true;
            if(endedQueue) return;
            try{
                Field privateField = mc.inGameHud.getPlayerListHud().getClass().getDeclaredField("field_2153");
                privateField.setAccessible(true);
                Text value = (Text)privateField.get(mc.inGameHud.getPlayerListHud());
                Pattern pattern = Pattern.compile("Position in queue: (\\d+)");
                Matcher match = pattern.matcher(value.getString());


                if(webSocket != null && responding){
                    if(!match.find() || match.group(1) == "1"){
                        webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.ENDQUEUE, 0, null)), true);
                        endedQueue = true;
                        return;
                    }

                    int queuePosition = Integer.parseInt(match.group(1));
                    webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.QUEUEUPDATE, queuePosition, null)), true);
                }else if(webSocket != null && !responding){
                    QueueManagerClient.LOGGER.warn("Websocket server is not responding...");
                }
            }catch(Exception e){
                QueueManagerClient.LOGGER.error(e.toString());
            }
        };
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(heartbeatRunnable, 0, 3, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(queueRunnable, 0, 15, TimeUnit.SECONDS);
    }
    ScheduledExecutorService executor;

    public void Stop(){
        if(executor == null) return;
        executor.shutdown();
        webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.BYE, 0, null)), true);
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Bye");
        webSocket = null;
    }
}
