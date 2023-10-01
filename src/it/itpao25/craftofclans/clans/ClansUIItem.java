package it.itpao25.craftofclans.clans;

import org.bukkit.inventory.ItemStack;

public class ClansUIItem {

	private ItemStack item;
	private ClansUIItemType type;

	public ClansUIItem(ItemStack item, ClansUIItemType type) {
		this.item = item;
		this.type = type;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public ClansUIItemType getType() {
		return this.type;
	}

	public enum ClansUIItemType {
		SET_NAME, SET_TYPE, SET_MIN_TROPHIES, SET_DESC, SUBMIT, BACK, NULL
	}
}
