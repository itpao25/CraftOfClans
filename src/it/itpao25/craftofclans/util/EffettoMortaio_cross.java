package it.itpao25.craftofclans.util;

public class EffettoMortaio_cross {
	public static boolean is18() {
		boolean result = false;
		try {
			Class.forName("org.bukkit.Particle");
			result = true;
		} catch (ClassNotFoundException e) {
		}
		return result;
	}
}
