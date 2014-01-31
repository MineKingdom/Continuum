package net.minekingdom.continuum.commands;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.BaseCommand.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand {
	
	@SubCommand(name = "set", min = 2, max = 3, permission = "continuum.set")
	public void set(CommandSender sender, String[] args) {
		
		Dimension dim;
		
		if (args.length == 2) {
			
			if (!(sender instanceof Player)) {
				throw new CommandException("You must specify a dimension");
			}
			
			dim = Dimension.get(((Player) sender).getWorld());
			
			if (dim == null) {
				throw new CommandException("The requested dimension is not a valid Continuum dimension.");
			}
			
		} else {
			
			String[] name = args[2].split(":");
			
			Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
			
			if (world == null) {
				throw new CommandException("The world \"" + name[0] + "\" does not exist.");
			}
			
			dim = world.getDimension(name[1]);
			
			if (dim == null) {
				throw new CommandException("The dimension \"" + name[1] + "\" does not exist.");
			}
			
		}
		
		switch (args[0]) {
		
		case "animals":
			dim.setAnimals(Boolean.parseBoolean(args[1]));
			break;
		
		case "difficulty":
			dim.setDifficulty(Difficulty.valueOf(args[1]));
			break;
		
		case "gamemode":
			sender.sendMessage(ChatColor.RED + "This command does not exist yet.");
			break;
		
		case "monsters":
			dim.setMonsters(Boolean.parseBoolean(args[1]));
			break;
		
		case "pvp":
			dim.setPVP(Boolean.parseBoolean(args[1]));
			break;
		
		case "weather":
			sender.sendMessage(ChatColor.RED + "This command does not exist yet.");
			break;
		}
	}
}
