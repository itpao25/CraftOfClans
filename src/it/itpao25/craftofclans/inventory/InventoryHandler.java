package it.itpao25.craftofclans.inventory;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansClan;
import it.itpao25.craftofclans.tier.TierManager;
import it.itpao25.craftofclans.util.Color;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {
	private Player player;
	private String title;
	private Inventory inv;
	private InventoryEnum type;
	private Integer size = 0;

	public InventoryHandler(Player p, InventoryEnum type) {
		this.player = p;
		this.type = type;
		setTitle();
		createInventory();
	}

	public InventoryHandler(Player p, InventoryEnum type, Integer size) {
		this.player = p;
		this.type = type;
		this.size = size;
		setTitle();
		createInventory();
	}

	public InventoryHandler(Player p, InventoryEnum type, String title) {
		this.player = p;
		this.type = type;
		this.title = title;
		createInventory();
	}

	public boolean createInventory() {
		inv = Bukkit.createInventory(null, getInventorySize(), title);
		return false;
	}

	public boolean openInventory() {
		player.openInventory(inv);
		return false;
	}

	public boolean setItem(int index, ItemStack item) {
		inv.setItem(index, item);
		return false;
	}

	public boolean setItem(ItemStack item) {
		inv.addItem(item);
		return false;
	}

	/**
	 * Ritorno con la dimensione dell'inventario
	 * 
	 * @return
	 */
	public int getInventorySize() {
		if (this.size != 0) {
			return this.size;
		}
		int size = 0;
		if (type.equals(InventoryEnum.Sell)) {
			size = CraftOfClans.config.getInt("sell.gui-size") != 0 ? CraftOfClans.config.getInt("sell.gui-size") : 18;
		} else if (type.equals(InventoryEnum.Structures_GUI)) {
			size = CraftOfClans.config.getInt("structures-gui.size") != 0 ? CraftOfClans.config.getInt("structures-gui.size") : 18;
		} else if (type.equals(InventoryEnum.ClanWizard_GUI)) {
			size = 54;
		} else if(type.equals(InventoryEnum.Shop_build)) {
			size = CraftOfClans.config.getInt("shop.structures-gui.size") != 0 ? CraftOfClans.config.getInt("shop.structures-gui.size") : 18;
		} else {
			size = 9;
		}
		return size;
	}

	/**
	 * Imposto il titolo da dare alla gui
	 * 
	 * @return
	 */
	public boolean setTitle() {
		if (type.equals(InventoryEnum.Sell)) {
			this.title = Color.translate(CraftOfClans.config.getString("sell.gui-title"));
		} else if (type.equals(InventoryEnum.Shop_build)) {
			this.title = Color.translate(CraftOfClans.config.getString("shop.structures-gui.title"));
		} else if (type.equals(InventoryEnum.Tier_GUI)) {
			this.title = TierManager.getGUIName();
		} else if (type.equals(InventoryEnum.ClanWizard_GUI)) {
			this.title = Color.translate(CraftOfClansClan.getString("clans-settings.gui-wizard.title"));
		} else if (type.equals(InventoryEnum.Guardian_GUI)) {
			this.title = Color.translate(CraftOfClans.config.getString("guardian-gui.title"));
		} else {
			this.title = Color.translate("&c&lDefault title");
		}
		return true;
	}
}
