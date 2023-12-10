package es.gabirp.websocket;

import com.google.gson.Gson;
import es.gabirp.QueueManager;
import net.minecraft.network.chat.Component;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.core.notification.NotificationType;

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

import static es.gabirp.Globals.*;
import static org.rusherhack.client.api.Globals.mc;

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
                    RusherHackAPI.getNotificationManager().send(NotificationType.WARNING, "Websocket server is not responding...");
                }
            }catch(Exception e){
                QueueManager.getInstance().getLogger().error(e.toString());
                RusherHackAPI.getNotificationManager().send(NotificationType.ERROR, "Error sending heartbeat");
            }
        };
        Runnable queueRunnable = () -> {
            if(mc.getCurrentServer() == null || mc.gui.getTabList() == null){
                if(isConnected){
                    webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.SERVERDISCONNECT, 0, null)), true);
                    isConnected = false;
                }
                QueueManager.getInstance().getLogger().info("Server or tablist is null");
                endedQueue = false;
                return;
            }
            if(!mc.getCurrentServer().ip.contains("2b2t.org")){
                QueueManager.getInstance().getLogger().info("Server is not 2b2t");
                return;
            }
            isConnected = true;
            if(endedQueue) return;
            try{
                /*
                // Used this to find the field name for the header
                Field[] a = mc.gui.getTabList().getClass().getDeclaredFields();
                for (Field field : a) {
                    field.setAccessible(true);
                    if(field.getType() == Component.class) {
                        RusherHackAPI.getNotificationManager().send(NotificationType.INFO, field.getName() + " " + ((Component)field.get(mc.gui.getTabList())).getString());
                    }
                }*/

                Field privateField = mc.gui.getTabList().getClass().getDeclaredField("field_2153");
                privateField.setAccessible(true);
                Component value = (Component)privateField.get(mc.gui.getTabList());
                Pattern pattern = Pattern.compile("Position in queue: (\\d+)");
                Matcher match = pattern.matcher(value.getString());


                if(webSocket != null && responding){
                    if(!match.find() || match.group(1) == "1"){
                        webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.ENDQUEUE, 0, null)), true);
                        endedQueue = true;
                        QueueManager.getInstance().getModule().setToggled(false);
                        RusherHackAPI.getNotificationManager().send(NotificationType.INFO, "Queue ended, the module has been automatically disabled and you have been disconnected from the websocket.");
                        return;
                    }

                    int queuePosition = Integer.parseInt(match.group(1));
                    webSocket.sendText(gson.toJson(new WebSocketMessage(MessageType.QUEUEUPDATE, queuePosition, null)), true);
                }else if(webSocket != null && !responding){
                    QueueManager.getInstance().getLogger().warn("Websocket server is not responding...");
                }
            }catch(Exception e){
                QueueManager.getInstance().getLogger().error(e.toString());
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
