package net.minekingdom.continuum.commands;

import java.util.Map;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.BaseCommand.SubCommand;

import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;

public class CreateCommand {

	@SubCommand(name = "create", permission = "continuum.create", min = 1, flags = {"seed", "env", "type", "generator"})
	public void create(CommandSender sender, String[] args, Map<String, String> flags) {
		String name = args[0];
		
		if (flags.isEmpty()) {
			WorldCreator creator = new WorldCreator("");
			String rawSeed = flags.get("seed"), 
					rawEnv = flags.get("env"), 
					rawType = flags.get("type"),
					generator = flags.get("generator");
			
			if (rawSeed != null) {
				int seed;
				try { 
					seed = Integer.parseInt(rawSeed);
				} catch (Exception ex) {
					seed = rawSeed.hashCode();
				}
				creator.seed(seed);
			}
			if (rawEnv != null) {
				Environment env = Environment.valueOf(rawEnv.toUpperCase());
				if (env != null) {
					creator.environment(env);
				}
			}
			if (rawType != null) {
				WorldType type = WorldType.valueOf(rawType.toUpperCase());
				if (type != null) {
					creator.type(type);
				}
			}
			if (generator != null) {
				Continuum.getInstance().getWorldManager().createWorld(name, creator, generator);
			} else {
				Continuum.getInstance().getWorldManager().createWorld(name, creator);
			}
		} else {
			Continuum.getInstance().getWorldManager().createWorld(name);
		}
	}
	
}
