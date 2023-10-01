package it.itpao25.craftofclans.guardian;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;

public class GuardianUpgrade {
	
	private GuardianVillage gvillage;
	private PlayerStored sender;
	private Boolean response = false;

	public GuardianUpgrade(GuardianVillage gvillage, PlayerStored sender) {

		this.gvillage = gvillage;
		this.sender = sender;
		
		update();
	}

	/**
	 * Controllo se è disponible un upgrade
	 * 
	 * @return
	 */
	private boolean checkLevel() {
		if (gvillage.hasLevelUp()) {
			return true;
		}
		return false;
	}

	/**
	 * Gestisco i costi dell'aggiornamento
	 * 
	 * @return
	 */
	private boolean resource() {
		
		double cost_gems = gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_gems") != null ? Double.parseDouble(gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_gems")) : 0;
		double cost_elixir = gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_elixir") != null ? Double.parseDouble(gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_elixir")) : 0;
		double cost_gold = gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_gold") != null ? Double.parseDouble(gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_gold")) : 0;
		double cost_dark_elixir = gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_dark_elixir") != null ? Double.parseDouble(gvillage.getDataCustom(gvillage.getLevel() + 1, "cost_dark_elixir")) : 0;
		
		if (cost_gems > 0) {
			if (sender.hasGems(cost_gems) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", cost_gems + ""));
				return false;
			}
		}
		if (cost_elixir > 0) {
			if (sender.hasElixir(cost_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", cost_elixir + ""));
				return false;
			}
		}
		if (cost_gold > 0) {
			if (sender.hasGold(cost_gold) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", cost_gold + ""));
				return false;
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.hasElixirNero(cost_dark_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", cost_dark_elixir + ""));
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
		return true;
	}

	/**
	 * Controllo se l'aggiornamento può essere effettuato controllo il livello del
	 * municipio è ok
	 * 
	 * @return
	 */
	public boolean hasLevelTownhall() {
		VillageId id = gvillage.getVillage();
		if (gvillage.getDataCustom(gvillage.getLevel() + 1, "townhall-required") != null) {
			if (_Number.isNumero(gvillage.getDataCustom(gvillage.getLevel() + 1, "townhall-required"))) {
				int level = Integer.parseInt(gvillage.getDataCustom(gvillage.getLevel() + 1, "townhall-required"));
				if (id.getLevelTownHall() < level) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.townhall-required-upgrade")).replace("%1%", level + ""));
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean npc_upgrade() {
		gvillage.getNPC().destroy();
		gvillage.spawn();
		
		return true;
	}
	
	/**
	 * Imposto l'aggiornamento della struttura
	 * 
	 * @return
	 */
	public boolean update() {
		int next_level = gvillage.getLevel() + 1;
		if (checkLevel() == false) {
			return false;
		}

		// Controllo se il livello del municipio soddisfa la richiesta
		if (hasLevelTownhall() == false) {
			return false;
		}
		
		// Controllo se il giocatore ha le risorse necessarie
		// I messaggi vengono gestiti dalla funzione in modo diretto
		if (resource() == false) {
			return false;
		}
		
		gvillage.setLevel(next_level);
		npc_upgrade();
		
		sender.sendMessage(Color.message(CraftOfClansM.getString("messages.upgrade-success-guardian")));
		
		this.response = true;
		return true;
	}

	/**
	 * Ritorno con la risposta
	 * 
	 * @return
	 */
	public boolean getResponse() {
		return this.response;
	}
}
