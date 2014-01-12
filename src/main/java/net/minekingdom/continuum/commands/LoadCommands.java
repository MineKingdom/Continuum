package net.minekingdom.continuum.commands;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.BaseCommand.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class LoadCommands {
	
	@SubCommand(name = "load", min = 1, max = 1, permission = "continuum.load")
	public void load(CommandSender sender, String[] args) {
		
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
			
			dim.load();
			
		} else {
			Universe world = Continuum.getInstance().getWorldManager().getWorld(args[0]);

			if (world == null) {
				throw new CommandException("The world \"" + args[0] + "\" does not exist.");
			}

			world.load();
		}
		

	}
	
	@SubCommand(name = "unload", min = 1, max = 1, permission = "continuum.unload")
	public void unload(CommandSender sender, String[] args) {
		
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
			
			dim.unload();
			
		} else {
			Universe world = Continuum.getInstance().getWorldManager().getWorld(args[0]);

			if (world == null) {
				throw new CommandException("The world \"" + args[0] + "\" does not exist.");
			}
			
			world.unload();
		}
	}
}
