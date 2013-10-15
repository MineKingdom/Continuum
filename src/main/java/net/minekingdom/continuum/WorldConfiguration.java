package net.minekingdom.continuum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minekingdom.continuum.world.ContinuumWorld;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class WorldConfiguration {
	
	private File file;
	private FileConfiguration config;

	public WorldConfiguration(ContinuumWorld world) {
		this.file = new File(Continuum.WORLD_FOLDER + File.separator + world.getName() + ".yml");
		this.config = new YamlConfiguration();
	}
	
	public Configuration getConfig() {
		return this.config;
	}
	
	public void load() {
		try {
			this.config.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			this.config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ConfigurationSection getWorldConfigSection() {
		ConfigurationSection section = config.getConfigurationSection("world");
		if (section == null) {
			return config.createSection("world");
		}
		return section;
	}

	public Map<String, ConfigurationSection> getDimensions() {
		Map<String, ConfigurationSection> dims = new HashMap<String, ConfigurationSection>();
		ConfigurationSection section = getWorldConfigSection();
		for (String key : section.getKeys(false)) {
			ConfigurationSection child = section.getConfigurationSection(key);
			if (child != null) {
				dims.put(key, child);
			}
		}
		return dims;
	}

	public void setDimensions(Map<String, ConfigurationSection> dimensionConfig) {
		config.createSection("world", dimensionConfig);
	}

}
