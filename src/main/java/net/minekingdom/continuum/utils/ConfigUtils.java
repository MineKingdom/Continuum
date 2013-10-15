package net.minekingdom.continuum.utils;

import java.io.File;
import java.io.IOException;

import net.minekingdom.continuum.Continuum;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ConfigUtils {
	
	public static void savePlayer(final Player player, final World world) {
		
		File dir = new File(Continuum.PLAYER_FOLDER + File.separator + world.getName());
			dir.mkdirs();
		
		final FileConfiguration config = new YamlConfiguration();
		
		ConfigurationSection inventory = config.createSection("inventory");
		ConfigurationSection armor = config.createSection("armor");
		ConfigurationSection enderChest = config.createSection("ender-chest");
		
		ItemStack[] contents = player.getInventory().getContents();
		ItemStack[] armorContents = player.getInventory().getArmorContents();
		ItemStack[] enderChestContents = player.getEnderChest().getContents();
		
		for (int i = 0; i < contents.length; ++i) {
			inventory.set(String.valueOf(i), contents[i]);
		}
		
		for (int i = 0; i < armorContents.length; ++i) {
			armor.set(String.valueOf(i), armorContents[i]);
		}
		
		for (int i = 0; i < enderChestContents.length; ++i) {
			enderChest.set(String.valueOf(i), enderChestContents[i]);
		}
		
		config.set("hunger", player.getFoodLevel());
		config.set("max-health", (int) player.getMaxHealth());
		config.set("health", (int) player.getHealth());
		config.set("bed-spawn", player.getBedSpawnLocation() == null ? null : player.getBedSpawnLocation().toVector());
		config.set("level", player.getLevel());
		config.set("exp", (double) player.getExp());
		
		try {
			config.save(new File(dir + File.separator + player.getName() + ".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadPlayer(final Player player, final World world) {
		
		File dir = new File(Continuum.PLAYER_FOLDER + File.separator + world.getName());
			dir.mkdirs();
		
		final Configuration config = YamlConfiguration.loadConfiguration(new File(dir + File.separator + player.getName() + ".yml"));
		
		ConfigurationSection inventory = config.getConfigurationSection("inventory");
		ConfigurationSection armor = config.getConfigurationSection("armor");
		ConfigurationSection enderChest = config.getConfigurationSection("ender-chest");
		
		if (inventory == null) {
			inventory = config.createSection("inventory");
		}
		if (armor == null) {
			armor = config.createSection("armor");
		}
		if (enderChest == null) {
			enderChest = config.createSection("ender-chest");
		}
		
		ItemStack[] contents = new ItemStack[9 * 4];
		for (int i = 0; i < contents.length; ++i) {
			contents[i] = inventory.getItemStack(String.valueOf(i), null);
		}
		
		ItemStack[] armorContents = new ItemStack[4];
		for (int i = 0; i < armorContents.length; ++i) {
			armorContents[i] = armor.getItemStack(String.valueOf(i), null);
		}
		
		ItemStack[] enderChestContents = new ItemStack[player.getEnderChest().getSize()];
		for (int i = 0; i < enderChestContents.length; ++i) {
			enderChestContents[i] = enderChest.getItemStack(String.valueOf(i), null);
		}
		
		try {
			player.getInventory().setContents(contents);
			player.getInventory().setArmorContents(armorContents);
			player.getEnderChest().setContents(enderChestContents);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		player.setFoodLevel(config.getInt("hunger"));
		
		player.setMaxHealth(config.getInt("max-health", 20));
		player.setHealth(Math.min(player.getMaxHealth(), config.getInt("health", 20)));
		
		Vector bedVector = config.getVector("bed-spawn", null);
		if (bedVector != null) {
			Location bedSpawn = new Location(world, bedVector.getX(), bedVector.getY(), bedVector.getZ());
			player.setBedSpawnLocation(bedSpawn, true);
			player.setCompassTarget(bedSpawn);
		} else {
			player.setBedSpawnLocation(null, true);
			player.setCompassTarget(new Location(world, 0, 0, -Double.MAX_VALUE));
		}
		
		player.setLevel(config.getInt("level", 0));
		player.setExp((float) config.getDouble("exp", 0));
	}
	
	
}
