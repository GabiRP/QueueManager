package es.gabirp.queuemanager;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import es.gabirp.queuemanager.commands.QueueManagerCommand;
import es.gabirp.queuemanager.websocket.WebSocketClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.WebSocket;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static es.gabirp.queuemanager.Globals.mc;

public class QueueManagerClient implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("queuemanager");
	@Override
	public void onInitializeClient() {

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientCommandRegistrationCallback.EVENT.register(QueueManagerClient::registerCommands);

	}


	private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registry){
		QueueManagerCommand.register(dispatcher);
	}
}