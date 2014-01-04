package net.minekingdom.continuum.utils;

import java.io.File;
import java.io.IOException;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.nms.ItemStackConfig;

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
			if (contents[i] == null) {
				continue;
			}
			inventory.set(String.valueOf(i), new ItemStackConfig(contents[i]));
		}
		
		for (int i = 0; i < armorContents.length; ++i) {
			if (armorContents[i] == null) {
				continue;
			}
			armor.set(String.valueOf(i), new ItemStackConfig(armorContents[i]));
		}
		
		for (int i = 0; i < enderChestContents.length; ++i) {
			if (enderChestContents[i] == null) {
				continue;
			}
			enderChest.set(String.valueOf(i), new ItemStackConfig(enderChestContents[i]));
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
		if (player == null || world == null) {
			return;
		}
		
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
		ItemStackConfig[] contentsConfig = new ItemStackConfig[contents.length];
		for (int i = 0; i < contents.length; ++i) {
			contentsConfig[i] = getItemStackConfig(inventory, String.valueOf(i), null);
			if (contentsConfig[i] != null) {
				contents[i] = contentsConfig[i].toItemStack();
			}
		}
		
		ItemStack[] armorContents = new ItemStack[4];
		ItemStackConfig[] armorContentsConfig = new ItemStackConfig[armorContents.length];
		for (int i = 0; i < armorContents.length; ++i) {
			armorContentsConfig[i] = getItemStackConfig(armor, String.valueOf(i), null);
			if (armorContentsConfig[i] != null) {
				armorContents[i] = armorContentsConfig[i].toItemStack();
			}
		}
		
		ItemStack[] enderChestContents = new ItemStack[player.getEnderChest().getSize()];
		ItemStackConfig[] enderChestContentsConfig = new ItemStackConfig[enderChestContents.length];
		for (int i = 0; i < enderChestContents.length; ++i) {
			enderChestContentsConfig[i] = getItemStackConfig(enderChest, String.valueOf(i), null);
			if (enderChestContentsConfig[i] != null) {
				enderChestContents[i] = enderChestContentsConfig[i].toItemStack();
			}
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
			player.setCompassTarget(new Location(world, 0, 0, Double.NEGATIVE_INFINITY));
		}
		
		player.setLevel(config.getInt("level", 0));
		player.setExp((float) config.getDouble("exp", 0));
	}
	
	private static ItemStack getItemStack(ConfigurationSection section, String key, ItemStack def) {
		Object val = section.get(key, def);
		return (val instanceof ItemStackConfig) ? ((ItemStackConfig) val).toItemStack() : def;
	}
	
	private static ItemStackConfig getItemStackConfig(ConfigurationSection section, String key, ItemStackConfig def) {
		Object val = section.get(key, def);
		return (val instanceof ItemStackConfig) ? (ItemStackConfig) val : def;
	}
	
}
