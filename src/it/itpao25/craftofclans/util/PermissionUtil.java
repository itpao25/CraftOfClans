package it.itpao25.craftofclans.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PermissionUtil {

	public static boolean _has(Player player, String perm) {
		if (!player.isOnline()) {
			return false;
		}
		// Controllo se il giocatore ha i permessi compatibili con "*"
		if (player.hasPermission("coc.*") || player.hasPermission("*")) {
			return true;
		}
		return player.hasPermission(perm);
	}

	public static boolean _has(OfflinePlayer player, String perm) {
		if (player.isOnline()) {
			Player playeron = Bukkit.getPlayer(player.getName());
			return _has(playeron, perm);
		}
		return false;
	}

	/**
	 * Controllo dei permessi per il cast CommandSender
	 */
	public static boolean _has(CommandSender sender, String perm) {
		if (sender instanceof ConsoleCommandSender) {
			return true;
		}
		if (sender instanceof Player) {
			OfflinePlayer player = (OfflinePlayer) sender;
			return _has(player, perm);
		}
		return false;
	}
}
