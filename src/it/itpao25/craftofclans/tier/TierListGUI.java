package it.itpao25.craftofclans.tier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.config.CraftOfClansTier;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

public class TierListGUI {

	private Player p;

	public TierListGUI(Player p) {
		this.p = p;
		setup();
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
	 * Imposto la GUI attraverso il config (tiers.yml)
	 * 
	 * @return
	 */
	private boolean setup() {

		// Imposto il size dell'inventario
		int size = 0;
		if (ListTier().size() <= 9) {
			size = 9;
		} else if (ListTier().size() <= 18) {
			size = 18;
		} else if (ListTier().size() <= 27) {
			size = 27;
		} else if (ListTier().size() <= 36) {
			size = 36;
		} else if (ListTier().size() <= 45) {
			size = 45;
		} else if (ListTier().size() <= 54) {
			size = 54;
		}

		// Index dell'oggetto inserito
		int index_item = 0;
		InventoryHandler inv = new InventoryHandler(p, InventoryEnum.Tier_GUI, size);
		for (String s : ListTier()) {
			// Se ha raggiunto il limite degli oggetti inseriti
			if (index_item > 54) {
				return true;
			}
			String name = s;
			TierObject object = new TierObject(s, this.p);
			ItemStack item_corrent = null;

			if (object.hasBought()) {
				String name_bought = CraftOfClans.config.getString("tiers-manager.tier-gui-item.already-bought") != null ? CraftOfClans.config.getString("tiers-manager.tier-gui-item.already-bought") : "DIRT";
				item_corrent = new ItemStack(Material.getMaterial(name_bought), 1);

				ItemMeta meta_item = item_corrent.getItemMeta();
				List<String> lores = new ArrayList<String>();
				lores.add(Color.translate(CraftOfClansM.getString("tiers.click-to-teletransport")));
				meta_item.setLore(lores);
				meta_item.setDisplayName(name);
				item_corrent.setItemMeta(meta_item);
			} else {
				String name_tobuy = CraftOfClans.config.getString("tiers-manager.tier-gui-item.to-buy") != null ? CraftOfClans.config.getString("tiers-manager.tier-gui-item.to-buy") : "MYCEL";
				item_corrent = new ItemStack(Material.getMaterial(name_tobuy), 1);

				ItemMeta meta_item = item_corrent.getItemMeta();
				meta_item.setDisplayName(name);

				boolean has_cost = false;
				// Lore
				List<String> lores = new ArrayList<String>();
				if (CraftOfClansTier.getString("tiers." + name + ".cost_gems") != null) {
					double cost_gems = _Number.isNumero(CraftOfClansTier.getString("tiers." + name + ".cost_gems")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + name + ".cost_gems")) : 0;
					if (cost_gems != 0) {
						has_cost = true;
						lores.add(Color.translate(CraftOfClansM.getString("tiers.cost-gems") + ": " + _Number.showNumero(cost_gems)));
					}
				}
				if (CraftOfClansTier.getString("tiers." + name + ".cost_elixir") != null) {
					double cost_elixir = _Number.isNumero(CraftOfClansTier.getString("tiers." + name + ".cost_elixir")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + name + ".cost_elixir")) : 0;
					if (cost_elixir != 0) {
						has_cost = true;
						lores.add(Color.translate(CraftOfClansM.getString("tiers.cost-elixir") + ": " + _Number.showNumero(cost_elixir)));
					}
				}
				if (CraftOfClansTier.getString("tiers." + name + ".cost_dark_elixir") != null) {
					double cost_dark_elixir = _Number.isNumero(CraftOfClansTier.getString("tiers." + name + ".cost_dark_elixir")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + name + ".cost_dark_elixir")) : 0;
					if (cost_dark_elixir != 0) {
						has_cost = true;
						lores.add(Color.translate(CraftOfClansM.getString("tiers.cost-dark-elixir") + ": " + _Number.showNumero(cost_dark_elixir)));
					}
				}
				if (CraftOfClansTier.getString("tiers." + name + ".cost_gold") != null) {
					double cost_gold = _Number.isNumero(CraftOfClansTier.getString("tiers." + name + ".cost_gold")) ? Double.parseDouble(CraftOfClansTier.getString("tiers." + name + ".cost_gold")) : 0;
					if (cost_gold != 0) {
						has_cost = true;
						lores.add(Color.translate(CraftOfClansM.getString("tiers.cost-gold") + ": " + _Number.showNumero(cost_gold)));
					}
				}

				if (!has_cost) {
					lores.add(Color.translate(CraftOfClansM.getString("tiers.no-cost")));
				}

				if (CraftOfClansTier.getString("tiers." + name + ".requirement") != null) {
					List<String> lista = CraftOfClansTier.get().getStringList("tiers." + name + ".requirement");
					lores.add(Color.translate(CraftOfClansM.getString("tiers.needs-requirements")));
					for (String required : lista) {
						lores.add(Color.translate("- " + required));
					}
				}

				meta_item.setLore(lores);
				item_corrent.setItemMeta(meta_item);
			}

			inv.setItem(item_corrent);
			index_item++;
		}
		inv.openInventory();
		return false;
	}

}
