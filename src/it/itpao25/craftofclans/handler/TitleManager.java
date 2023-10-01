package it.itpao25.craftofclans.handler;

import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.TitleManagerPlugin;
import it.itpao25.craftofclans.util.Color;

public class TitleManager {

	public static TitleManagerPlugin plugin;

	public static void sendFloatingText(Player player, String title, String subtitle) {
		plugin.sendTitlesWithPlaceholders(player, title, subtitle);
	}

	public static void sendActionbarMessage(Player player, String message) {
		plugin.sendActionbarWithPlaceholders(player, message);
	}

	public static String getProgressEx(double actuals, double max) {
		
		int percentuale = (int) ((int) actuals / max * 100);
		StringBuilder finale = new StringBuilder();
		for (int i = 0; i < percentuale; i++) {
			finale.append("&d|");
		}
		for (int i2 = 0; i2 < 100 - percentuale; i2++) {
			finale.append("&f|");
		}
		return Color.translate(finale.toString());

	}
}
