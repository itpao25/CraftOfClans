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

public class StructuresGUIBarracks {

	private StructuresId id;
	private Player p;
	private InventoryHandler handler;

	public StructuresGUIBarracks(StructuresId struct, Player p) {

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

		for (TroopsId truppa : pstored.getTroops()) {
			
			ItemStack item_corrent = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta item_meta = item_corrent.getItemMeta();
			item_meta.setDisplayName(truppa.display_name);

			// Cerco il lore corretto
			String lore_path = "";
			
			if (truppa.isToTraning()) {
				// Avvia addestramento
				lore_path = "barracks-gui.item-troop-training.lore";
			} else if (truppa.isReady()) {
				// Si può prelevare
				lore_path = "barracks-gui.item-troop-pickup.lore";
			} else {
				// In lavorazione
				lore_path = "barracks-gui.item-troop-onhold.lore";
			}
			
			List<String> lore = CraftOfClans.troops.get().getStringList(lore_path);
			List<String> lore_final = new ArrayList<>();

			for (int c = 0; c <= lore.size() - 1; c++) {
				String current = Color.translate(lore.get(c));

				// nome della truppa
				if (current.contains("[name]")) {
					current = current.replace("[name]", truppa.display_name);
					lore_final.add(current);
					continue;
				}

				// Tempo richiesto per l'addestramento
				if (current.contains("[time_required]")) {
					current = current.replace("[time_required]", truppa.getTimeTraining() + " sec.");
					lore_final.add(current);
					continue;
				}

				// Tempo rimanente
				if (current.contains("[time_remaining]")) {
					current = current.replace("[time_remaining]", truppa.getTime_remaining() + " sec.");
					lore_final.add(current);
					continue;
				}
				
				// Costo di addestramento
				if (current.contains("[cost_training]")) {
					current = current.replace("[cost_training]", "");
					lore_final.addAll(truppa.getCostTraningList());
					continue;
				}
				
				lore_final.add(current);
			}

			item_meta.setLore(lore_final);
			
			item_meta = SkullSkin.setSkullSkin(item_meta, truppa.type);
			item_corrent.setItemMeta(item_meta);

			result.put(item_corrent, -1);
		}
		return result;
	}

}
