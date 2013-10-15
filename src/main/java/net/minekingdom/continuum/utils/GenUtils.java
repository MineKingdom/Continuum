package net.minekingdom.continuum.utils;

import java.util.Random;

public final class GenUtils {
	private GenUtils() {}
	
	private final static Random random = new Random();

	public static long generateSeed() {
		return random.nextLong();
	}
}
