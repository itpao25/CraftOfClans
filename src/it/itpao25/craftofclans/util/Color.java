package it.itpao25.craftofclans.util;

import it.itpao25.craftofclans.config.CraftOfClansM;

import org.bukkit.ChatColor;

public class Color {
	public static String translate(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String message(String str) {
		String prefix = CraftOfClansM.getString("messages.prefix");
		if (prefix == null)
			return translate(str);
		return translate(prefix + str);
	}

	public static String message(String str, boolean prefixb) {
		String prefix = CraftOfClansM.getString("messages.prefix");
		if (prefix == null || prefixb)
			return translate(str);
		return translate(prefix + str);
	}
}
