package it.itpao25.craftofclans.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.inventory.InventoryShopBuild;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.StructuresInfoSetting;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandShop {

	public CommandShop(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}
		if (!PermissionUtil._has(sender, _Permission.PERM_SHOP) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}
		Player p = (Player) sender;
		if (args.length == 1) {

			PlayerStored pstored = new PlayerStored(p);

			// Controllo se ha un villaggio
			if (!pstored.hasVillage()) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village")));
				return;
			}

			InventoryHandler inv = new InventoryHandler(p, InventoryEnum.Shop_build);
			HashMap<ItemStack, Integer> items = new HashMap<>();
			int slot_fall = 0;

			// Numero massimo di strutture
			String max_structure_string = CraftOfClans.config.getString("shop.structures-gui.max-build-number");

			// Lore delle difese
			List<String> defense_structure = CraftOfClans.config.get().getStringList("shop.structures-gui.defense-lore-damage");

			// Compongo la GUI per lo shop delle strutture
			for (String string : CraftOfClans.config.get().getConfigurationSection("shop.structures-gui").getKeys(false)) {
				if (CraftOfClans.config.getString("shop.structures-gui." + string + ".title") != null) {

					// Controllo se la struttura puo essere comprata
					if (CraftOfClans.config.getString("shop.structures-gui." + string + ".active") != null) {
						if (CraftOfClans.config.getBoolean("shop.structures-gui." + string + ".active") == false) {
							continue;
						}
					}

					int slot = CraftOfClans.config.getString("shop.structures-gui." + string + ".slot") != null ? CraftOfClans.config.getInt("shop.structures-gui." + string + ".slot") - 1 : slot_fall;
					String material = CraftOfClans.config.getString("shop.structures-gui." + string + ".material");
					ItemStack item_corrent = new ItemStack(Material.getMaterial(material));

					ItemMeta item_corrent_meta = item_corrent.getItemMeta();
					item_corrent_meta.setDisplayName(Color.translate(CraftOfClans.config.getString("shop.structures-gui." + string + ".title")));
					ArrayList<String> lista = new StructuresInfoSetting(string, 1).costo();
					ArrayList<String> lore = new ArrayList<String>();

					// Aggiungo il numero massimo di strutture
					int max_structures = SchematicsHandler.getMaxStructuresByType(pstored.getVillage(), string);
					if (max_structures != -1) {

						// Ora
						int numero_now = MapInfo.getStructuresAtVillageByType(string, pstored.getVillage());
						String maxstring_lc = max_structure_string.replace("{now}", numero_now + "");

						// Massime
						maxstring_lc = maxstring_lc.replace("{max}", max_structures + "");

						lore.add(Color.translate(maxstring_lc));
					}

					for (String lore_lc : lista) {
						lore.add(Color.translate(lore_lc));
					}
					
					// Se la struttura fa danno (se è una difesa)
					HashMap<String, String> getDefenseValue = new StructuresInfoSetting(string, 1).defenseValues();
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
					
					item_corrent_meta.setLore(lore);
					item_corrent.setItemMeta(item_corrent_meta);
					items.put(item_corrent, slot);
					slot_fall++;
				}
			}
			for (Entry<ItemStack, Integer> item : items.entrySet()) {
				inv.setItem(item.getValue(), item.getKey());
			}

			// Apro l'inventario
			inv.openInventory();
			return;

		} else if (args.length == 3) {

			if (args[0].equalsIgnoreCase("shop")) {
				if (args[1].equalsIgnoreCase("buy")) {

					InventoryShopBuild shopbuild = new InventoryShopBuild(args[2], new PlayerStored(p));
					shopbuild.getItemResponse();

					return;
				}
			}

		}
		p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.shop-use")).replace("%1%", "/coc shop"));
	}
}
