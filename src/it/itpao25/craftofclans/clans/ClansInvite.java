package it.itpao25.craftofclans.clans;

import it.itpao25.craftofclans.player.PlayerStored;

import java.util.HashMap;
import java.util.Map.Entry;

public class ClansInvite {

	private static HashMap<PlayerStored, ClanObject> lista_inviti = new HashMap<>();

	public static void addInvite(PlayerStored player, ClanObject obj) {
		lista_inviti.put(player, obj);
	}

	public static void accept(PlayerStored player) {
		lista_inviti.remove(player);
	}

	public static void negate(PlayerStored player, ClanObject obj) {
		lista_inviti.remove(player);
	}

	public static boolean hasInvite(PlayerStored player, ClanObject obj) {
		for (Entry<PlayerStored, ClanObject> inviti : lista_inviti.entrySet()) {
			if (inviti.getKey().equals(player) && inviti.getValue().equals(obj)) {
				return true;
			}
		}
		return false;
	}
}
