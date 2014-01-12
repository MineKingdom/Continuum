package net.minekingdom.continuum.commands;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.annotated.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class DeleteCommands {
	
	@SubCommand(name = "remove", min = 1, max = 1, permission = "continuum.remove")
	public void remove(CommandSender sender, String[] args) {
		
		String[] name = args[0].split(":");
		
		if (name.length > 1) {
			Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
		
			if (world == null) {
				throw new CommandException("The world \"" + name[0] + "\" does not exist.");
			}
		
			Dimension dim = world.getDimension(name[1]);
		
			if (dim == null) {
				throw new CommandException("The dimension \"" + name[1] + "\" does not exist.");
			}
		
			world.removeDimension(dim.getName());
		} else {
			Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
			
			if (world == null) {
				throw new CommandException("The world \"" + name[0] + "\" does not exist.");
			}
			
			sender.sendMessage(ChatColor.RED + "This command does not already exist.");
		}
	}
}
