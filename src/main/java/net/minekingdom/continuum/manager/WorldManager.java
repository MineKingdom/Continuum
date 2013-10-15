package net.minekingdom.continuum.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.portal.Portal;
import net.minekingdom.continuum.world.ContinuumWorld;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;

public class WorldManager {
	
	private Continuum plugin;
	private Map<String, ContinuumWorld> worlds;
	private Map<Material, Portal> portalTypes;
	
	public WorldManager(final Continuum plugin) {
		this.plugin = plugin;
		this.worlds = new HashMap<String, ContinuumWorld>();
		this.portalTypes = new HashMap<Material, Portal>();
	}
	
	public ContinuumWorld getWorld(final String name) {
		return worlds.get(name);
	}
	
	public Collection<ContinuumWorld> getWorlds() {
		return worlds.values();
	}
	
	public ContinuumWorld createWorld(final String name) {
		return createWorld0(name);
	}
	
	public ContinuumWorld createWorld(final String name, int seed, String generator, Environment env, WorldType type) {
		return createWorld0(name, new WorldCreator("null").seed(seed).environment(env).type(type), generator);
	}
	
	public ContinuumWorld createWorld(final String name, WorldCreator creator, String generator) {
		return createWorld0(name, creator, generator);
	}
	
	public ContinuumWorld createWorld(final String name, WorldCreator creator) {
		return createWorld0(name, creator, "");
	}
	
	public ContinuumWorld loadWorld(final String name) {
		return createWorld0(name);
	}
	
	private ContinuumWorld createWorld0(final String name) {
		return createWorld0(name, null, null);
	}
	
	private ContinuumWorld createWorld0(final String name, final WorldCreator creator, final String generator) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid world name");
		}
		if (this.worlds.containsKey(name)) {
			throw new IllegalArgumentException("World already exist");
		}
		ContinuumWorld world = new ContinuumWorld(plugin.getServer(), name, creator, generator);
		this.worlds.put(name, world);
		return world;
	}
	
	public void load() {
		loadPortals();
		loadWorlds();
	}
	
	public void reload() {
		loadPortals();
		reloadWorlds();
	}
	
	public void loadPortals() {
		ConfigurationSection portals = plugin.getConfig().getConfigurationSection("nether-portals");
		if (portals == null) {
			portals = plugin.getConfig().createSection("nether-portals");
			List<String> links = Arrays.asList(new String[] {"*:surface", "*:nether"});
			portals.set("obsidian", links);
		}
		
		this.portalTypes.clear();
		for (String key : portals.getKeys(false)) {
			Material frame = Material.matchMaterial(key);
			if (frame == null) {
				plugin.getLogger().warning("Material " + key + " not found in nether portal definition.");
				continue;
			}
			List<String> link = portals.getStringList(key);
			
			if (link == null || link.size() < 2) {
				plugin.getLogger().warning("Invalid destination pattern (must be a string list of 2 elements) for Material " + key + " in nether portal definition.");
				continue;
			}
			
			this.portalTypes.put(frame, new Portal(link.get(0), link.get(1)));
		}
	}
	
	public void loadWorlds() {
		File[] files = Continuum.WORLD_FOLDER.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".yml");
			}
		});
		
		for (File file : files) {
			try {
				createWorld(file.getName().substring(0, file.getName().length() - 4));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	public void reloadWorlds() {
		File[] files = Continuum.WORLD_FOLDER.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".yml");
			}
		});
		
		for (ContinuumWorld world : this.worlds.values()) {
			if (new File(Continuum.WORLD_FOLDER + File.separator + world.getName() + ".yml").exists()) {
				world.load();
			} else {
				this.worlds.remove(world.getName());
			}
		}
		
		for (File file : files) {
			try {
				String name = file.getName().substring(0, file.getName().length() - 4);
				if (!this.worlds.containsKey(name)) {
					createWorld(name);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	public void saveWorlds() {
		for (ContinuumWorld world : this.worlds.values()) {
			world.save();
		}
	}

	public Portal getPortal(final Material frame) {
		return this.portalTypes.get(frame);
	}
}
