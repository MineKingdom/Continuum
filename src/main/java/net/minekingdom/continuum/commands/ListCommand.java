package net.minekingdom.continuum.commands;

import java.util.Map;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.BaseCommand.SubCommand;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand {

	@SubCommand(name = "list", permission = "continuum.list", max = 0)
	public void create(CommandSender sender, String[] args, Map<String, String> flags) {
		for (Universe w : Continuum.getInstance().getWorldManager().getWorlds()) {
			if (w.canAccess(sender)) {
				sender.sendMessage(ChatColor.GREEN + w.getName() + ":");
				for (Dimension dim : w.getDimensions()) {
					if (dim.canAccess(sender)) {
						sender.sendMessage(ChatColor.GREEN + "    - " + getColor(dim) + dim.getName() + " (" + dim.getEnvironment().name() + ") - ");
					}
				}
			}
		}
	}

	private ChatColor getColor(Dimension dim) {
		if (dim.isLoaded()) {
			switch (dim.getEnvironment()) {
				case NORMAL:  return ChatColor.AQUA;
				case NETHER:  return ChatColor.RED;
				case THE_END: return ChatColor.LIGHT_PURPLE;
				default: return ChatColor.WHITE;
			}
		} else {
			return ChatColor.GRAY;
		}
	}
	
}
