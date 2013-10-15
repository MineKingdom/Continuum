package net.minekingdom.continuum.portal;

import net.minekingdom.continuum.Continuum;
import net.minekingdom.continuum.world.ContinuumDimension;
import net.minekingdom.continuum.world.ContinuumWorld;

import org.bukkit.World;

public class Portal {

	private String destinationPattern1;
	private String destinationPattern2;
	
	public Portal(String destinationPattern1, String destinationPattern2) {
		this.destinationPattern1 = destinationPattern1;
		this.destinationPattern2 = destinationPattern2;
	}
	
	public ContinuumDimension getFirstWorld(ContinuumWorld world) {
		return getDimension(destinationPattern1, world);
	}
	
	public ContinuumDimension getSecondWorld(ContinuumWorld world) {
		return getDimension(destinationPattern2, world);
	}
	
	public ContinuumDimension getOtherWorld(ContinuumWorld world, World first) {
		ContinuumDimension dim = ContinuumDimension.get(first);
		if (matches(destinationPattern1, world, dim)) {
			return getSecondWorld(world);
		} else if (matches(destinationPattern2, world, dim)) {
			return getFirstWorld(world);
		}
		return null;
	}
	
	public static boolean matches(String pattern, ContinuumWorld world, ContinuumDimension dim) {
		String[] split = pattern.replaceFirst("\\*", world.getName()).split(":");
		if (split.length >= 2) {
			ContinuumWorld w = Continuum.getInstance().getWorldManager().getWorld(split[0]);
			if (w != null && w.equals(world)) {
				ContinuumDimension d = w.getDimension(split[1]);
				if (d != null && d.equals(dim)) {
					return true;
				}
			}
		}
		return false;
	}

	public static ContinuumDimension getDimension(String pattern, ContinuumWorld world) {
		String[] split = pattern.replaceFirst("\\*", world.getName()).split(":");
		if (split.length >= 2) {
			ContinuumWorld w = Continuum.getInstance().getWorldManager().getWorld(split[0]);
			if (w != null) {
				return w.getDimension(split[1]);
			}
		}
		return null;
	}
}
