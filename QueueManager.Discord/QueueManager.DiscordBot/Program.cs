// See https://aka.ms/new-console-template for more information

using System.Reflection.Metadata.Ecma335;
using System.Runtime.CompilerServices;
using Discord;
using Discord.Addons.Hosting;
using Discord.WebSocket;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using QueueManager.DiscordBot;
using QueueManager.DiscordBot.Websocket;
using Serilog;
using Serilog.Events;

Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Information()
                .MinimumLevel.Override("Microsoft", LogEventLevel.Error)
                .WriteTo.Console()
                .CreateLogger();

Config? _config;

if (!File.Exists("config.json"))
{
    _config = new Config();
    File.WriteAllText("config.json", JsonConvert.SerializeObject(new Config()));
}
_config = JsonConvert.DeserializeObject<Config>(File.ReadAllText("config.json"));

if (_config == null)
{
    Log.Fatal("Config is null");
    Environment.Exit(1);
}

IHost host = Host.CreateDefaultBuilder(args)
                .UseSerilog()
                .ConfigureDiscordHost((context, config) =>
                {
                    config.SocketConfig = new DiscordSocketConfig()
                    {
                        MessageCacheSize = 1000,
                        GatewayIntents = GatewayIntents.All,
                        LogLevel = LogSeverity.Info,
                        UseInteractionSnowflakeDate = false,
                    };
                    config.LogFormat = (message, exception) => $"{message.Source}: {message.Message}";
                    config.Token = _config.Token;
                })
                .UseInteractionService((context, config) =>
                {
                    config.LogLevel = LogSeverity.Info;
                    config.UseCompiledLambda = false;
                })
                .ConfigureServices((context, services) =>
                {
                    services.AddSingleton(_config);

                    services.AddHostedService<WebsocketServer>();
                    services.AddSingleton<WebsocketServer>();
                    services.AddHostedService<Bot>();
                    services.AddSingleton<Bot>();
                }).Build();

await host.RunAsync();