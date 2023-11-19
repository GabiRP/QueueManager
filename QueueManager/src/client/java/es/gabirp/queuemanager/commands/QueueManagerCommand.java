package es.gabirp.queuemanager.commands;

import com.mojang.brigadier.CommandDispatcher;
import es.gabirp.queuemanager.websocket.WebSocketClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.net.URI;

import static es.gabirp.queuemanager.Globals.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class QueueManagerCommand {

    private static WebSocketClient client;
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher){
        dispatcher.register(literal("queuemanager")
                .then(literal("start").executes(context -> {
                    try{
                        final URI websocketEndpoint = new URI(endpointUrl);
                        client = new WebSocketClient(websocketEndpoint);
                        isQueueManagerEnabled = true;
                    }catch(Exception e){
                        context.getSource().sendError(Text.of("Error while connecting to the server"));
                        isQueueManagerEnabled = false;
                    }

                    context.getSource().sendFeedback(Text.of("QueueManager enabled"));

                    return 1;
                })).then(literal("stop").executes(context -> {
                    try{
                        client.websocket.Stop();
                        isQueueManagerEnabled = false;
                    }catch (Exception e){
                        context.getSource().sendError(Text.of("Error while disconnecting from the server"));
                    }
                    context.getSource().sendFeedback(Text.of("QueueManager disabled"));
                    return 1;
                }))
                .executes(ctx ->{
                    String enabled = isQueueManagerEnabled ? "on" : "off";
                    ctx.getSource().sendFeedback(Text.of("QueueManager " + enabled));
                    return 1;
                }));
    }
}
