package net.minekingdom.continuum.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.portal.Portal;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class WorldManager {
	
	private Continuum plugin;
	private Map<String, Universe> worlds;
	private Map<Material, Map<Material, Portal>> portalTypes;
	
	public WorldManager(final Continuum plugin) {
		this.plugin = plugin;
		this.worlds = new HashMap<String, Universe>();
		this.portalTypes = new HashMap<Material, Map<Material, Portal>>();
	}
	
	public Universe getWorld(final String name) {
		return worlds.get(name);
	}
	
	public Collection<Universe> getWorlds() {
		return worlds.values();
	}
	
	public Universe createWorld(final String name) {
		return createWorld0(name);
	}
	
	public Universe createWorld(final String name, int seed, String generator, Environment env, WorldType type) {
		return createWorld0(name, new WorldCreator("null").seed(seed).environment(env).type(type), generator);
	}
	
	public Universe createWorld(final String name, WorldCreator creator, String generator) {
		return createWorld0(name, creator, generator);
	}
	
	public Universe createWorld(final String name, WorldCreator creator) {
		return createWorld0(name, creator, "");
	}
	
	public Universe loadWorld(final String name) {
		return createWorld0(name);
	}
	
	private Universe createWorld0(final String name) {
		return createWorld0(name, null, null);
	}
	
	private Universe createWorld0(final String name, final WorldCreator creator, final String generator) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid world name");
		}
		if (this.worlds.containsKey(name)) {
			throw new IllegalArgumentException("World already exist");
		}
		Universe world = new Universe(plugin.getServer(), name, creator, generator);
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
	
	public void remove(String universe) {
		 this.worlds.remove(universe);
		}
	
	public void loadPortals() {
		
		List<Map<?, ?>> portals = plugin.getConfig().getMapList("portals");
		if (portals == null || portals.isEmpty()) {
			portals = new LinkedList<Map<?, ?>>();
			
			Map<String, Object> nether = new LinkedHashMap<String, Object>(); 
			{
				List<String> links = Arrays.asList(new String[] {"*:surface", "*:nether"});
				nether.put("frame", Material.OBSIDIAN.name());
				nether.put("portal", Material.PORTAL.name());
				nether.put("link", links);
			}
			portals.add(nether);
			
			Map<String, Object> end = new LinkedHashMap<String, Object>();
			{
				List<String> links = Arrays.asList(new String[] {"*:surface", "*:the_end"});
				end.put("frame", Material.ENDER_PORTAL_FRAME.name());
				end.put("portal", Material.ENDER_PORTAL.name());
				end.put("link", links);
			}
			portals.add(end);
			
			plugin.getConfig().set("portals", portals);
		}
		
		this.portalTypes.clear();
		for (Map<?, ?> portal : portals) {
			try {
				String frameName = portal.get("frame").toString();
				String portalName = portal.get("portal").toString();
				
				Material frame = Material.matchMaterial(frameName);
				if (frame == null) {
					plugin.getLogger().warning("Material " + frameName + " not found in portal definition.");
					continue;
				}
				
				Material portalType = Material.matchMaterial(portalName);
				if (portalType == null) {
					plugin.getLogger().warning("Material " + portalName + " not found in portal definition.");
					continue;
				}
				List<String> link = (List<String>) portal.get("link");
				
				if (link == null || link.size() < 2) {
					plugin.getLogger().warning("Invalid destination pattern (must be a string list of 2 elements) for Material " + frameName + " in portal definition.");
					continue;
				}
				
				Portal p = new Portal(link.get(0), link.get(1));
				Map<Material, Portal> map = this.portalTypes.get(portalType);
				if (map == null) {
					map = new HashMap<Material, Portal>();
					this.portalTypes.put(portalType, map);
				}
				map.put(frame, p);
				
			} catch (Throwable t) {
				plugin.getLogger().warning("Could not get a portal definition in the configuration, please check it does not have any syntax errors.");
			}
		}
	}
	
	public void loadWorlds() {
		File[] files = Continuum.UNIVERSE_FOLDER.listFiles(new FileFilter() {
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
		File[] files = Continuum.UNIVERSE_FOLDER.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".yml");
			}
		});
		
		for (Universe world : this.worlds.values()) {
			if (new File(Continuum.UNIVERSE_FOLDER + File.separator + world.getName() + ".yml").exists()) {
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
		for (Universe world : this.worlds.values()) {
			world.save();
		}
	}

	public Portal getPortal(final Material portal, final Material frame) {
		Map<Material, Portal> map = this.portalTypes.get(portal);
		return map != null ? map.get(frame) : null;
	}
}
