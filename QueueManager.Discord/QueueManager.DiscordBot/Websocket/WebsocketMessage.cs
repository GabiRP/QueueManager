namespace QueueManager.DiscordBot.Websocket;

public class WebsocketMessage
{
    public MessageType type { get; set; }

    public string? content { get; set; }

    public int queuePosition { get; set; }

    public WebsocketMessage(MessageType type, int queuePosition, string content)
    {
        this.type = type;
        this.content = content;
        this.queuePosition = queuePosition;
    }
}

public enum MessageType
{
    HEARTBEAT = 0,
    HEARTBEAT_RESPONSE = 1,
    QUEUEUPDATE = 2,
    ENDQUEUE = 3,
    ERROR = 4,
    BYE = 5,
    SERVERDISCONNECT = 6,
}