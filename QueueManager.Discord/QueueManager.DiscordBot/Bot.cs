using System.Reflection;
using Discord;
using Discord.Addons.Hosting;
using Discord.Interactions;
using Discord.WebSocket;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
// ReSharper disable TemplateIsNotCompileTimeConstantProblem

namespace QueueManager.DiscordBot;

public class Bot : DiscordClientService
{
    private readonly IServiceProvider _provider;
    private readonly InteractionService _interactionService;
    private readonly Config _config;
    public static IDMChannel? _dm;
    public static DiscordSocketClient _Client;
    public Bot(DiscordSocketClient client, ILogger<Bot> logger, Config config, InteractionService interactionService, IServiceProvider services) : base(client, logger)
    {
        _provider = services;
        _interactionService = interactionService;
        _config = config;
    }
    
    
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        Logger.LogInformation("Starting the bot!");

        await _interactionService.AddModulesAsync(Assembly.GetExecutingAssembly(), _provider);
        
        Client.Ready += ClientOnReady;
        Client.Log += ClientOnLog;
        Client.InteractionCreated += ClientOnInteractionCreated;
        _Client = Client;

        await Task.Delay(TimeSpan.FromSeconds(5), stoppingToken);
        
        var user = Client.GetGuild(901954023224139796/*CHANGE THIS*/).GetUser(507955112027750401/*CHANGE THIS*/);
        _dm = await user.CreateDMChannelAsync();

        if (_dm == null)
        {
            Logger.LogError("DM Channel is NULL");
            return;
        }
        
        await Task.Delay(-1, stoppingToken);
    }

    private async Task ClientOnReady()
    {
        try
        {
            foreach (var command in _interactionService.SlashCommands)
            {
                Logger.LogInformation($"\t- /{command.Name}");
            }
            await _interactionService.RegisterCommandsGloballyAsync();
        }
        catch (Exception e)
        {
            Logger.LogError(e, e.Message);
            throw;
        }
    }
    
    private async Task ClientOnInteractionCreated(SocketInteraction arg)
    {
        try
        {
            SocketInteractionContext ctx = new(Client, arg);
            await _interactionService.ExecuteCommandAsync(ctx, _provider);
        }
        catch (Exception e)
        {
            Serilog.Log.Error(e, "Executing interaction failed");
        }
    }
    
    private Task ClientOnLog(LogMessage arg)
    {
        Serilog.Log.Information(arg.Message);
        return Task.CompletedTask;
    }
}