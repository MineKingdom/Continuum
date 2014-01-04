package net.minekingdom.continuum.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minekingdom.continuum.UniverseConfiguration;
import net.minekingdom.continuum.utils.EnumHelper;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.util.Vector;

public class Universe {
	
	public final static String ACCESS_PERMISSION_PREFIX = "continuum.access.";
	public final static Permission ALL_ACCESS_PERMISSION = new Permission(ACCESS_PERMISSION_PREFIX + "*");
	
	static {
		Bukkit.getServer().getPluginManager().addPermission(ALL_ACCESS_PERMISSION);
	}
	
	public final Permission ACCESS_PERMISSION;
	
	private Server	server;
	
	private String name;
	
	private Vector spawn;
	private Dimension spawnDimension;
	
	private Map<String, Dimension> dimensions;
	
	private UniverseConfiguration config;
	
	
	public Universe(Server server, String name) {
		this(server, name, null, null);
	}
	
	public Universe(Server server, String name, WorldCreator creator, String generator) {
		this.server = server;
		this.name = name;
		this.dimensions = new HashMap<String, Dimension>();
		this.config = new UniverseConfiguration(this);
		
		this.ACCESS_PERMISSION = new Permission(ACCESS_PERMISSION_PREFIX + name);
		this.ACCESS_PERMISSION.addParent(ALL_ACCESS_PERMISSION, true);
		server.getPluginManager().addPermission(ACCESS_PERMISSION);
		server.getPluginManager().recalculatePermissionDefaults(ALL_ACCESS_PERMISSION);
		
		load(creator, generator);
	}
	
	public void load() {
		load(null, null);
	}

