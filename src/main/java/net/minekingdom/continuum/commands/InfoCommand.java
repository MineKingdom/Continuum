package net.minekingdom.continuum.commands;

import java.util.Map;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.commands.annotated.SubCommand;
import net.minekingdom.continuum.utils.ChatFormatter;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand {
	
	private static String DIM_INFO = "{{GREEN}}" +
			"-------- {{GOLD}}Continuum Info{{GREEN}} ----------\n" +
			"  {{GOLD}}World{{GREEN}} : {worldName},\n" +
			"  {{YELLOW}}Dimension{{GREEN}} : {dimName} ({loaded}),\n" +
			"      {{AQUA}}Seed{{GREEN}} : {seed}\n" +
			"      {{AQUA}}Environment{{GREEN}} : {environment}\n" +
			"      {{AQUA}}World Type{{GREEN}} : {worldType}\n" +
			"      {{AQUA}}Generator{{GREEN}} : {generator}\n" +
			"      {{AQUA}}Difficulty{{GREEN}} : {difficulty}\n" +
			"      {{AQUA}}Scale{{GREEN}} : {scale}\n" +
			"  {{GOLD}}Limits{{GREEN}} : \n" +
			"      {{AQUA}}Animals{{GREEN}} : {animalLimit}\n" +
			"      {{AQUA}}Monsters{{GREEN}} : {monsterLimit}\n" +
			"      {{AQUA}}Water Mobs{{GREEN}} : {waterMobLimit}\n";
	
	private static String WORLD_INFO = "{{GREEN}}" +
			"-------- {{GOLD}}Continuum Info{{GREEN}} ----------\n" +
			"  {{GOLD}}World{{GREEN}} : {worldName},\n" +
			"  {{YELLOW}}Dimensions{{GREEN}} : {dimensions} \n" +
			"  {{GOLD}}Spawn{{GREEN}} : {spawnLoc} ({spawnDim})\n";

	@SubCommand(name = "info", permission = "continuum.info", min = 0, max = 1)
	public void info(CommandSender sender, String[] args, Map<String, String> flags) {
		
		Dimension dim;
		if (args.length == 0) {
		
			if (!(sender instanceof Player)) {
				throw new CommandException("Sender must be a player.");
			}
			
			dim = Dimension.get(((Player) sender).getWorld());
		} else {
			int delim = args[0].indexOf(':');
			String worldName;
			String dimName = null;
			if (delim == -1) {
				worldName = args[0];
			} else {
				worldName = args[0].substring(0, delim);
				dimName = args[0].substring(delim + 1, args[0].length());
			}
			Universe world = Continuum.getInstance().getWorldManager().getWorld(worldName);
			if (world == null || !world.canAccess(sender)) {
				throw new CommandException("\"" + worldName + "\" is not a valid continuum world.");
			}
			
			if (dimName == null) {
				infoWorld(sender, world);
				return;
			} else {
				dim = world.getDimension(dimName);
			}
		}
		
		if (dim == null || !dim.canAccess(sender)) {
			throw new CommandException("The requested dimension is not a valid continuum dimension.");
		}
		
		infoDim(sender, dim);
	}

	private void infoDim(CommandSender sender, Dimension dim) {
		ChatFormatter formatter = new ChatFormatter();
			formatter.addVariable("worldName", dim.getWorld().getName());
			formatter.addVariable("dimName", dim.getName());
			formatter.addVariable("loaded", dim.isLoaded() ? "LOADED" : "{{GRAY}}UNLOADED{{GREEN}}");
			formatter.addVariable("seed", dim.getSeed());
			formatter.addVariable("worldType", dim.getWorldType());
			formatter.addVariable("environment", dim.getEnvironment());
			formatter.addVariable("generator", dim.getGenerator());
			formatter.addVariable("difficulty", dim.getDifficulty());
			formatter.addVariable("scale", dim.getScale());
			formatter.addVariable("animalLimit", dim.getAnimalSpawnLimit());
			formatter.addVariable("monsterLimit", dim.getMonsterSpawnLimit());
			formatter.addVariable("waterMobLimit", dim.getWaterMobSpawnLimit());
			
		sender.sendMessage(formatter.format(DIM_INFO));
	}

	private void infoWorld(CommandSender sender, Universe world) {
		String dims = "";
		for (Dimension dim : world.getDimensions()) {
			dims += dim.getName() + " ";
		}
		
		ChatFormatter formatter = new ChatFormatter();
			formatter.addVariable("worldName", world.getName());
			formatter.addVariable("dimensions", dims);
			formatter.addVariable("spawnLoc", world.getSpawn().getX() + ", " + world.getSpawn().getY() + ", " + world.getSpawn().getZ());
			formatter.addVariable("spawnDim", world.getSpawnDimension().getName());
			
		sender.sendMessage(formatter.format(WORLD_INFO));
	}
	
}
