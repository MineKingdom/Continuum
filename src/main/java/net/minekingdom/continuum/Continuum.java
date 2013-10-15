package net.minekingdom.continuum;

import java.io.File;

import net.minekingdom.continuum.commands.BaseCommand;
import net.minekingdom.continuum.commands.CreateCommand;
import net.minekingdom.continuum.commands.ListCommand;
import net.minekingdom.continuum.commands.TeleportCommand;
import net.minekingdom.continuum.manager.WorldManager;

import org.bukkit.plugin.java.JavaPlugin;

public class Continuum extends JavaPlugin {

	public final static File SERVER_FOLDER = new File(System.getProperty("user.dir"));
	public final static File DATA_FOLDER = new File(SERVER_FOLDER + File.separator + "data" + File.separator + "continuum");
	public final static File PLAYER_FOLDER = new File(DATA_FOLDER + File.separator + "players");
	
	public static File WORLD_FOLDER;
	
	private static Continuum instance;
	private WorldManager worldManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		setupFilesystem();
		setupManagers();
		setupCommands();
		setupListeners();
	}
	
	private void setupListeners() {
		this.getServer().getPluginManager().registerEvents(new ContinuumListener(this), this);
	}

	private void setupCommands() {
		BaseCommand cmd = new BaseCommand()
				.registerSubCommands(new TeleportCommand())
				.registerSubCommands(new CreateCommand())
				.registerSubCommands(new ListCommand());
		
		this.getCommand("ctm").setExecutor(cmd);
	}

	private void setupManagers() {
		this.worldManager = new WorldManager(this);
		this.worldManager.load();
	}

	private void setupFilesystem() {
		WORLD_FOLDER = new File(getDataFolder() + File.separator + "worlds");
		WORLD_FOLDER.mkdirs();
	}

	@Override
	public void onDisable() {
		this.worldManager.saveWorlds();
		this.saveConfig();
	}
	
	public WorldManager getWorldManager() {
		return this.worldManager;
	}
	
	public static Continuum getInstance() {
		return instance;
	}
}
