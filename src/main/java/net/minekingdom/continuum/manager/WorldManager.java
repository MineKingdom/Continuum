package net.minekingdom.continuum.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minekingdom.continuum.Continuum;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

public class WorldManager {
	
	private Continuum plugin;
	private Map<String, World> worlds;
	
	public WorldManager(Continuum plugin) {
		this.plugin = plugin;
		this.worlds = new HashMap<String, World>();
	}
	
	public World getWorld(String name) {
		return worlds.get(name);
	}
	
	public Collection<World> getWorlds() {
		return worlds.values();
	}
	
	public World createWorld(String name, long seed, Environment env, String generator) {
		WorldCreator creator = new WorldCreator(name);
			creator.environment(env);
			creator.generator(generator);
			creator.seed(seed);
		return createWorld(creator);
	}
	
	public World createWorld(String name, Environment env, String generator) {
		WorldCreator creator = new WorldCreator(name);
			creator.environment(env);
			creator.generator(generator);
		return createWorld(creator);
	}
	
	public World createWorld(String name, Environment env) {
		WorldCreator creator = new WorldCreator(name);
			creator.environment(env);
		return createWorld(creator);
	}
	
	public World createWorld(String name, long seed, Environment env) {
		WorldCreator creator = new WorldCreator(name);
			creator.environment(env);
			creator.seed(seed);
		return createWorld(creator);
	}
	
	public World createWorld(WorldCreator creator) {
		World world = plugin.getServer().createWorld(creator);
		worlds.put(creator.name(), world);
		return world;
	}
	
	public void unloadWorld(String name) {
		World world = getWorld(name);
		plugin.getServer().unloadWorld(world, true);
	}

}
