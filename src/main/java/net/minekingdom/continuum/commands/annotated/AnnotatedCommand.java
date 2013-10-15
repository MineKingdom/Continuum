package net.minekingdom.continuum.commands.annotated;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class AnnotatedCommand {
	
	private Object	target;
	private Method	method;
	private String	permission;
	private String	usage;
	private String	description;
	
	private int min;
	private int max;
	
	private HashSet<String> flags;

	public AnnotatedCommand(SubCommand cmd, Object target, Method method) {
		this.target = target;
		this.method = method;
		this.permission = cmd.permission();
		this.usage = cmd.usage();
		this.description = cmd.description();
		this.min = cmd.min();
		this.max = cmd.max();
		this.flags = new HashSet<String>();
		for (String flag : cmd.flags()) {
			this.flags.add(flag);
		}
	}

	public void execute(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (!"".equals(permission) && sender.hasPermission(permission)) {
				List<String> newargs = new LinkedList<String>();
				Map<String, String> flags = new HashMap<String, String>();
				for (int i = 0; i < args.length; ++i) {
					if (args[i].startsWith("--")) {
						int delim = args[i].indexOf(':');
						String name;
						String val;
						if (delim == -1) {
							name = args[i].substring(2);
							val = null;
						} else {
							name = args[i].substring(2, delim);
							val = args[i].substring(delim + 1, args[i].length());
						}
						
						if (!this.flags.contains(name)) {
							throw new CommandException("Invalid flag \"" + name + "\".");
						}
						
						flags.put(name, val);
					} else {
						newargs.add(args[i]);
					}
				}
				
				if (newargs.size() < this.min || this.max != -1 && newargs.size() > this.max) {
					throw new CommandException("Invalid number of arguments.");
				}
				
				method.setAccessible(true);
				Class<?>[] argTypes = method.getParameterTypes();
				if (Map.class.isAssignableFrom(argTypes[argTypes.length - 1])) {
					method.invoke(target, sender, newargs.toArray(new String[0]), flags);
				} else {
					method.invoke(target, sender, newargs.toArray(new String[0]));
				}
			} else {
				throw new CommandException("You need the permission " + permission + " to execute this command.");
			}
		} catch (CommandException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		} catch (Throwable t) {
			sender.sendMessage(ChatColor.RED + "An error occured while running this command.");
			t.printStackTrace();
		}
	}
	
	public String getPermission() {
		return permission;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public String getDescription() {
		return description;
	}

}
