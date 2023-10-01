package it.itpao25.craftofclans.tier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansTier;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.village.VillageCuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TierManager {

	/**
	 * Creo una nuova tier
	 * 
	 * @param name
	 * @return
	 */
	public boolean create(Player p, String name) {
		if (name == null || name.trim() == "") {
			return true;
		}
		name = name.trim().toLowerCase();

		if (!CraftOfClans.isWorldEdit) {
			LogHandler.error("WorldEdit is not installed!");
			return false;
		}

		if (isExistTier(name)) {
			p.sendMessage(Color.message("&cThis tier already exists!"));
			return false;
		}

		// Prendo le coordinate
		ArrayList<Location> locals = WorldEditUtil.getSelectionPlayer(p);
		if (locals == null) {
			return false;
		}
		Location min = locals.get(0);
		Location max = locals.get(1);
		
		if (!CraftOfClans.freemode) {
			if (min.getWorld().getName().equals("clansworld") || max.getWorld().getName().equals("clansworld")) {
				p.sendMessage(Color.message("You can't create the new tier in the clansworld!"));
				return false;
			}
		}
		
		return create(name, min, max, p);
	}

	/**
	 * Creo una nuova tier compresi di Location Questa funzione crea definitivamente
	 * la miniera nel config
	 * 
	 * @param name
	 * @param min
	 * @param max
	 * @return
	 */
	public boolean create(String name, Location min, Location max, Player p) {
		if (name == null || name.trim() == "") {
			return true;
		}
		if (isExistTier(name)) {
			return false;
		}

		VillageCuboid cuboid = new VillageCuboid(min, max);
		// Inserisco il Tier nel file
		CraftOfClansTier.get().createSection("tiers." + name);
		CraftOfClansTier.get().set("tiers." + name + ".world", cuboid.getWorld().getName());
		CraftOfClansTier.get().set("tiers." + name + ".point-min", cuboid.getLowerX() + "_" + cuboid.getLowerY() + "_" + cuboid.getLowerZ());
		CraftOfClansTier.get().set("tiers." + name + ".point-max", cuboid.getUpperX() + "_" + cuboid.getUpperY() + "_" + cuboid.getUpperZ());
		CraftOfClansTier.get().set("tiers." + name + ".cost_gems", 0);
		CraftOfClansTier.get().set("tiers." + name + ".cost_elixir", 0);
		CraftOfClansTier.get().set("tiers." + name + ".cost_dark_elixir", 0);
		CraftOfClansTier.get().set("tiers." + name + ".cost_gold", 0);
		CraftOfClansTier.save();
		return true;
	}

	/**
	 * Lista delle Tier
	 * 
	 * @return
	 */
	public static Set<String> ListTier() {
		Set<String> list = null;
		if (CraftOfClansTier.get().getConfigurationSection("tiers") == null) {
			Set<String> new_set = new HashSet<String>();
			return new_set;
		}
		list = CraftOfClansTier.get().getConfigurationSection("tiers").getKeys(false);
		return list;
	}

	/**
	 * Lista delle Tier in output verso un giocatore
	 * 
	 * @param p
	 * @return
	 */
	public boolean list(Player p) {
		for (String s : ListTier()) {
			p.sendMessage(s);
		}
		return true;
	}

	/**
	 * Controllo se un nome Tier è già occupato
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isExistTier(String name) {
		name = name.toLowerCase();
		if (ListTier().contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Ritorno con il nome della GUI nella lista delle Tier
	 * 
	 * @return String name of gui
	 */
	public static String getGUIName() {
		String name = Color.translate(CraftOfClans.config.getString("tiers-manager.tier-gui-title"));
		return name;
	}
}
