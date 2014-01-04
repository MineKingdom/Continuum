package net.minekingdom.continuum;

import java.io.File;

import net.minekingdom.continuum.commands.BaseCommand;
import net.minekingdom.continuum.commands.CreateCommand;
import net.minekingdom.continuum.commands.InfoCommand;
import net.minekingdom.continuum.commands.ListCommand;
import net.minekingdom.continuum.commands.TeleportCommand;
import net.minekingdom.continuum.manager.WorldManager;

import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;

public class Continuum extends JavaPlugin {

	public final static File SERVER_FOLDER = new File(System.getProperty("user.dir"));
	public final static File DATA_FOLDER = new File(SERVER_FOLDER + File.separator + "data" + File.separator + "continuum");
	public final static File PLAYER_FOLDER = new File(DATA_FOLDER + File.separator + "players");
	
	public static File UNIVERSE_FOLDER;
	
	private static Continuum instance;
	private WorldManager worldManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		displayInfos();
		
		setupFilesystem();
		setupManagers();
		setupCommands();
		setupListeners();
	}
	
	private void displayInfos() {
		String envs = "";
		for (Environment env : Environment.values()) {
			envs += env.name() + ", ";
		}
		envs = envs.substring(0, envs.length() - 2);
		
		String wts = "";
		for (WorldType type : WorldType.values()) {
			wts += type.name() + "(" + type.getName() + "), ";
		}
		wts = wts.substring(0, wts.length() - 2);
		
		this.getLogger().info("Detected environments : " + envs);
		this.getLogger().info("Detected world types : " + wts);
	}

	private void setupListeners() {
		this.getServer().getPluginManager().registerEvents(new ContinuumListener(this), this);
	}

	private void setupCommands() {
		BaseCommand cmd = new BaseCommand()
				.registerSubCommands(new TeleportCommand())
				.registerSubCommands(new CreateCommand())
				.registerSubCommands(new ListCommand())
				.registerSubCommands(new InfoCommand());
		
		this.getCommand("ctm").setExecutor(cmd);
	}

	private void setupManagers() {
		this.worldManager = new WorldManager(this);
		this.worldManager.load();
	}

	private void setupFilesystem() {
		UNIVERSE_FOLDER = new File(getDataFolder() + File.separator + "worlds");
		UNIVERSE_FOLDER.mkdirs();
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
