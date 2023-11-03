using System.Net;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using WebSocketSharp.Server;

namespace QueueManager.DiscordBot.Websocket;

public class WebsocketServer : IHostedService
{
    private WebSocketServer wssv;
    private readonly ILogger<WebsocketServer> _logger;
    public WebsocketServer(ILogger<WebsocketServer> logger)
    {
        _logger = logger;
    }
    public Task StartAsync(CancellationToken cancellationToken)
    {
        wssv = new WebSocketServer(IPAddress.Loopback, 8080);

        //wssv.AddWebSocketService<QueueBehavior>("/queue");
        wssv.AddWebSocketService<QueueBehavior>("/queue");
        
        wssv.Start();

        if (wssv.IsListening) {
            _logger.LogInformation("Listening on  {Address}:{Port}, and providing WebSocket services:",wssv.Address.ToString(), wssv.Port);

            foreach (var path in wssv.WebSocketServices.Paths)
                Console.WriteLine ("- {0}", path);
        }
        return Task.CompletedTask;
    }
    
    public Task StopAsync(CancellationToken cancellationToken)
    {
        wssv.Stop();
        return Task.CompletedTask;
    }
}