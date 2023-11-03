package es.gabirp.queuemanager.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

import static es.gabirp.queuemanager.Globals.mc;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class QueueManagerCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher){
        dispatcher.register(literal("queuemanager")
                .then(literal("force").executes(context -> {
                    try {
                        Field privateField = mc.inGameHud.getPlayerListHud().getClass().getDeclaredField("header");
                        privateField.setAccessible(true);
                        Text value = (Text)privateField.get(mc.inGameHud.getPlayerListHud());
                    } catch (Exception e) {
                        context.getSource().sendFeedback(Text.of("Error"));
                    }

                    return 1;
                }))
                .executes(ctx ->{
                    ctx.getSource().sendFeedback(Text.of("QueueManager on"));
                    return 1;
                }));
    }
}
