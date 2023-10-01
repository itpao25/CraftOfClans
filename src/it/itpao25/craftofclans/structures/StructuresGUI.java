package it.itpao25.craftofclans.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.inventory.InventoryListener;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StructuresGUI {
	private StructuresId id;
	private Player p;
	private InventoryHandler handler;

	public StructuresGUI(Player p, StructuresId id, Block block) {
		// Controllo se il villaggio è del giocatore
		PlayerStored player = new PlayerStored(p);
		
		if (!id.getVillage().isOwner(player)) {
			// La struttura non è sua
			return;
		}

		// Se sta aprendo il laboratorio
		if (id.getType().equals(StructuresEnum.LABORATORY.toString())) {
			if (block.getType().equals(Material.getMaterial("ENCHANTING_TABLE")) || block.getType().equals(Material.getMaterial("LECTERN"))) {
				new StructuresGUILaboratory(id, p);
				return;
			}
		}

		// Se sta aprendo la caserma
		if (id.getType().equals(StructuresEnum.BARRACKS.toString())) {
			if (block.getType().equals(Material.getMaterial("ENCHANTING_TABLE")) || block.getType().equals(Material.getMaterial("LECTERN"))) {
				new StructuresGUIBarracks(id, p);
				return;
			}
		}

		this.id = id;
		this.p = p;
		intid();
	}

	private void intid() {
		String name = id.getName();
		this.handler = new InventoryHandler(p, InventoryEnum.Structures_GUI, Color.translate(name));
		setItemRitiro();
		setItemView();
		setItemInfo();
		setItemUpgrade();

		// La struttura può essere spostata
		setItemBeMoved();

		// La struttura se può essere spostata può essere anche eliminata
		setItemBeDelete();

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

	private void setItemRitiro() {
		if (id.hasResource() == false)
			return;

		Map.Entry<ItemStack, Integer> entry = getItemHandler("item-withdrawal").entrySet().iterator().next();
		ItemStack item = entry.getKey();
		Integer slot = entry.getValue();

		if (slot == -1) {
			this.handler.setItem(item);
		} else {
			this.handler.setItem(slot, item);
		}
	}

	public void setItemView() {
		if (id.getType().equals("ELIXIR_STORAGE") || id.getType().equals("GOLD_STORAGE") || id.getType().equals("DARK_ELIXIR_STORAGE")) {

			Map.Entry<ItemStack, Integer> entry = getItemHandler("item-viewresource").entrySet().iterator().next();
			ItemStack item = entry.getKey();
			Integer slot = entry.getValue();

			if (slot == -1) {
				this.handler.setItem(item);
			} else {
				this.handler.setItem(slot, item);
			}

		}
	}

	public void setItemInfo() {
		Map.Entry<ItemStack, Integer> entry = getItemHandler("item-viewinfo").entrySet().iterator().next();
		ItemStack item = entry.getKey();
		Integer slot = entry.getValue();

		if (slot == -1) {
			this.handler.setItem(item);
		} else {
			this.handler.setItem(slot, item);
		}
	}

	public void setItemUpgrade() {
		if (this.id.hasLevelUp()) {

			Map.Entry<ItemStack, Integer> entry = getItemHandler("item-upgrade").entrySet().iterator().next();
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
	 * Imposto l'item se la struttura può essere spostata
	 * 
	 */
	public void setItemBeMoved() {
		if (id.getType().equals("TOWNHALL"))
			return;

		boolean isAllowd = CraftOfClans.config.getString("shop.structures-core." + id.getType() + ".can-be-moved") != null ? CraftOfClans.config.getBoolean("shop.structures-core." + id.getType() + ".can-be-moved") : false;

		if (isAllowd) {
			Map.Entry<ItemStack, Integer> entry = getItemHandler("item-move-strutture").entrySet().iterator().next();
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
	 * E
	 */
	public void setItemBeDelete() {
		if (id.getType().equals("TOWNHALL"))
			return;

		boolean isAllowd = CraftOfClans.config.getString("shop.structures-core." + id.getType() + ".can-be-moved") != null ? CraftOfClans.config.getBoolean("shop.structures-core." + id.getType() + ".can-be-moved") : false;

		if (isAllowd) {
			Map.Entry<ItemStack, Integer> entry = getItemHandler("item-delete-strutture").entrySet().iterator().next();
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
	 * Conto le risorse nella struttura
	 * 
	 * @return
	 */
	public double getResourcesInside() {

		PlayerStored pstored = new PlayerStored(p);

		if (id.getType().equals("ELIXIR_STORAGE")) {

			int conto = 0;
			for (Entry<StructuresId, String> entry : id.getVillage().getStructuresList().entrySet()) {
				if (entry.getValue().equals("ELIXIR_STORAGE")) {
					conto++;
				}
			}
			if (conto == 1) {
				return pstored.getElixir();
			}
			return pstored.getElixir() / conto;

		} else if (id.getType().equals("GOLD_STORAGE")) {

			int conto = 0;
			for (Entry<StructuresId, String> entry : id.getVillage().getStructuresList().entrySet()) {
				if (entry.getValue().equals("GOLD_STORAGE")) {
					conto++;
				}
			}
			if (conto == 1) {
				return pstored.getGold();
			}
			return pstored.getGold() / conto;

		} else if (id.getType().equals("DARK_ELIXIR_STORAGE")) {

			int conto = 0;
			for (Entry<StructuresId, String> entry : id.getVillage().getStructuresList().entrySet()) {
				if (entry.getValue().equals("DARK_ELIXIR_STORAGE")) {
					conto++;
				}
			}
			if (conto == 1) {
				return pstored.getElixirNero();
			}
			return pstored.getElixirNero() / conto;
		}

		return -1;
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

	private HashMap<ItemStack, Integer> getItemHandler(String name_) {

		String material = CraftOfClans.config.getString("structures-gui." + name_ + ".material");
		String name = Color.translate(CraftOfClans.config.getString("structures-gui." + name_ + ".title"));
		List<String> lore = CraftOfClans.config.get().getStringList("structures-gui." + name_ + ".lore");

		Integer slot = CraftOfClans.config.getString("structures-gui." + name_ + ".slot") != "" ? CraftOfClans.config.getInt(("structures-gui." + name_ + ".slot")) - 1 : -1;

		ItemStack item_corrent = new ItemStack(Material.getMaterial(material));
		ItemMeta item_meta = item_corrent.getItemMeta();
		item_meta.setDisplayName(name);

		Boolean has_lore_price = false;

		for (int c = 0; c <= lore.size() - 1; c++) {
			String current = lore.get(c);

			current = current.replace("[current]", _Number.showNumero(id.getResources()));

			if (current.contains("[capacity]")) {
				if (id.getCapacity() != 0) {
					current = current.replace("[capacity]", _Number.showNumero(id.getCapacity()));
				} else {
					current = current.replace("/", "");
					current = current.replace("[capacity]", "");
				}
			}

			current = current.replace("[inside]", _Number.showNumero(getResourcesInside()));
			current = current.replace("[name]", id.getName());
			current = current.replace("[level]", id.getLevel() + "");
			current = current.replace("[next_level]", id.getLevel() + 1 + "");

			has_lore_price = current.contains("[cost_next]");
			if (has_lore_price) {
				current = current.replace("[cost_next]", "");
			}

			// Se è l'incremento per ora
			if (current.contains("[increment_per_hour]")) {
				if (!id.getType().equals(StructuresEnum.GEMS_COLLECTOR.toString())) {
					// Se ha un incremento
					if (id.getIncrementResource() > 0) {
						current = current.replace("[increment_per_hour]", id.getIncrementResource() * 60 + "");
					} else {
						lore.remove(c);
						continue;
					}
				} else {
					lore.remove(c);
					continue;
				}
			}

			lore.set(c, Color.translate(current));
		}

		if (has_lore_price) {
			if (new StructuresInfoSetting(id.getType(), id.getLevel() + 1).costo() != null) {
				ArrayList<String> costo = new StructuresInfoSetting(id.getType(), id.getLevel() + 1).costo();
				for (String costo_str : costo) {
					lore.add(Color.translate(costo_str));
				}
			}
		}

		// Se ha le difese
		if (name_.equals("item-upgrade") || name_.equals("item-viewinfo")) {

			int levelInfoDefense = id.getLevel();
			if (name_.equals("item-upgrade")) {
				levelInfoDefense = levelInfoDefense + 1;
			}

			List<String> defense_structure = CraftOfClans.config.get().getStringList("shop.structures-gui.defense-lore-damage");

			// Se la struttura fa danno (se è una difesa)
			HashMap<String, String> getDefenseValue = new StructuresInfoSetting(id.getType(), levelInfoDefense).defenseValues();
			if (getDefenseValue.size() > 0) {
				for (String lore_defense : defense_structure) {
					if (lore_defense.contains("{damage}") && getDefenseValue.containsKey("damage")) {
						lore_defense = lore_defense.replace("{damage}", getDefenseValue.get("damage"));
						lore.add(Color.translate(lore_defense));
					}
					if (lore_defense.contains("{range}") && getDefenseValue.containsKey("range")) {
						lore_defense = lore_defense.replace("{range}", getDefenseValue.get("range"));
						lore.add(Color.translate(lore_defense));
					}
				}
			}
		}

		item_meta.setLore(lore);
		item_corrent.setItemMeta(item_meta);

		HashMap<ItemStack, Integer> result = new HashMap<>();
		result.put(item_corrent, slot);

		return result;
	}

}