	public void load(WorldCreator creator, String gen) {
		config.load();
		
		for (Dimension dim : this.dimensions.values()) {
			dim.unload();
		}
		
		this.dimensions.clear();
		
		Map<String, ConfigurationSection> dimensionConfig = config.getDimensions();
		for (Map.Entry<String, ConfigurationSection> dim : dimensionConfig.entrySet()) {
			
			try {
				ConfigurationSection dc = dim.getValue();
					long seed = dc.getLong("seed", new Random().nextLong());
					String generator = dc.getString("generator", "default");
					Environment environment = Environment.valueOf(dc.getString("environment", "normal").toUpperCase());
					
					String rawType = dc.getString("world-type", "normal").toUpperCase();
					WorldType type = WorldType.valueOf(rawType);
					/*try {
						type = WorldType.valueOf(rawType);
					} catch (Throwable t) {
						type = (WorldType) EnumHelper.addEnum(WorldType.class, rawType, new Class[]{String.class}, new Object[]{rawType});
					}*/
				
				Dimension cd;
				if (dim.getKey().equals("surface")) {
					cd = new DefaultDimension(
							this.server, 
							this,
							seed, 
							generator, 
							environment == null ? Environment.NORMAL : environment, 
							type == null ? WorldType.NORMAL : type);
				} else {
					cd = new Dimension(
							this.server, 
							this, 
							dim.getKey(), 
							seed, 
							generator, 
							environment == null ? Environment.NORMAL : environment, 
							type == null ? WorldType.NORMAL : type);
				}
				
				
				ConfigurationSection props = dc.getConfigurationSection("properties");
				if (props != null) {
					Difficulty diff = Difficulty.valueOf(props.getString("difficulty", "normal").toUpperCase());
					
					cd.setDifficulty			(diff == null ? Difficulty.NORMAL : diff)
					  .setAnimals           	(props.getBoolean("has-animals", true))
					  .setMonsters          	(props.getBoolean("has-monsters", true))
					  .setAnimalSpawnLimit  	(props.getInt("animal-spawn-limit", -1))
					  .setWaterMobSpawnLimit	(props.getInt("water-mob-spawn-limit", -1))
					  .setMonsterSpawnLimit 	(props.getInt("monster-spawn-limit", -1))
					  .setPVP               	(props.getBoolean("pvp-enabled", true))
					  .setKeepSpawnInMemory 	(props.getBoolean("keep-spawn-in-memory", false))
					  .setScale					(props.getDouble("scale", 1));
				}
				
				this.addDimension(cd);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		if (!this.dimensions.containsKey("surface")) {
			if (creator != null && gen != null) {
				this.addDimension(new DefaultDimension(this.server, this, creator.seed(), gen, creator.environment(), creator.type()));
			} else {
				this.addDimension(new DefaultDimension(this.server, this));
			}
		}
		
		Vector spawnVector = config.getConfig().getVector("spawn-location");
		String dimensionName = config.getConfig().getString("spawn-dimension", "surface");
		
		Dimension spawnDimension = this.dimensions.get(dimensionName);
		if (spawnDimension == null) {
			spawnDimension = this.dimensions.get("surface");
		}
		
		if (spawnVector == null) {
			spawnVector = spawnDimension.getHandle().getSpawnLocation().toVector();
		}
		
		
		this.spawn = spawnVector;
		this.spawnDimension = spawnDimension;
	}

	public void save() {
		
		config.getConfig().set("spawn-location", this.spawn);
		config.getConfig().set("spawn-dimension", this.spawnDimension.getName());
		
		ConfigurationSection universe = config.getDimensionsConfigSection();
		for (Dimension dim : dimensions.values()) {
			ConfigurationSection section = universe.createSection(dim.getName());
				section.set("seed", dim.getSeed());
				section.set("generator", dim.getGenerator());
				section.set("environment", dim.getEnvironment().toString());
				section.set("world-type", dim.getWorldType().toString());
			
			ConfigurationSection props = section.createSection("properties");
				props.set("difficulty", dim.getDifficulty().toString());
				props.set("has-animals", dim.hasAnimals());
				props.set("has-monsters", dim.hasMonsters());
				props.set("animal-spawn-limit", dim.getAnimalSpawnLimit());
				props.set("water-mob-spawn-limit", dim.getWaterMobSpawnLimit());
				props.set("monster-spawn-limit", dim.getMonsterSpawnLimit());
				props.set("pvp-enabled", dim.hasPVP());
				props.set("keep-spawn-in-memory", dim.keepsSpawnInMemory());
				props.set("scale", dim.getScale());
		}
		
		config.save();
	}
	
	public Dimension getDimension(String name) {
		return this.dimensions.get(name);
	}
	
	public Location getSpawn() {
		return new Location(this.spawnDimension.getHandle(), this.spawn.getX(), this.spawn.getY(), this.spawn.getZ());
	}

	public Dimension getSpawnDimension() {
		return spawnDimension;
	}
	
	public String getName() {
		return name;
	}
	
	public Universe addDimension(Dimension dimension) {
		this.dimensions.put(dimension.getName(), dimension);
		if (!dimension.isLoaded()) {
			dimension.load();
		}
		return this;
	}
	
	public Universe removeDimension(Dimension dimension) {
		this.dimensions.remove(dimension);
		if (dimension.isLoaded()) {
			dimension.unload();
		}
		return this;
	}

	public Collection<Dimension> getDimensions() {
		return this.dimensions.values();
	}
	
	public boolean canAccess(Permissible permissible) {
		return permissible.hasPermission(ACCESS_PERMISSION);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Universe ? ((Universe) o).getName().equals(this.name) : false;
	}
	
	public static class DefaultDimension extends Dimension {

		public DefaultDimension(Server server, Universe parent) {
			super(server, parent, "surface");
			this.keepSpawnInMemory = true;
		}
		
		public DefaultDimension(Server server, Universe universe, long seed, String generator, Environment environment, WorldType type) {
			super(server, universe, "surface", seed, generator, environment, type);
			this.keepSpawnInMemory = true;
		}

		@Override
		protected WorldCreator getWorldCreator() {
			return new WorldCreator(universe.getName())
				.seed(seed)
				.environment(environment)
				.generator(generator)
				.type(type);
		}
		
	}
	
	public static Universe get(World world) {
		if (world != null) {
			List<MetadataValue> meta = world.getMetadata("continuum.universe");
			if (meta.size() > 0 && meta.get(0).value() instanceof Universe) {
				return (Universe) meta.get(0).value();
			}
		}
		return null;
	}
}
