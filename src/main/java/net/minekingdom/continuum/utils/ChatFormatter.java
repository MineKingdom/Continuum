package net.minekingdom.continuum.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class ChatFormatter {
	
	private Map<String, String> variables = new HashMap<String, String>();
	
	public void addVariable(String name, Object value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, char value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, byte value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, short value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, int value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, long value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, float value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, double value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, boolean value) {
		variables.put(name, String.valueOf(value));
	}
	
	public void addVariable(String name, String value) {
		variables.put(name, value);
	}
	
	public String format(String str) {
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			str = str.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
		}
		
		for (ChatColor color : ChatColor.values()) {
			str = str.replaceAll("\\{\\{" + color.name() + "\\}\\}", color.toString());
		}
		return str;
	}
}
