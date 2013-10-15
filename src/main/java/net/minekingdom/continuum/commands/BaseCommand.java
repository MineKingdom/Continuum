package net.minekingdom.continuum.commands;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minekingdom.continuum.commands.annotated.AnnotatedCommand;
import net.minekingdom.continuum.commands.annotated.SubCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BaseCommand implements CommandExecutor {
	
	private Map<String, AnnotatedCommand> subcommands;
	
	public BaseCommand() {
		this.subcommands = new HashMap<String, AnnotatedCommand>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length >= 1) {
			AnnotatedCommand exec = this.subcommands.get(args[0].toLowerCase());
			if (exec != null) {
				exec.execute(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
				return true;
			}
		}
		return false;
	}
	
	public BaseCommand registerSubCommands(Object o) {
		Class<?> clazz = o.getClass();
		for (Method method : clazz.getDeclaredMethods()) {
			SubCommand annotation = method.getAnnotation(SubCommand.class);
			if (annotation != null) {
				subcommands.put(annotation.name().toLowerCase(), new AnnotatedCommand(annotation, o, method));
			}
		}
		return this;
	}

}
