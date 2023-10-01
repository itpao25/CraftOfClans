package it.itpao25.craftofclans.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.StructuresInfoSetting;
import it.itpao25.craftofclans.util.Color;

public class InventoryShopToPlayer {

	public InventoryShopToPlayer(Player p, String name_item, int livello) {

		String material = CraftOfClans.config.getString("shop.structures-gui." + name_item + ".material");
		String name = Color.translate(CraftOfClans.config.getString("shop.structures-gui." + name_item + ".title"));

		// Imposto nel lore il villaggio di provenienza
		List<String> lore = new ArrayList<>();
		lore.add("Level: " + livello);

		// Se la struttura fa danno (se è una difesa)
		List<String> defense_structure = CraftOfClans.config.get().getStringList("shop.structures-gui.defense-lore-damage");
		
		HashMap<String, String> getDefenseValue = new StructuresInfoSetting(name_item, livello).defenseValues();
		if (getDefenseValue.size() > 0) {
			for (String lore_defense : defense_structure) {
				if(lore_defense.contains("{damage}") && getDefenseValue.containsKey("damage")) {
					lore_defense = lore_defense.replace("{damage}", getDefenseValue.get("damage"));
					lore.add(Color.translate(lore_defense));
				}
				if(lore_defense.contains("{range}") && getDefenseValue.containsKey("range")) {
					lore_defense = lore_defense.replace("{range}", getDefenseValue.get("range"));
					lore.add(Color.translate(lore_defense));
				}
			}
		}

		String villaggioid = new PlayerStored(p).getVillage().getOwnerName();
		lore.add("&7&o@Village: " + villaggioid);
		lore.addAll(CraftOfClans.config.get().getStringList("shop.structures-gui." + name_item + ".lore"));

		ItemStack item_corrent = new ItemStack(Material.getMaterial(material));
		ItemMeta item_meta = item_corrent.getItemMeta();

		item_meta.setDisplayName(name);
		for (int c = 0; c <= lore.size() - 1; c++) {
			lore.set(c, Color.translate(lore.get(c)));
		}

		item_meta.setLore(lore);
		item_corrent.setItemMeta(item_meta);
		p.getInventory().addItem(item_corrent);
	}
}
