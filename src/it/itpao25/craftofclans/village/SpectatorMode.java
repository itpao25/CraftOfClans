package it.itpao25.craftofclans.village;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.util.Color;

public class SpectatorMode {

	public static HashMap<Player, VillageId> player_inspect = new HashMap<Player, VillageId>();
	public static HashMap<Player, Location> player_original_position = new HashMap<Player, Location>();

	/**
	 * Inserisco nello spectator mode
	 * 
	 * @param p
	 * @param id
	 */
	public static void sendInSpect(Player p, VillageId id) {

		if (inSpectator(p)) {
			return;
		}

		player_original_position.put(p, p.getLocation());
		player_inspect.put(p, id);

		p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-entred").replace("%1%", "/coc attack")));
		
		// Disabilito la fly
		p.setAllowFlight(false);
		p.setFlying(false);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
			public void run() {
				if (!inSpectator(p)) {
					return;
				}
				p.teleport(id.spectatorLocation(), TeleportCause.SPECTATE);
			}
		}, 2 * 20);
	}

	public static boolean inSpectator(Player p) {
		if (player_inspect.containsKey(p)) {
			return true;
		}
		return false;
	}

	public static void remove(Player p, boolean toremove_position) {
		if (player_inspect.containsKey(p)) {
			player_inspect.remove(p);
		}
		if (toremove_position) {
			if (player_original_position.containsKey(p)) {
				player_original_position.remove(p);
			}
		}
	}
}
