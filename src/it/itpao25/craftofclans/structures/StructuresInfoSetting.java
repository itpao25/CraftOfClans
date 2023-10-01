package it.itpao25.craftofclans.structures;

import java.util.ArrayList;
import java.util.HashMap;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.player.PlayerElixir;
import it.itpao25.craftofclans.player.PlayerElixirNero;
import it.itpao25.craftofclans.player.PlayerGems;
import it.itpao25.craftofclans.player.PlayerGold;
import it.itpao25.craftofclans.util._Number;

public class StructuresInfoSetting {
	private String name;
	private Integer LVL = 1;

	public StructuresInfoSetting(String name) {
		this.name = name;
	}

	public StructuresInfoSetting(String name, Integer LVL) {
		this.name = name;
		this.LVL = LVL;
	}

	/**
	 * Se esiste il livello (ad esempio per l'upgrade)
	 * @return
	 */
	public boolean existLevel() {
		if(CraftOfClans.config.get().getConfigurationSection("shop.structures-core." + name) == null) {
			return false;
		}
		if(CraftOfClans.config.get().getConfigurationSection("shop.structures-core." + name + ".levels." + LVL) != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Ritorno con il costo della struttura
	 * 
	 * @return
	 */
	public ArrayList<String> costo() {

		ArrayList<String> finale = new ArrayList<String>();

		double cost_gems = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".cost_gems") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".cost_gems") : 0;
		double cost_elixir = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".cost_elixir") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".cost_elixir") : 0;
		double cost_dark_elixir = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".cost_dark_elixir") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".cost_dark_elixir") : 0;
		double cost_gold = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".cost_gold") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".cost_gold") : 0;

		String elixir_str = PlayerElixir.getDisplayName();
		String gems_str = PlayerGems.getDisplayName();
		String elixir_dark_str = PlayerElixirNero.getDisplayName();
		String gold_str = PlayerGold.getDisplayName();

		if (cost_gems > 0)
			finale.add(gems_str + ": " + _Number.showNumero(cost_gems));

		if (cost_elixir > 0)
			finale.add(elixir_str + ": " + _Number.showNumero(cost_elixir));

		if (cost_dark_elixir > 0)
			finale.add(elixir_dark_str + ": " + _Number.showNumero(cost_dark_elixir));

		if (cost_gold > 0)
			finale.add(gold_str + ": " + _Number.showNumero(cost_gold));

		return (finale);
	}

	/**
	 * Prendo i valori di difesa di una struttura
	 * 
	 * @return
	 */
	public HashMap<String, String> defenseValues() {
		HashMap<String, String> finale = new HashMap<String, String>();
		
		double range_defense = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".damage-range") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".damage-range") : 0;
		if (range_defense != 0) {
			finale.put("range", range_defense + "");
		}

		// Danno delle freccie
		double damagearrow_defense = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".damage-arrow") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".damage-arrow") : 0;
		if (damagearrow_defense != 0) {
			finale.put("damage", damagearrow_defense + "");
		}

		// Danno delle bombe
		double damagebomb_defense = CraftOfClans.config.getString("shop.structures-core." + name + ".levels." + LVL + ".damage-bomb") != null ? CraftOfClans.config.getDouble("shop.structures-core." + name + ".levels." + LVL + ".damage-bomb") : 0;
		if (damagebomb_defense != 0) {
			finale.put("damage", damagebomb_defense + "");
		}
		return (finale);
	}
}
