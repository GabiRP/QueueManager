package es.gabirp;

import es.gabirp.websocket.WebSocketClient;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.events.render.EventRender2D;
import org.rusherhack.client.api.events.render.EventRender3D;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.IRenderer3D;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.setting.BindSetting;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.client.api.utils.WorldUtils;
import org.rusherhack.core.bind.key.NullKey;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.notification.NotificationType;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.core.setting.StringSetting;
import org.rusherhack.core.utils.ColorUtils;

import static es.gabirp.Globals.*;

import java.awt.*;
import java.net.URI;

/**
 * Example rusherhack module
 *
 * @author John200410
 */
public class QueueManagerModule extends ToggleableModule {

	private static WebSocketClient client;

	public QueueManagerModule() {
		super("QueueManager", "2b2t Queue status through discord bot", ModuleCategory.CLIENT);
	}

	@Override
	public void onEnable() {

		try{
			final URI websocketEndpoint = new URI(endpointUrl);
			client = new WebSocketClient(websocketEndpoint);
			isQueueManagerEnabled = true;
		}catch(Exception e){
			RusherHackAPI.getNotificationManager().send(NotificationType.ERROR, "QueueManager: error creating websocket client");
			isQueueManagerEnabled = false;
		}
		RusherHackAPI.getNotificationManager().send(NotificationType.INFO, "QueueManager: enabled");
	}
	
	@Override
	public void onDisable() {
		try{
			client.websocket.Stop();
			isQueueManagerEnabled = false;
		}catch (Exception e){
			RusherHackAPI.getNotificationManager().send(NotificationType.ERROR, "QueueManager: error disconnecting websocket client");
		}
		RusherHackAPI.getNotificationManager().send(NotificationType.INFO, "QueueManager: disconnected");
	}
}
