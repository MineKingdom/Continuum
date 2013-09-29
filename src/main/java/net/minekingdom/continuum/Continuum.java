package net.minekingdom.continuum;

import net.minekingdom.continuum.manager.WorldManager;

import org.bukkit.plugin.java.JavaPlugin;

public class Continuum extends JavaPlugin {

	private static Continuum instance;
	private WorldManager worldManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.worldManager = new WorldManager(this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public WorldManager getWorldManager() {
		return this.worldManager;
	}
	
	public static Continuum getInstance() {
		return instance;
	}
}
