package it.itpao25.craftofclans.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.village.VillageId;

public class AttackerManager {

	public static HashMap<UUID, VillageId> attackers = new HashMap<>();
	public static HashMap<UUID, Attack> battles = new HashMap<>();
	public static HashMap<StructuresId, HashMap<ArrayList<Block>, Integer>> battles_blocks = new HashMap<>();
	private static HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>();

	/**
	 * Finisco l'attacco da parte del giocatore
	 * 
	 * @param p
	 * @return
	 */
	public boolean stopAttack(Player p) {
		PlayerStored pstored = new PlayerStored(p);
		if (hasPlayer(p.getUniqueId())) {
			p.teleport(pstored.getVillage().locBase());
		}
		return false;
	}

	/**
	 * Durata di un attacco per il giocatore
	 * 
	 * @return
	 */
	public static int getTimeMatch() {
		return CraftOfClans.config.getString("attack.time") != null ? CraftOfClans.config.getInt("attack.time") : 60;
	}

	public static boolean hasPlayer(UUID id) {
		if (attackers.containsKey(id)) {
			return true;
		}
		return false;
	}

	/**
	 * Rimuovo il player dalla lista
	 */
	public static boolean removePlayer(UUID id) {
		if (attackers.containsKey(id)) {
			attackers.remove(id);
			return true;
		}
		return false;
	}

	/**
	 * Controllo se il giocatore è presente nel cooldown tra un attacco e un altro
	 * 
	 * @param id
	 * @return
	 */
	public static int isInCooldown(UUID id) {
		if (cooldowns.containsKey(id)) {
			int time = CraftOfClans.config.getString("attack.cooldown-time") != null ? CraftOfClans.config.getInt("attack.cooldown-time") : 60;
			long coolDownRimasto = ((cooldowns.get(id) / 1000) + time) - (System.currentTimeMillis() / 1000);
			if (coolDownRimasto >= 0) {
				return (int) coolDownRimasto;
			}
		}
		return 0;
	}

	/**
	 * Aggiungo il giocatore nel cooldown
	 * 
	 * @param id
	 * @return
	 */
	public static boolean addInCooldown(UUID id) {
		cooldowns.put(id, System.currentTimeMillis());
		return true;
	}

	/**
	 * Compatibilità raggio livello municipio per attacchi. Max
	 * 
	 * @return
	 */
	public static int getRangeAttackTownhallMax() {
		int max = CraftOfClans.config.getString("attack.townhall-level-range-compatibility-max") != null ? CraftOfClans.config.getInt("attack.townhall-level-range-compatibility-max") : 3;
		return max;
	}

	/**
	 * Compatibilità raggio livello municipio per attacchi. Min
	 * 
	 * @return
	 */
	public static int getRangeAttackTownhallMin() {
		int min = CraftOfClans.config.getString("attack.townhall-level-range-compatibility-min") != null ? CraftOfClans.config.getInt("attack.townhall-level-range-compatibility-min") : 2;
		return min;
	}
}
