package es.gabirp;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Example rusherhack plugin
 *
 * @author John200410
 */
public class QueueManager extends Plugin {

	private static QueueManager INSTANCE;
	public QueueManagerModule module;
	@Override
	public void onLoad() {
		INSTANCE = this;
		this.getLogger().info(this.getName() + " loaded!");
		
		//creating and registering a new module
		module = new QueueManagerModule();

		RusherHackAPI.getModuleManager().registerFeature(module);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info(this.getName() + " unloaded!");
	}
	
	@Override
	public String getName() {
		return "QueueManager";
	}
	
	@Override
	public String getVersion() {
		return "v1.0";
	}
	
	@Override
	public String getDescription() {
		return "A plugin that lets you see your queue position through a discord bot.";
	}
	
	@Override
	public String[] getAuthors() {
		return new String[]{"GabiRP"};
	}
	public QueueManagerModule getModule(){
		return module;
	}

	public static QueueManager getInstance() {
		return INSTANCE;
	}
}
