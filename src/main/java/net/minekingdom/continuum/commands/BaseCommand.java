package net.minekingdom.continuum.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
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
	
	public static class AnnotatedCommand {
		
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
								throw new IllegalCommandFlagException("Invalid flag \"" + name + "\".");
							}
							
							flags.put(name, val);
						} else {
							newargs.add(args[i]);
						}
					}
					
					if (newargs.size() < this.min || this.max != -1 && newargs.size() > this.max) {
						throw new IllegalCommandArgumentException("Invalid number of arguments.");
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
			} catch (IllegalCommandArgumentException ex) {
				sender.sendMessage(ChatColor.RED + ex.getMessage());
				sender.sendMessage(ChatColor.RED + "usage : " + usage);
			} catch (CommandException ex) {
				sender.sendMessage(ChatColor.RED + ex.getMessage());
			} catch (InvocationTargetException ex) {
				Throwable tex = ex.getCause();
				if (tex instanceof CommandException) {
					sender.sendMessage(ChatColor.RED + tex.getMessage());
				} else if (tex instanceof IllegalCommandArgumentException) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
					sender.sendMessage(ChatColor.RED + "usage : " + usage);
				} else {
					sender.sendMessage(ChatColor.RED + "An error occured while running this command.");
					tex.printStackTrace();
				}
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
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface SubCommand {
		public String name();
		public String description() default "";
		public String usage() default "";
		public String permission() default "";
		public int min() default 0;
		public int max() default -1;
		public String[] flags() default {};
	}
	
	public static class IllegalCommandArgumentException extends Exception {
		public IllegalCommandArgumentException(String message) {
			super(message);
		}
	}
	
	public static class IllegalCommandFlagException extends Exception {
		public IllegalCommandFlagException(String message) {
			super(message);
		}
	}

}
