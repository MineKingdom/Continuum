package net.minekingdom.continuum.portal;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.World;

public class Portal {

	private String destinationPattern1;
	private String destinationPattern2;
	
	public Portal(String destinationPattern1, String destinationPattern2) {
		this.destinationPattern1 = destinationPattern1;
		this.destinationPattern2 = destinationPattern2;
	}
	
	public Dimension getFirstWorld(Universe world) {
		return getDimension(destinationPattern1, world);
	}
	
	public Dimension getSecondWorld(Universe world) {
		return getDimension(destinationPattern2, world);
	}
	
	public Dimension getOtherWorld(Universe world, World first) {
		Dimension dim = Dimension.get(first);
		if (matches(destinationPattern1, world, dim)) {
			return getSecondWorld(world);
		} else if (matches(destinationPattern2, world, dim)) {
			return getFirstWorld(world);
		}
		return null;
	}
	
	public static boolean matches(String pattern, Universe world, Dimension dim) {
		String[] split = pattern.replaceFirst("\\*", world.getName()).split(":");
		if (split.length >= 2) {
			Universe w = Continuum.getInstance().getWorldManager().getWorld(split[0]);
			if (w != null && w.equals(world)) {
				Dimension d = w.getDimension(split[1]);
				if (d != null && d.equals(dim)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Dimension getDimension(String pattern, Universe world) {
		String[] split = pattern.replaceFirst("\\*", world.getName()).split(":");
		if (split.length >= 2) {
			Universe w = Continuum.getInstance().getWorldManager().getWorld(split[0]);
			if (w != null) {
				return w.getDimension(split[1]);
			}
		}
		return null;
	}
}
