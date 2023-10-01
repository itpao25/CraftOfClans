package it.itpao25.craftofclans.structures;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.ResourceCollected;
import it.itpao25.craftofclans.api.StructuresResourceCollection;
import it.itpao25.craftofclans.api.StructuresResourceCollectionFinal;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.worldmanager.Sounds;

public class StructuresGUIItem {
	private String title;
	private String type;
	private StructuresId id;
	private PlayerStored p;
	private boolean response = false;

	public StructuresGUIItem(String title, StructuresId id, PlayerStored p) {
		this.title = title;
		this.id = id;
		this.p = p;
		initd();
		action();
	}

	private boolean initd() {
		for (String key : CraftOfClans.config.get().getConfigurationSection("structures-gui").getKeys(false)) {
			if (CraftOfClans.config.get().getConfigurationSection("structures-gui." + key) == null)
				continue;
			String slot = Color.translate(CraftOfClans.config.get().getString("structures-gui." + key + ".title"));
			if (this.title.equals(slot)) {
				this.type = CraftOfClans.config.get().getString("structures-gui." + key + ".type");
			}
		}
		return true;
	}

	/**
	 * Eseguo l'azione dell'item per l'inventory StructuresGUI
	 * 
	 * @return
	 */
	private boolean action() {
		// Controllo il tipo di oggetto
		if (this.type == null)
			return false;
		if (this.type.equals("WITHDRAWAL")) {

			StructuresResourceCollection event = new StructuresResourceCollection(id, id.getVillage(), p.get());
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}

			double resources = id.getResources();

			if (id.getType().equals("COLLECTOR_ELIXIR")) {

				if (!p.addElixir(resources)) {

					// Calcolo quanto ne può ricevere prima di finire lo spazio
					double massimo_rimane = p.elixirSpaceRemains();

					if (massimo_rimane <= 0) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.elixir-storage-full")));
						return false;
					} else {
						if (p.addElixir(massimo_rimane)) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.pick-elixir-success").replace("%1%", _Number.showNumero(massimo_rimane))));
							resources = resources - massimo_rimane;
						} else {
							return false;
						}
					}

				} else {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.pick-elixir-success").replace("%1%", _Number.showNumero(resources))));
					resources = 0;
				}
			} else if (id.getType().equals("GOLD_MINE")) {

				if (!p.addGold(resources)) {

					// Calcolo quanto ne può ricevere prima di finire lo spazio
					double massimo_rimane = p.goldSpaceRemains();

					if (massimo_rimane <= 0) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.gold-storage-full")));
						return false;
					} else {
						if (p.addGold(massimo_rimane)) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.pick-gold-success").replace("%1%", _Number.showNumero(massimo_rimane))));
							resources = resources - massimo_rimane;
						} else {
							return false;
						}
					}

				} else {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.pick-gold-success").replace("%1%", _Number.showNumero(resources))));
					resources = 0;
				}

			} else if (id.getType().equals("GEMS_COLLECTOR")) {
				if (!p.addGems(resources)) {
					return false;
				} else {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.pick-gems-success").replace("%1%", _Number.showNumero(resources))));
					resources = 0;
				}

			} else if (id.getType().equals("DARK_ELIXIR_DRILL")) {

				if (!p.addElixirNero(resources)) {

					// Calcolo quanto ne può ricevere prima di finire lo spazio
					double massimo_rimane = p.darkElixrSpaceRemains();

					if (massimo_rimane <= 0) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.dark-elixir-storage-full")));
						return false;
					} else {
						if (p.addElixirNero(massimo_rimane)) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.elixir-dark-success").replace("%1%", _Number.showNumero(massimo_rimane))));
							resources = resources - massimo_rimane;
						} else {
							return false;
						}
					}

				} else {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.pick-elixir-dark-success").replace("%1%", _Number.showNumero(resources))));
					resources = 0;
				}
			}

			// Suono al giocatore
			p.get().playSound(p.get().getLocation(), Sounds.ORB_PICKUP.bukkitSound(), 1000, 1);

			// Svuoto la struttura
			id.setResources(resources);

			CraftOfClansData.save();
			CraftOfClansData.reload();
			this.response = true;

			StructuresResourceCollectionFinal event2 = new StructuresResourceCollectionFinal(id, id.getVillage(), p.get());
			Bukkit.getServer().getPluginManager().callEvent(event2);
			if (event2.isCancelled()) {
				return false;
			}

			// Aggiorno gli hologrammi
			ResourceCollected event1 = new ResourceCollected(id);
			Bukkit.getServer().getPluginManager().callEvent(event1);

			return true;

		} else if (this.type.equals("UPGRADE")) {

			StructuresUpgrade upgrade = new StructuresUpgrade(id, p);
			if (upgrade.getResponse()) {
				this.response = true;
			}
			return false;

		} else if (this.type.equals("MOVE")) {

			// Faccio la richiesta per spostare la struttura
			id.requestToMove(p.get());
			p.get().closeInventory();

		} else if (this.type.equals("DELETE")) {

			if (SchematicsHandler.structures_delete.containsKey(p.get())) {
				SchematicsHandler.structures_delete.remove(p.get());
			}

			HashMap<Long, StructuresId> hashmap = new HashMap<Long, StructuresId>();
			hashmap.put(System.currentTimeMillis(), id);

			SchematicsHandler.structures_delete.put(p.get(), hashmap);
			p.get().closeInventory();
			
			InventoryHandler inventory = new InventoryHandler(p.get(), InventoryEnum.StructuresDelete_GUI, Color.translate(CraftOfClansM.getString("messages.gui-confirm-delete-structure")));
			
			ItemStack item_green = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
			ItemMeta item_green_meta = item_green.getItemMeta();
			item_green_meta.setDisplayName(Color.translate(CraftOfClansM.getString("messages.gui-delete-structure-yes")));
			item_green.setItemMeta(item_green_meta);
			
			inventory.setItem(1, item_green);
			inventory.setItem(0, item_green);
			inventory.setItem(2, item_green);
			
			ItemStack item_red = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
			ItemMeta item_red_meta = item_red.getItemMeta();
			item_red_meta.setDisplayName(Color.translate(CraftOfClansM.getString("messages.gui-delete-structure-no")));
			item_red.setItemMeta(item_red_meta);
			
			inventory.setItem(6, item_red);
			inventory.setItem(7, item_red);
			inventory.setItem(8, item_red);
			inventory.openInventory();
		}

		return false;
	}

	public boolean getResponse() {
		return this.response;
	}
}
