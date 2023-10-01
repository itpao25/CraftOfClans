package it.itpao25.craftofclans.inventory;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.GuardianGUI;
import it.itpao25.craftofclans.guardian.GuardianVillage;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.handler.SellHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.StructuresGUIItem;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.troops.TroopsId;
import it.itpao25.craftofclans.util.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

	public static HashMap<Player, StructuresId> StructuresGUI = new HashMap<Player, StructuresId>();
	public static HashMap<Player, GuardianVillage> GuardianNPC = new HashMap<Player, GuardianVillage>();

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		// Vendita degli oggetti
		if (e.getView().getTitle().equals(Color.translate(CraftOfClans.config.getString("sell.gui-title")))) {
			ArrayList<ItemStack> item_final2 = new ArrayList<>();
			try {
				for (int i = 0; i <= e.getInventory().getSize(); i++) {
					ItemStack item = e.getInventory().getItem(i);
					if (item != null) {
						item_final2.add(item);
					}
				}
			} catch (ArrayIndexOutOfBoundsException errore) {
			}
			if (item_final2.size() == 0) {
				return;
			}
			Player player = (Player) e.getPlayer();
			PlayerStored p = new PlayerStored(player);
			SellHandler finale = new SellHandler(item_final2, p);
			finale.registerPremi();
		}

		// StructuresGUI
		if (StructuresGUI.containsKey(e.getPlayer())) {
			StructuresGUI.remove(e.getPlayer());
		}

		// GuardianNPC
		if (InventoryListener.GuardianNPC.containsKey(e.getPlayer())) {
			InventoryListener.GuardianNPC.remove(e.getPlayer());
		}
	}

	/**
	 * Evito che oggetti non registrati vengano venduti
	 * 
	 * @param e
	 */
	@EventHandler
	public void onInventoryDrag(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (e.getView().getTitle().equals(Color.translate(CraftOfClans.config.getString("sell.gui-title")))) {
			Player p = (Player) e.getWhoClicked();
			
			if(e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
			}
			
			boolean found = false;
			int minimu_amount = 0;
			Material materiale = null;

			List<String> list = CraftOfClans.config.get().getStringList("sell.price");
			for (String price : list) {
				if (price.contains(":") == false) {
					LogHandler.error("The price of sell " + price + " is not valid!");
					return;
				}

				String[] result_price = price.split(":");
				int amount = Integer.parseInt(result_price[0]);
				String name = result_price[1];
				double gems = Double.parseDouble(result_price[2]);
				ItemStack items = e.getCurrentItem();
				materiale = items.getType();
				if (items.getType().toString().equals(name)) {
					if (items.getAmount() < amount) {
						found = false;
						minimu_amount = amount;
					} else {
						found = true;
						p.sendMessage(Color.message("&aYou can sell " + amount + " of " + materiale + " for " + gems + " gems"));
					}
				}
			}
			if (!found) {
				// e.setCancelled(true);
				if (minimu_amount != 0) {
					p.sendMessage(Color.message("&cYou must sell at least " + minimu_amount + " of " + materiale));
				} else {
					e.setCancelled(true);
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.sell-returned-item-notpresent")).replace("%1%", "" + materiale));
				}
			}
		}
	}

	@EventHandler
	public void onClickShop(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(Color.translate(CraftOfClans.config.getString("shop.structures-gui.title")))) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) {
				return;
			}
			if (e.getWhoClicked() instanceof Player) {
				Player p = (Player) e.getWhoClicked();
				InventoryShopBuild shopbuild = new InventoryShopBuild(e.getCurrentItem(), new PlayerStored(p));
				shopbuild.getItemResponse();
				
				// Se il player è shiftato un
				if(!e.isShiftClick()) {
					p.closeInventory();
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClickStructures(InventoryClickEvent e) {
		// StructuresGUI
		if (e.getWhoClicked() instanceof Player) {
			if (isInventoryStructures(e.getView().getTitle())) {
				e.setCancelled(true);
				if (StructuresGUI.containsKey(e.getWhoClicked())) {
					StructuresId id = StructuresGUI.get(e.getWhoClicked());
					PlayerStored player = new PlayerStored((Player) e.getWhoClicked());
					if (e.getCurrentItem() == null)
						return;
					if (e.getCurrentItem().getItemMeta() == null)
						return;
					if (e.getCurrentItem().getItemMeta().getDisplayName() == null)
						return;
					StructuresGUIItem item = new StructuresGUIItem(e.getCurrentItem().getItemMeta().getDisplayName(), id, player);
					if (item.getResponse()) {
						StructuresGUI.remove(e.getWhoClicked());
						e.getWhoClicked().closeInventory();
					}
				}
			}
		}
	}

	/**
	 * Controllo se l'inventario è di una struttura
	 * 
	 * @return
	 */
	public boolean isInventoryStructures(String title) {

		// Nomi impostati in messages.yml per le strutture classiche
		for (String nome : CraftOfClansM.get().getConfigurationSection("structures").getKeys(false)) {
			if (CraftOfClansM.getString("structures." + nome) != null) {
				String corrent = Color.translate(CraftOfClansM.getString("structures." + nome));
				if (title.equals(corrent)) {
					return true;
				}
			}
		}

		// Nome delle decorazioni
		for (String nome : CraftOfClans.config.get().getConfigurationSection("shop.structures-core").getKeys(false)) {
			if (CraftOfClans.config.getString("shop.structures-core." + nome + ".display-name") != null) {
				String corrent = Color.translate(CraftOfClans.config.getString("shop.structures-core." + nome + ".display-name"));
				if (title.equals(corrent)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Controllo se è la GUI del guardiano 0.6.3
	 */
	@EventHandler
	public void onInventoryClickGuardian(InventoryClickEvent e) {
		if (e.getView().getTitle() == null)
			return;
		if (!e.getView().getTitle().equals(Color.translate(CraftOfClans.config.getString("guardian-gui.title")))) {
			return;
		}
		if (e.getWhoClicked() instanceof Player) {

			Player p = (Player) e.getWhoClicked();
			if (!InventoryListener.GuardianNPC.containsKey(p)) {
				return;
			}

			e.setCancelled(true);
			if (e.getCurrentItem() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {
				return;
			}

			PlayerStored player = new PlayerStored(p);
			GuardianGUI item = new GuardianGUI(e.getCurrentItem().getItemMeta().getDisplayName(), InventoryListener.GuardianNPC.get(p), player);

			if (item.getResponse()) {
				InventoryListener.GuardianNPC.remove(e.getWhoClicked());
				e.getWhoClicked().closeInventory();
			}
		}
	}

	/**
	 * Gui per l'eliminazione di una struttura
	 * 
	 * @param e
	 */
	@EventHandler
	public void onInventoryClickDelete(InventoryClickEvent e) {
		if (e.getView().getTitle() == null)
			return;
		if (!e.getView().getTitle().equals(Color.translate(CraftOfClansM.getString("messages.gui-confirm-delete-structure")))) {
			return;
		}
		if (e.getWhoClicked() instanceof Player) {

			Player p = (Player) e.getWhoClicked();

			e.setCancelled(true);
			if (e.getCurrentItem() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {
				return;
			}

			String cliccato = e.getCurrentItem().getItemMeta().getDisplayName();

			// Se ha cliccato si
			if (cliccato.equals(Color.translate(CraftOfClansM.getString("messages.gui-delete-structure-yes")))) {
				if (SchematicsHandler.structures_delete.containsKey(p)) {
					for (Map.Entry<Long, StructuresId> entry : SchematicsHandler.structures_delete.get(p).entrySet()) {
						Long timestamp = entry.getKey();
						long coolDownRimasto = (System.currentTimeMillis() - timestamp);

						if (coolDownRimasto <= (1000 * 60)) {
							entry.getValue().destroy();
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.structure-deleted")));
						}
					}
					SchematicsHandler.structures_delete.remove(p);
				}
			}

			e.getWhoClicked().closeInventory();
		}
	}

	/**
	 * GUI dentro il laboratorio
	 * 
	 * @param e
	 */
	@EventHandler
	public void onInventoryClickLab(InventoryClickEvent e) {

		if (e.getView().getTitle() == null)
			return;
		
		String nome_concat = CraftOfClansM.getString("messages.troops-name") + " " + CraftOfClansM.getString("structures.LABORATORY");
		if (!e.getView().getTitle().equals(Color.translate(nome_concat))) {
			return;
		}

		if (e.getWhoClicked() instanceof Player) {

			Player p = (Player) e.getWhoClicked();
			PlayerStored pstored = new PlayerStored(p);

			e.setCancelled(true);
			if (e.getCurrentItem() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {
				return;
			}

			String cliccato = e.getCurrentItem().getItemMeta().getDisplayName();

			for (String name : CraftOfClans.troops.get().getConfigurationSection("troops").getKeys(false)) {
				String titolo = Color.translate(CraftOfClans.troops.getString("troops." + name + ".display_name"));

				// Truppa cliccato
				if (titolo.equals(cliccato)) {

					if (pstored.hasTroop(name, 0)) {

						TroopsId trop = pstored.getTroop(name);

						if (!trop.hasNextLevel()) {
							e.getWhoClicked().closeInventory();
							return;
						}

						// Faccio l'upgrade
						TroopsId trop_new = new TroopsId(pstored, name, (pstored.getTroop(name).getLevel() + 1));
						if (trop_new.buy()) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.upgrade-troops-successfully").replace("%1%", titolo)));
						}

					} else {

						TroopsId trop = new TroopsId(pstored, name, 1);

						// Compro la prima volta la truppa
						if (trop.buy()) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.bought-troops-successfully").replace("%1%", titolo)));
						}
					}
				}
			}

			e.getWhoClicked().closeInventory();
		}
	}

	/**
	 * GUI dentro la caserma
	 * 
	 * @param e
	 */
	@EventHandler
	public void onInventoryClickBarracks(InventoryClickEvent e) {

		if (e.getView().getTitle() == null)
			return;
		
		String nome_concat = CraftOfClansM.getString("messages.troops-name") + " " + CraftOfClansM.getString("structures.BARRACKS");
		if (!e.getView().getTitle().equals(Color.translate(nome_concat))) {
			return;
		}

		if (e.getWhoClicked() instanceof Player) {

			Player p = (Player) e.getWhoClicked();
			PlayerStored pstored = new PlayerStored(p);

			e.setCancelled(true);
			if (e.getCurrentItem() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta() == null) {
				return;
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {
				return;
			}

			String cliccato = Color.translate(e.getCurrentItem().getItemMeta().getDisplayName());

			for (TroopsId truppa : pstored.getTroops()) {
				if (truppa.display_name.equals(cliccato)) {

					if (truppa.isReady()) {
						
						// Dò la truppa al player
						truppa.pickUp();
						
					} else if (truppa.isToTraning()) {
						
						truppa.traning();
					}
				}
			}
			
			e.getWhoClicked().closeInventory();
		}
	}
}
