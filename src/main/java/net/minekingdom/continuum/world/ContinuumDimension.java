package net.minekingdom.continuum.world;

import java.util.List;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.utils.GenUtils;

import org.bukkit.Difficulty;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ContinuumDimension {
	
	protected final Server server;
	protected final ContinuumWorld world;
	
	protected final String name;
	
	protected World handle;
	
	protected long seed;
	protected String generator;
	protected Environment environment;
	protected WorldType type;
	
	protected double scale;
	
	protected boolean loaded;
	
	protected boolean monsters;
	protected boolean animals;
	
	protected int monsterSpawnLimit;
	protected int waterMobSpawnLimit;
	protected int animalSpawnLimit;
	
	protected boolean pvp;
	protected Difficulty	difficulty;
	
	protected boolean keepSpawnInMemory;
	
	public ContinuumDimension(Server server, ContinuumWorld world, String name, long seed, String generator, Environment environment, WorldType type) {
		this.server = server;
		this.world  = world;
		
		this.name        = name;
		this.seed        = seed;
		this.generator   = generator;
		this.environment = environment;
		this.type        = type;
		
		this.scale = 1;
		
		this.monsters = true;
		this.animals  = true;
		
		this.monsterSpawnLimit  = -1;
		this.animalSpawnLimit   = -1;
		this.waterMobSpawnLimit = -1;

		this.difficulty = Difficulty.NORMAL;
		this.pvp = true;
		
		this.keepSpawnInMemory = false;
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, long seed) {
		this(server, parent, name, seed, "", Environment.NORMAL, WorldType.NORMAL);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, long seed, String generator) {
		this(server, parent, name, seed, generator, Environment.NORMAL, WorldType.NORMAL);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, long seed, Environment environment) {
		this(server, parent, name, seed, "", environment, WorldType.NORMAL);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, long seed, WorldType type) {
		this(server, parent, name, seed, "", Environment.NORMAL, type);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, long seed, String generator, Environment environment) {
		this(server, parent, name, seed, generator, environment, WorldType.NORMAL);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, long seed, String generator, WorldType type) {
		this(server, parent, name, seed, generator, Environment.NORMAL, type);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, String generator) {
		this(server, parent, name, GenUtils.generateSeed(), generator, Environment.NORMAL, WorldType.NORMAL);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, String generator, Environment environment) {
		this(server, parent, name, GenUtils.generateSeed(), generator, environment, WorldType.NORMAL);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, String generator, WorldType type) {
		this(server, parent, name, GenUtils.generateSeed(), generator, Environment.NORMAL, type);
	}

	public ContinuumDimension(Server server, ContinuumWorld parent, String name, String generator, Environment environment, WorldType type) {
		this(server, parent, name, GenUtils.generateSeed(), generator, environment, type);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, Environment environment) {
		this(server, parent, name, GenUtils.generateSeed(), "", environment, WorldType.NORMAL);
	}

	public ContinuumDimension(Server server, ContinuumWorld parent, String name, Environment environment, WorldType type) {
		this(server, parent, name, GenUtils.generateSeed(), "", environment, type);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name, WorldType type) {
		this(server, parent, name, GenUtils.generateSeed(), "", Environment.NORMAL, type);
	}
	
	public ContinuumDimension(Server server, ContinuumWorld parent, String name) {
		this(server, parent, name, GenUtils.generateSeed(), "", Environment.NORMAL, WorldType.NORMAL);
	}
	
	/*-------------------------------------*
	 *            Load functions           *
	 *-------------------------------------*/
	
		public boolean isLoaded() {
			return this.loaded;
		}
		
		public void load() {
			if (isLoaded()) {
				throw new IllegalStateException("World is already loaded.");
			}
			
			this.handle = server.createWorld(getWorldCreator());
			updateHandle();
			
			this.handle.setMetadata("continuum.world", new FixedMetadataValue(Continuum.getInstance(), this.world));
			this.handle.setMetadata("continuum.dimension", new FixedMetadataValue(Continuum.getInstance(), this));
			
			this.loaded = true;
		}
		
		protected WorldCreator getWorldCreator() {
			return new WorldCreator(world.getName() + "_" + name)
				.seed(seed)
				.environment(environment)
				.generator(generator)
				.type(type);
		}
		
		public void unload() {
			unload(true);
		}
	
		public void unload(boolean save) {
			if (!isLoaded()) {
				throw new IllegalStateException("World is already unloaded.");
			}
			this.loaded = false;
			this.server.unloadWorld(this.handle, save);
		}
	
	/*-------------------------------------*
	 *           Update Functions          *
	 *-------------------------------------*/
	
		private void updateHandle() {
			updateSpawnFlags();
			updateMonsterSpawnLimit();
			updateAnimalSpawnLimit();
			updateWaterMobSpawnLimit();
			updateDifficulty();
			updatePVP();
			updateKeepSpawnInMemory();
		}
		
		private void updateSpawnFlags() {
			handle.setSpawnFlags(monsters, animals);
		}
		
		private void updateMonsterSpawnLimit() {
			this.handle.setMonsterSpawnLimit(this.monsterSpawnLimit);
		}
		
		private void updateAnimalSpawnLimit() {
			this.handle.setAnimalSpawnLimit(this.animalSpawnLimit);
		}
		
		private void updateWaterMobSpawnLimit() {
			this.handle.setWaterAnimalSpawnLimit(this.waterMobSpawnLimit);
		}
	
		private void updateDifficulty() {
			this.handle.setDifficulty(difficulty);
		}
	
		private void updatePVP() {
			this.handle.setPVP(pvp);
		}
		
		private void updateKeepSpawnInMemory() {
			this.handle.setKeepSpawnInMemory(this.keepSpawnInMemory);
		}
	
	/*-------------------------------------*
	 *        Mutators / Accessors         *
	 *-------------------------------------*/

		public String getName() {
			return name;
		}
	
		public long getSeed() {
			return seed;
		}
	
		public ContinuumDimension setSeed(long seed) {
			this.seed = seed;
			return this;
		}
	
		public String getGenerator() {
			return generator;
		}
	
		public ContinuumDimension setGenerator(String generator) {
			this.generator = generator;
			return this;
		}
	
		public Environment getEnvironment() {
			return environment;
		}
	
		public ContinuumDimension setEnvironment(Environment environment) {
			this.environment = environment;
			return this;
		}
	
		public WorldType getWorldType() {
			return type;
		}
	
		public ContinuumDimension setWorldType(WorldType worldType) {
			this.type = worldType;
			return this;
		}
	
		public boolean hasMonsters() {
			return monsters;
		}
	
		public ContinuumDimension setMonsters(boolean monsters) {
			this.monsters = monsters;
			if (isLoaded()) {
				updateSpawnFlags();
			}
			return this;
		}
	
		public boolean hasAnimals() {
			return animals;
		}
	
		public ContinuumDimension setAnimals(boolean animals) {
			this.animals = animals;
			if (isLoaded()) {
				updateSpawnFlags();
			}
			return this;
		}
	
		public int getMonsterSpawnLimit() {
			return this.monsterSpawnLimit;
		}
	
		public ContinuumDimension setMonsterSpawnLimit(int monsterSpawnLimit) {
			this.monsterSpawnLimit = monsterSpawnLimit;
			if (isLoaded()) {
				updateMonsterSpawnLimit();
			}
			return this;
		}
	
		public int getAnimalSpawnLimit() {
			return this.animalSpawnLimit;
		}
	
		public ContinuumDimension setAnimalSpawnLimit(int animalSpawnLimit) {
			this.animalSpawnLimit = animalSpawnLimit;
			if (isLoaded()) {
				updateAnimalSpawnLimit();
			}
			return this;
		}
		
		public int getWaterMobSpawnLimit() {
			return this.waterMobSpawnLimit;
		}
	
		public ContinuumDimension setWaterMobSpawnLimit(int waterMobSpawnLimit) {
			this.waterMobSpawnLimit = waterMobSpawnLimit;
			if (isLoaded()) {
				updateWaterMobSpawnLimit();
			}
			return this;
		}
		
		public Difficulty getDifficulty() {
			return difficulty;
		}
	
		public ContinuumDimension setDifficulty(Difficulty difficulty) {
			this.difficulty = difficulty;
			if (isLoaded()) {
				updateDifficulty();
			}
			return this;
		}
	
		public boolean hasPVP() {
			return pvp;
		}
	
		public ContinuumDimension setPVP(boolean pvp) {
			this.pvp = pvp;
			if (isLoaded()) {
				updatePVP();
			}
			return this;
		}
		
		public boolean keepsSpawnInMemory() {
			return keepSpawnInMemory;
		}
		
		public ContinuumDimension setKeepSpawnInMemory(boolean keepSpawnInMemory) {
			this.keepSpawnInMemory = keepSpawnInMemory;
			if (isLoaded()) {
				updateKeepSpawnInMemory();
			}
			return this;
		}
		
		public double getScale() {
			return this.scale;
		}
		
		public ContinuumDimension setScale(double scale) {
			this.scale = scale;
			return this;
		}

		public World getHandle() {
			return handle;
		}
		
		public ContinuumWorld getWorld() {
			return this.world;
		}
		
	/*-------------------------------------*
	 *               Static                *
	 *-------------------------------------*/
		
		public static ContinuumDimension get(World world) {
			if (world != null) {
				List<MetadataValue> meta = world.getMetadata("continuum.dimension");
				if (meta.size() > 0 && meta.get(0).value() instanceof ContinuumDimension) {
					return (ContinuumDimension) meta.get(0).value();
				}
			}
			return null;
		}
}
