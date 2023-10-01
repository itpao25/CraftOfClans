package it.itpao25.craftofclans.clans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import it.itpao25.craftofclans.clans.ClansUIItem.ClansUIItemType;
import it.itpao25.craftofclans.config.CraftOfClansClan;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClansUIListeners implements Listener {

	private static HashMap<Player, HashMap<Integer, ClansUIItem>> items_player = new HashMap<>();
	static HashMap<Player, HashMap<ClansUIItemType, String>> settings_players = new HashMap<>();
	private static HashMap<Player, InventoryHandler> last_guis = new HashMap<>();

	/**
	 * Faccio il render del gui per il giocatore
	 */
	public static void getRender(Player p) {

		// Controllo se la sua GUI è già salvata
		if (items_player.containsKey(p)) {
			getReasumedRender(p);
			return;
		}

		HashMap<ClansUIItemType, String> setting_old = new HashMap<>();
		setting_old.put(ClansUIItemType.SET_TYPE, ClansTypes.ALL_CAN_JOIN.toString());
		if (!settings_players.containsKey(p)) {
			settings_players.put(p, setting_old);
		}

		InventoryHandler inv = new InventoryHandler(p, InventoryEnum.ClanWizard_GUI);
		HashMap<Integer, ClansUIItem> items = new HashMap<>();
		int slot_fall = 0;

		for (String string : CraftOfClansClan.get().getConfigurationSection("clans-settings.gui-wizard.items").getKeys(false)) {
			if (CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".title") != null) {
				ItemStack item_corrent;
				
				String slot = CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".slot") != null ? CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".slot") : "";
				String material = CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".material");
				
				item_corrent = new ItemStack(Material.getMaterial(material));
				
				String type = CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".type") != null ? CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".type") : "NULL";

				ItemMeta item_corrent_meta = item_corrent.getItemMeta();
				item_corrent_meta.setDisplayName(Color.translate(CraftOfClansClan.getString("clans-settings.gui-wizard.items." + string + ".title")));
				List<String> list_lore = new ArrayList<String>();

				if (settings_players.containsKey(p)) {
					HashMap<ClansUIItemType, String> hosts = settings_players.get(p);
					if (hosts.containsKey(ClansUIItemType.valueOf(type))) {
						list_lore.add(Color.translate("&bCurrent: " + hosts.get(ClansUIItemType.valueOf(type))));
					}
				}

				item_corrent_meta.setLore(list_lore);
				item_corrent.setItemMeta(item_corrent_meta);
				ClansUIItem uiItem = new ClansUIItem(item_corrent, ClansUIItemType.valueOf(type));

				if (slot.contains(",")) {

					String[] explodo_slot = slot.split(",");
					for (String slot_current : explodo_slot) {
						int slot_int_current = Integer.parseInt(slot_current);
						items.put(slot_int_current, uiItem);
						slot_fall++;
					}
				} else {

					int slot_int_current = _Number.isNumero(slot) ? Integer.parseInt(slot) : slot_fall;
					items.put(slot_int_current, uiItem);
					slot_fall++;
				}
			}
		}

		for (Entry<Integer, ClansUIItem> item : items.entrySet()) {
			inv.setItem(item.getKey(), item.getValue().getItem());
		}

		// Slot vuote - riempimento
		String item_slot_empty = CraftOfClansClan.getString("clans-settings.gui-wizard.empty-slots") != null ? CraftOfClansClan.getString("clans-settings.gui-wizard.empty-slots") : "0";
		ItemStack item_empty = new ItemStack(Material.getMaterial(item_slot_empty));
		
		for (int i = 0; i < 54; i++) {
			if (items.containsKey(i) == false) {
				items.put(i, new ClansUIItem(item_empty, ClansUIItemType.valueOf("NULL")));
				inv.setItem(i, item_empty);
			}
		}
		items_player.put(p, items);
		// Apro l'inventario
		inv.openInventory();
		last_guis.put(p, inv);
	}

	/**
	 * Ripristino GUI chiusa
	 */
	public static void getReasumedRender(Player p) {
		if (items_player.containsKey(p)) {
			InventoryHandler inv = new InventoryHandler(p, InventoryEnum.ClanWizard_GUI);
			for (Entry<Integer, ClansUIItem> item : items_player.get(p).entrySet()) {
				inv.setItem(item.getKey(), item.getValue().getItem());
			}
			inv.openInventory();
			last_guis.put(p, inv);
		}
	}

	/**
	 * Imposto il lore dell'item
	 * 
	 * @param p
	 * @param type
	 * @param string
	 */
	public static void setReasumedRenderItem(Player p, ClansUIItemType type, String string) {
		if (items_player.containsKey(p)) {
			for (Entry<Integer, ClansUIItem> item : items_player.get(p).entrySet()) {
				if (item.getValue().getType().equals(type)) {

					ItemStack item_current = item.getValue().getItem();
					ItemMeta meta_current = item_current.getItemMeta();

					List<String> lores_current = meta_current.getLore() != null ? meta_current.getLore() : new ArrayList<String>();
					if (lores_current.size() > 0) {
						lores_current.set(0, Color.translate("&bCurrent: " + string));
					} else {
						lores_current.add(Color.translate("&bCurrent: " + string));
					}

					meta_current.setLore(lores_current);
					item_current.setItemMeta(meta_current);

					ClansUIItem uiItem = new ClansUIItem(item_current, type);
					HashMap<Integer, ClansUIItem> items = new HashMap<>();
					items.putAll(items_player.get(p));
					items.put(item.getKey(), uiItem);
					items_player.put(p, items);
				}
			}
		}
	}

	public void setTypeChanger(Inventory inv, Player p) {
		if (items_player.containsKey(p)) {
			String new_type = "";
			String current_value = ClansTypes.ALL_CAN_JOIN.toString();

			HashMap<ClansUIItemType, String> setting_old = new HashMap<>();

			if (!settings_players.containsKey(p)) {
				settings_players.put(p, setting_old);
			}

			for (Entry<ClansUIItemType, String> settings : settings_players.get(p).entrySet()) {
				if (settings.getKey().equals(ClansUIItemType.SET_TYPE)) {
					if (settings.getValue() != "") {
						current_value = settings.getValue();
					}
				}
			}

			if (current_value.equals(ClansTypes.ALL_CAN_JOIN.toString())) {
				new_type = ClansTypes.JOIN_WITH_INVITE.toString();
			} else if (current_value.equals(ClansTypes.JOIN_WITH_INVITE.toString())) {
				new_type = ClansTypes.ALL_CAN_JOIN.toString();
			}
			setReasumedRenderItem(p, ClansUIItemType.SET_TYPE, new_type);
			setting_old.putAll(settings_players.get(p));
			setting_old.put(ClansUIItemType.SET_TYPE, new_type);
			settings_players.put(p, setting_old);

			for (Entry<Integer, ClansUIItem> item : items_player.get(p).entrySet()) {
				if (item.getValue().getType().equals(ClansUIItemType.SET_TYPE)) {
					last_guis.get(p).setItem(item.getKey(), item.getValue().getItem());
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (e.getView().getTitle() == null)
			return;
		if (e.getView().getTitle().equals(Color.translate(CraftOfClansClan.getString("clans-settings.gui-wizard.title")))) {
			e.setCancelled(true);
		} else {
			return;
		}

		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();

			if (items_player.containsKey(p)) {
				HashMap<Integer, ClansUIItem> items = items_player.get(p);
				int slot_clicked = e.getRawSlot();

				if (items.containsKey(slot_clicked)) {
					ClansUIItemType type = items.get(slot_clicked).getType();
					switch (type) {
					case BACK:
						p.closeInventory();
						break;
					case NULL:
						// NULLA
						break;
					case SET_NAME:

						ClansUIPromptAdd uiPrompt = new ClansUIPromptAdd();
						uiPrompt.addUser(p, ClansUIItemType.SET_NAME);

						p.closeInventory();

						break;
					case SET_TYPE:

						setTypeChanger(p.getInventory(), p);

						break;
					case SET_MIN_TROPHIES:

						ClansUIPromptAdd uiPrompt1 = new ClansUIPromptAdd();
						uiPrompt1.addUser(p, ClansUIItemType.SET_MIN_TROPHIES);

						p.closeInventory();

						break;
					case SET_DESC:

						ClansUIPromptAdd uiPrompt2 = new ClansUIPromptAdd();
						uiPrompt2.addUser(p, ClansUIItemType.SET_DESC);

						p.closeInventory();

						break;
					case SUBMIT:

						// SUBMIT
						if (submit(p)) {
							p.closeInventory();
						} else {
							p.sendMessage(Color.message(CraftOfClansM.getString("clan.message-invalid-params")));
						}

						break;
					}
				}
			}
		}
	}

	/**
	 * Metodo finale per la creazione del clan
	 * 
	 * @param p
	 * @return
	 */
	public boolean submit(Player p) {

		HashMap<ClansUIItemType, String> setting_old = new HashMap<>();

		if (!settings_players.containsKey(p)) {
			settings_players.put(p, setting_old);
		}
		setting_old.putAll(settings_players.get(p));

		// Parametri default
		String name = null;
		int min_trophies = -1;
		ClansTypes type = null;
		String desc = null;

		// Controllo le impostazioni
		if (!setting_old.get(ClansUIItemType.SET_NAME).equals("") && setting_old.get(ClansUIItemType.SET_NAME) != null) {
			name = setting_old.get(ClansUIItemType.SET_NAME);
		}
		
		if (!setting_old.get(ClansUIItemType.SET_MIN_TROPHIES).equals("") && _Number.isNumero(setting_old.get(ClansUIItemType.SET_MIN_TROPHIES)) && Integer.parseInt(setting_old.get(ClansUIItemType.SET_MIN_TROPHIES)) >= 0
				&& Integer.parseInt(setting_old.get(ClansUIItemType.SET_MIN_TROPHIES)) < 5000) {
			min_trophies = Integer.parseInt(setting_old.get(ClansUIItemType.SET_MIN_TROPHIES));
		}
		
		if (!setting_old.get(ClansUIItemType.SET_TYPE).equals("") && setting_old.get(ClansUIItemType.SET_TYPE) != null && ClansTypes.valueOf(setting_old.get(ClansUIItemType.SET_TYPE)) != null) {
			type = ClansTypes.valueOf(setting_old.get(ClansUIItemType.SET_TYPE));
		}
		
		if (setting_old.get(ClansUIItemType.SET_DESC) != null && !setting_old.get(ClansUIItemType.SET_DESC).equals("")) {
			desc = setting_old.get(ClansUIItemType.SET_DESC);
		}
		
		if (name != null && min_trophies != -1 && type != null) {
			ClansManager.createClan(name, desc, type, min_trophies, p);
			return true;
		}
		return false;
	}

	/**
	 * Reset settings del giocatore
	 * 
	 * @param p
	 */
	public static void resetToPlayer(Player p) {
		items_player.remove(p);
		settings_players.remove(p);
		last_guis.remove(p);
	}

	public static HashMap<Player, Boolean> waiting_disband = new HashMap<>();

	/**
	 * Conferma per il disband di un clan
	 */
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if (waiting_disband.containsKey(player)) {
			event.setCancelled(true);
			
			waiting_disband.remove(player);
			
			if (event.getMessage().equals("yes")) {
				
				PlayerStored pstored1 = new PlayerStored(player.getUniqueId());
				
				ClanObject cojb = pstored1.getClan();
				
				// Rimuovo tutti i memberi
				for (PlayerStored pmember : cojb.getMembers()) {
					pmember.setIdClan(0);
					if (pmember.isOnline()) {
						pmember.sendMessage(Color.message(CraftOfClansM.getString("clan.message-disband-success")));
					}
				}
				
				// Disbando il clan
				cojb.disband();
				
			} else {
				player.sendMessage(Color.message(CraftOfClansM.getString("clan.message-confirm-disband-canceled")));
			}
		}
	}
}
