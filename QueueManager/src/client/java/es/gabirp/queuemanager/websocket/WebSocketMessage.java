package es.gabirp.queuemanager.websocket;

import org.jetbrains.annotations.Nullable;

public class WebSocketMessage {
    public MessageType type;

    @Nullable
    public String content;

    public int queuePosition;

    public WebSocketMessage(MessageType type, int queuePosition, @Nullable String content)
    {
        this.type = type;
        this.content = content;
        this.queuePosition = queuePosition;
    }
}


