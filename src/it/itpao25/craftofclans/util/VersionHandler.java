package it.itpao25.craftofclans.util;

import org.bukkit.Bukkit;

public class VersionHandler {
	public static boolean is1_6() {
		return Bukkit.getVersion().contains("1.6");
	}

	public static boolean is1_7() {
		return Bukkit.getVersion().contains("1.7");
	}

	public static boolean is1_8() {
		return Bukkit.getVersion().contains("1.8");
	}

	public static boolean is1_9() {
		return Bukkit.getVersion().contains("1.9");
	}
}