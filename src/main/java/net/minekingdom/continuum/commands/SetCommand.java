package net.minekingdom.continuum.commands;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.annotated.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class SetCommand {
	
	@SubCommand(name = "set", min = 2, max = 3, permission = "continuum.set")
	public void set(CommandSender sender, String[] args) {
		
		if (args[2] == null) {
			throw new CommandException("No destination specified !");
		}
		
		String[] name = args[2].split(":");
		
		Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
		
		if (world == null) {
			throw new CommandException("The world \"" + name[0] + "\" does not exist.");
		}
		
		Dimension dim = world.getDimension(name[1]);
		
		if (dim == null) {
			throw new CommandException("The dimension \"" + name[1] + "\" does not exist.");
		}
		
		switch (args[0]) {
			
			case "animals":
				dim.setAnimals(Boolean.parseBoolean(args[1]));
				break;
			
			case "difficulty":
				dim.setDifficulty(Difficulty.valueOf(args[1]));
				break;
			
			case "gamemode":
				sender.sendMessage(ChatColor.RED + "This command does not already exist.");
				break;
			
			case "monsters":
				dim.setMonsters(Boolean.parseBoolean(args[1]));
				break;
			
			case "pvp":
				dim.setPVP(Boolean.parseBoolean(args[1]));
				break;
			
			case "weather":
				sender.sendMessage(ChatColor.RED + "This command does not already exist.");
				break;
		}
	}
}
