package it.itpao25.craftofclans.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map.Entry;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.inventory.InventoryListener;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.troops.TroopsId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.SkullSkin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StructuresGUILaboratory {

	private StructuresId id;
	private Player p;
	private InventoryHandler handler;

	public StructuresGUILaboratory(StructuresId struct, Player p) {

		// Controllo se il villaggio è del giocatore
		PlayerStored player = new PlayerStored(p);

		if (!struct.getVillage().isOwner(player)) {
			// La struttura non è sua
			return;
		}

		this.id = struct;
		this.p = p;

		intid();
	}

	private void intid() {
		
		String name = CraftOfClansM.getString("messages.troops-name") + " " + id.getName();
		this.handler = new InventoryHandler(p, InventoryEnum.Laboratory_GUI, Color.translate(name));

		setTruppe();

		open();
		addHashMap();
	}

	public StructuresId getStructure() {
		return this.id;
	}

	public Player getPlayer() {
		return this.p;
	}

	private void open() {
		this.handler.openInventory();
	}

	private void setTruppe() {
		for (Entry<ItemStack, Integer> entry : getItemHandler().entrySet()) {
			ItemStack item = entry.getKey();
			Integer slot = entry.getValue();
			if (slot == -1) {
				this.handler.setItem(item);
			} else {
				this.handler.setItem(slot, item);
			}
		}
	}

	/**
	 * Aggiungo l'utente
	 * 
	 * @return
	 */
	private boolean addHashMap() {
		if (InventoryListener.StructuresGUI.containsKey(p)) {
			InventoryListener.StructuresGUI.remove(p);
		}
		InventoryListener.StructuresGUI.put(p, id);
		return false;
	}

	private HashMap<ItemStack, Integer> getItemHandler() {

		HashMap<ItemStack, Integer> result = new HashMap<>();

		PlayerStored pstored = new PlayerStored(p);

		for (String name : CraftOfClans.troops.get().getConfigurationSection("troops").getKeys(false)) {
			boolean is_enabled = CraftOfClans.troops.getBoolean("troops." + name + ".enable");
			if (!is_enabled) {
				continue;
			}

			TroopsId trop;

			boolean has_troop = false;
			String nome_lore = "laboratory-gui.item-buy.lore";
			if (pstored.hasTroop(name, 0)) {
				nome_lore = "laboratory-gui.item-having.lore";
				has_troop = true;
				trop = pstored.getTroop(name);
			} else {
				trop = new TroopsId(pstored, name, 1);
			}

			String titolo = Color.translate(CraftOfClans.troops.getString("troops." + name + ".display_name"));
			Integer slot = CraftOfClans.troops.getString("troops." + name + ".slot") != "" ? CraftOfClans.troops.getInt("troops." + name + ".slot") - 1 : -1;
			List<String> lore = CraftOfClans.troops.get().getStringList(nome_lore);

			List<String> lore_final = new ArrayList<>();

			for (int c = 0; c <= lore.size() - 1; c++) {
				String current = Color.translate(lore.get(c));

				if (has_troop) {

					if (current.contains("[current]")) {
						current = current.replace("[current]", trop.getLevel() + "");
						lore_final.add(current);
						continue;
					}

					// Livello prossimo della truppa
					if (current.contains("[next_level]")) {
						if (trop.hasNextLevel()) {
							current = current.replace("[next_level]", (trop.getLevel() + 1) + "");
							lore_final.add(current);
						}
						continue;
					}

					// Costo del prossimo livello
					if (current.contains("[cost_next]")) {
						if (trop.hasNextLevel()) {

							TroopsId trop_next = new TroopsId(pstored, name, trop.getLevel() + 1);
							current = current.replace("[cost_next]", "");
							lore_final.add(current);
							lore_final.addAll(trop_next.getCostStringList());
						}
						continue;
					}

					// Livello attuale della truppa
					if (current.contains("[level]")) {
						current = current.replace("[level]", trop.getLevel() + "");
						lore_final.add(current);
						continue;
					}

				} else {

					if (current.contains("[cost]")) {
						current = current.replace("[cost]", "");
						lore_final.add(current);
						lore_final.addAll(trop.getCostStringList());

						continue;
					}
				}

				lore_final.add(current);
			}

			ItemStack item_corrent = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta item_meta = item_corrent.getItemMeta();
			item_meta.setDisplayName(titolo);

			item_meta.setLore(lore_final);
			
			item_meta = SkullSkin.setSkullSkin(item_meta, trop.type);
			item_corrent.setItemMeta(item_meta);

			result.put(item_corrent, slot);
		}
		return result;
	}

}
