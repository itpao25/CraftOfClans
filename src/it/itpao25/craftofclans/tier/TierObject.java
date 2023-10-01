package it.itpao25.craftofclans.tier;

import java.util.ArrayList;
import java.util.List;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.config.CraftOfClansTier;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TierObject {

	public String name;
	private Player p;

	public TierObject(String name) {
		this.name = name.trim().toLowerCase();
	}

	public TierObject(String name, Player p) {
		this.name = name.trim().toLowerCase();
		this.p = p;
	}

	public boolean exits() {
		return TierManager.isExistTier(name);
	}

	/**
	 * Imposto il punto di spawn per il Tier
	 */
	public boolean setSpawnPoit() {
		if (exits() == false) {
			return false;
		}
		if (this.p == null) {
			LogHandler.error("setSpawnPoit() need a Player instance");
			return false;
		}

		Location loc = p.getLocation();
		CraftOfClansTier.get().set("tiers." + this.name + ".spawn-point", loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ());
		CraftOfClansTier.save();
		p.sendMessage(Color.message("&2SpawnPoint for Tier " + name + " set successfully!"));
		return true;
	}

	/**
	 * Teletrasporto il giocatore alla Tier (al punto impostato)
	 * 
	 * @return
	 */
	public boolean tpPlayer() {
		if (exits() == false) {
			return false;
		}
		if (this.p == null) {
			LogHandler.error("setSpawnPoit() need a Player instance");
			return false;
		}

		String data = getData("spawn-point");
		if (data == null) {
			this.p.sendMessage(Color.message("&cNo spawn point set for this Tier!"));
			return false;
		}
		String worldstring = getData("world");
		World world = Bukkit.getWorld(worldstring);
		String[] split_location = data.split("_");
		int x = Integer.parseInt(split_location[0]);
		int y = Integer.parseInt(split_location[1]);
		int z = Integer.parseInt(split_location[2]);

		Location toTp = new Location(world, x, y, z);
		this.p.teleport(toTp);
		return true;
	}

	public String getData(String str) {
		String data = CraftOfClansTier.getString("tiers." + this.name + "." + str);
		return data;
	}

	/**
	 * Se il giocatore ha già comprato la Tier
	 * 
	 * @return
	 */
	public boolean hasBought() {
		if (this.p == null) {
			LogHandler.error("hasBought() need a Player instance");
			return false;
		}

		if (exits() == false) {
			return false;
		}

		PlayerStored pstored = new PlayerStored(this.p);
		if (pstored.hasTier(this.name)) {
			return true;
		}
		return false;
	}

	/**
	 * Compro la Tier per il giocatore
	 * 
	 * @return
	 */
	public boolean buy() {

		double cost_gems = 0;
		double cost_elixir = 0;
		double cost_gold = 0;
		double cost_dark_elixir = 0;

		if (CraftOfClansTier.getString("tiers." + this.name + ".cost_gems") != null) {
			cost_gems = _Number.isNumero(CraftOfClansTier.getString("tiers." + this.name + ".cost_gems")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + this.name + ".cost_gems")) : 0;
		}
		if (CraftOfClansTier.getString("tiers." + this.name + ".cost_elixir") != null) {
			cost_elixir = _Number.isNumero(CraftOfClansTier.getString("tiers." + this.name + ".cost_elixir")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + this.name + ".cost_elixir")) : 0;
		}
		if (CraftOfClansTier.getString("tiers." + this.name + ".cost_gold") != null) {
			cost_gold = _Number.isNumero(CraftOfClansTier.getString("tiers." + this.name + ".cost_gold")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + this.name + ".cost_gold")) : 0;
		}
		if (CraftOfClansTier.getString("tiers." + this.name + ".cost_dark_elixir") != null) {
			cost_dark_elixir = _Number.isNumero(CraftOfClansTier.getString("tiers." + this.name + ".cost_dark_elixir")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + this.name + ".cost_dark_elixir")) : 0;
		}

		PlayerStored sender = new PlayerStored(p);

		if (cost_gems > 0) {
			if (sender.hasGems(cost_gems) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", _Number.showNumero(cost_gems)));
				return false;
			}
		}
		if (cost_elixir > 0) {
			if (sender.hasElixir(cost_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", _Number.showNumero(cost_elixir)));
				return false;
			}
		}
		if (cost_gold > 0) {
			if (sender.hasGold(cost_gold) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", _Number.showNumero(cost_gold)));
				return false;
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.hasElixirNero(cost_dark_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", _Number.showNumero(cost_dark_elixir)));
				return false;
			}
		}

		// Rimuovo le risorse
		if (cost_gems > 0) {
			if (sender.removeGems(cost_gems)) {
				cost_gems = 0;
			}
		}
		if (cost_elixir > 0) {
			if (sender.removeElixir(cost_elixir)) {
				cost_elixir = 0;
			}
		}
		if (cost_gold > 0) {
			if (sender.removeGold(cost_gold)) {
				cost_gold = 0;
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.removeElixirNero(cost_dark_elixir)) {
				cost_dark_elixir = 0;
			}
		}
		
		// Se il comando è abilitato
		if (CraftOfClans.config.getBoolean("tiers-manager.tier-afterbuy-run-command")) {
			runCommandAfterBuy(this.name, this.p.getName());
		}
		return true;
	}

	/**
	 * Aggiungo il giocatore alla Tier
	 * 
	 * @return
	 */
	public boolean addPlayer() {
		PlayerStored pstored = new PlayerStored(this.p);
		return pstored.addTier(this.name);
	}

	/**
	 * Imposto il costo di una Tier
	 * 
	 * @param type
	 * @return
	 */
	public boolean setCost(String type, int value) {
		String type_config = null;

		switch (type) {
		case "cost_elixir":
			type_config = "cost_elixir";
			break;
		case "cost_gems":
			type_config = "cost_gems";
			break;
		case "cost_gold":
			type_config = "cost_gold";
			break;
		case "cost_dark_elixir":
			type_config = "cost_dark_elixir";
			break;
		}

		CraftOfClansTier.get().set("tiers." + this.name + "." + type_config + "", value);
		CraftOfClansTier.save();
		return true;
	}

	/**
	 * Elimino la tier
	 * 
	 * @return
	 */
	public boolean delete() {
		CraftOfClansTier.get().set("tiers." + this.name, null);
		CraftOfClansTier.save();
		return false;
	}

	/**
	 * Aggiungo un requisito per comprare un tier
	 * 
	 * @param object2
	 * @return
	 */
	public boolean addRequirement(TierObject object2) {
		List<String> prima = CraftOfClansTier.get().getStringList("tiers." + this.name + ".requirement");
		if (prima.contains(object2.name)) {
			return false;
		}
		prima.add(object2.name);

		CraftOfClansTier.get().set("tiers." + this.name + ".requirement", prima);
		CraftOfClansTier.save();
		return true;
	}

	/**
	 * Rimuovo un requisito per comprare un tier
	 * 
	 * @param object2
	 * @return
	 */
	public boolean removeRequirement(TierObject object2) {
		List<String> nuova = new ArrayList<>();
		List<String> prima = CraftOfClansTier.get().getStringList("tiers." + this.name + ".requirement");
		if (prima.contains(object2.name) == false) {
			return false;
		}
		for (String ora : prima) {
			if (!ora.equals(object2.name)) {
				nuova.add(ora);
			}
		}
		CraftOfClansTier.get().set("tiers." + this.name + ".requirement", nuova);
		CraftOfClansTier.save();
		return true;
	}

	/**
	 * Controllo se il giocatore ha i requisiti
	 */
	public boolean hasRequirements() {
		if (this.p == null) {
			LogHandler.error("hasRequirements() need a Player instance");
			return false;
		}

		PlayerStored pstored = new PlayerStored(this.p);
		List<String> lista = CraftOfClansTier.get().getStringList("tiers." + this.name + ".requirement");
		for (String tier_name : lista) {
			if (!pstored.hasTier(tier_name)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prendo la lista delle tier come requisiti
	 */
	public String getRequirements() {
		if (this.p == null) {
			LogHandler.error("getRequirements() need a Player instance");
			return null;
		}

		PlayerStored pstored = new PlayerStored(this.p);
		String nomi = "";
		List<String> lista = CraftOfClansTier.get().getStringList("tiers." + this.name + ".requirement");
		for (String tier_name : lista) {
			if (!pstored.hasTier(tier_name)) {
				nomi += tier_name + " ";
			}
		}
		return nomi;
	}

	/**
	 * Eseguo un comando dopo il buy
	 * 
	 * @return
	 */
	public boolean runCommandAfterBuy(String name, String player) {
		
		String command = CraftOfClans.config.getString("tiers-manager.tier-afterbuy-command");
		command = command.replace("{username}", player);
		command = command.replace("{name}", name);
		
		LogHandler.log("Runned command: "+ command);
		
		return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
}