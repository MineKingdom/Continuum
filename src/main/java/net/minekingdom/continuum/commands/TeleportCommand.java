package net.minekingdom.continuum.commands;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.annotated.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand {

	@SubCommand(name = "tp", permission = "continuum.tp", min = 1, max = 2)
	public void teleport(CommandSender sender, String[] args) {
		
		Location loc;
		{
			String[] name = args[0].split(":");
			
			if (name.length >= 2) {
				Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
				if (world == null) {
					throw new CommandException("Unknown world \"" + name[0] + "\".");
				}
				Dimension dimension = world.getDimension(name[1]);
				if (dimension == null) {
					throw new CommandException("Unknown dimension \"" + name[1] + "\" in world " + name[0] + ".");
				}
				loc = dimension.getHandle().getSpawnLocation();
			} else if (name.length == 1) {
				Universe world = Continuum.getInstance().getWorldManager().getWorld(name[0]);
				if (world == null) {
					throw new CommandException("Unknown world \"" + name[0] + "\".");
				}
				loc = world.getSpawn();
			} else {
				throw new CommandException("Could not parse world name \"" + args[0] + "\".");
			}
		}
		
		Player target;
		{
			if (args.length == 2) {
				target = Bukkit.getPlayer(args[1]);
			} else if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				throw new CommandException("Cannot send command as the console.");
			}
		}
		
		if (loc == null) {
			throw new CommandException("Target location is not valid (does the world spawn exist ?)");
		}
		
		if (target == null) {
			throw new CommandException("Unknown player\"" + args[1] + "\".");
		}
		
		target.teleport(loc);
	}

}
