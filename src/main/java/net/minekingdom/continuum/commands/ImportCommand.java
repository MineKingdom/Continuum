package net.minekingdom.continuum.commands;

import java.io.File;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.BaseCommand.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class ImportCommand {
	
	@SubCommand(name = "import", min = 2, max = 2, permission = "continuum.import")
	public void importWorld(CommandSender sender, String[] args) {
		
		String[] name = args[0].split(":");
		
		if (name.length > 1) {
			
			if (!worldExists(name[0] + "_" + name[1])) {
				throw new CommandException("World " + name[0] + "_" + name[1] + " is not a valid world on disk !");
			}
			
			Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
			
			if (world == null) {
				throw new CommandException("The world \"" + name[0] + "\" does not exist.");
			}
			
			Dimension dim = world.getDimension(name[1]);
			
			if (dim == null) {
				world.addDimension(new Dimension(Bukkit.getServer(), world, name[1], Environment.valueOf(args[1].toUpperCase())));
				sender.sendMessage(ChatColor.GREEN + "Dimension successfully imported !");
			} else {
				throw new CommandException("The dimension \"" + name[1] + "\" already exist.");
			}
			
		} else {
			
			if (!worldExists(name[0])) {
				throw new CommandException("World " + name[0] + " is not a valid world on disk !");
			}
			
			Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
		
			if (world == null) {
				Continuum.getInstance().getWorldManager().createWorld(name[0], WorldCreator.name("random").environment(Environment.valueOf(args[1].toUpperCase())));
				sender.sendMessage(ChatColor.GREEN + "World successfully imported !");
			} else {
				throw new CommandException("The world \"" + name[1] + "\" already exist.");
			}

		}

	}
	
	public boolean worldExists(String name) {		
		File worldDirectory = new File(Bukkit.getWorldContainer() + File.separator + name);
		if (worldDirectory.isDirectory() && worldDirectory.exists()) {
			return new File(worldDirectory + File.separator + "level.dat").exists();
		}
		return false;
	}
}
