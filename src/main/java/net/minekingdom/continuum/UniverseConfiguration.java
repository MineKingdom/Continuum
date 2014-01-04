package net.minekingdom.continuum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minekingdom.continuum.world.Universe;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class UniverseConfiguration {
	
	private File file;
	private FileConfiguration config;

	public UniverseConfiguration(Universe universe) {
		this.file = new File(Continuum.UNIVERSE_FOLDER + File.separator + universe.getName() + ".yml");
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
	
	public ConfigurationSection getDimensionsConfigSection() {
		ConfigurationSection section = config.getConfigurationSection("dimensions");
		if (section == null) {
			return config.createSection("dimensions");
		}
		return section;
	}

	public Map<String, ConfigurationSection> getDimensions() {
		Map<String, ConfigurationSection> dims = new HashMap<String, ConfigurationSection>();
		ConfigurationSection section = getDimensionsConfigSection();
		for (String key : section.getKeys(false)) {
			ConfigurationSection child = section.getConfigurationSection(key);
			if (child != null) {
				dims.put(key, child);
			}
		}
		return dims;
	}

}
