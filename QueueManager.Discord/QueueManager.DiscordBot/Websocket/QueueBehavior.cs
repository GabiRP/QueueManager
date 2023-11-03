using Discord.WebSocket;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using WebSocketSharp;
using WebSocketSharp.Server;
using ErrorEventArgs = WebSocketSharp.ErrorEventArgs;

namespace QueueManager.DiscordBot.Websocket;

public class QueueBehavior : WebSocketBehavior
{
    private int lastQueuePosition = -1;
    
    protected override void OnMessage(MessageEventArgs ev)
    {
        WebsocketMessage? msg = JsonConvert.DeserializeObject<WebsocketMessage>(ev.Data);
        if(msg == null) return;
        switch (msg.type)
        {
            case MessageType.HEARTBEAT:
                Serilog.Log.Information("Received heartbeat. Sending response...");
                Send(JsonConvert.SerializeObject(new WebsocketMessage(MessageType.HEARTBEAT_RESPONSE, 0, "")));
                break;
            case MessageType.ERROR:
                Serilog.Log.Error("Error occured: {Content}", msg.content);
                break;
            case MessageType.QUEUEUPDATE:
                Serilog.Log.Information("NEW QUEUE POSITION: {QueuePosition}", msg.queuePosition);
                if (lastQueuePosition != msg.queuePosition)
                    Bot._Client.SetGameAsync(msg.queuePosition.ToString());
                if(msg.queuePosition <= 2)
                    Bot._dm?.SendMessageAsync("You are next or almost next in queue! Please be ready to join the game!");
                lastQueuePosition = msg.queuePosition;
                break;
            case MessageType.ENDQUEUE:
                Serilog.Log.Information("QUEUE POSSIBLY ENDED");
                Bot._dm?.SendMessageAsync("QUEUE HAS PROBABLY ENDED!");
                break;
            case MessageType.BYE:
                Serilog.Log.Information("CLIENT DISCONNECTED");
                break;
            case MessageType.SERVERDISCONNECT:
                Bot._dm?.SendMessageAsync("Server disconnected! Please rejoin the queue!");
                Bot._Client.SetActivityAsync(null);
                break;
        }
    }

    protected override void OnError(ErrorEventArgs e)
    {
        
    }
    protected override void OnClose(CloseEventArgs e)
    {
        string reason = e.Reason;
        if (e.Code == 1006)
        {
            reason += "**THIS SHOULD BE NORMAL**";
        }
        Serilog.Log.Information("Client disconnected. {Code} - {Reason}", e.Code, reason);
        Bot._Client.SetActivityAsync(null);
    }
}