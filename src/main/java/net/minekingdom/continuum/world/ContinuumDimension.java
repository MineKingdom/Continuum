package net.minekingdom.continuum.world;

import java.util.Random;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

public class ContinuumDimension {
	private World world;
	private String name;
	
	private long seed;
	private String generator;
	private Environment environment;
	private WorldType worldType;
	
	private boolean mobGrief;
	
	private boolean monsters;
	private boolean animals;
	private int monsterSpawnLimit;
	private int animalSpawnLimit;
	private int waterMobSpawnLimit;
	
	private int protectionSpawnRadius;
	private Difficulty difficulty;
	private boolean keepSpawnMemory;
	
	private boolean pvp;
	
	public ContinuumDimension(String name, long seed, String generator, Environment environment, WorldType worldType) {
		this.name = name;
		this.seed = seed;
		this.generator = generator;
		this.environment = environment;
		this.worldType = worldType;
		
		this.mobGrief = true;
		
		this.monsters = true;
		this.animals = true;
		this.monsterSpawnLimit = -1;
		this.animalSpawnLimit = -1;
		this.waterMobSpawnLimit = -1;
		
		this.protectionSpawnRadius = 10;
		this.difficulty = Difficulty.NORMAL;
		this.keepSpawnMemory = false;
		
		this.pvp = true;
		
	}	
	
	public ContinuumDimension(String name) {
		this(name, generateSeed(), "", Environment.NORMAL, WorldType.NORMAL);
	}
	
	public static long generateSeed() {
		return new Random().nextLong();
	}
}
