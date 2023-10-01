package it.itpao25.craftofclans.tier;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.util.Color;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TierListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		if (e.getView().getTitle().equalsIgnoreCase(TierManager.getGUIName())) {
			// Controllo se il giocatore ha già aquistato il tier oppure no
			e.setCancelled(true);
			if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || e.getCurrentItem().getItemMeta().getDisplayName() == null) {
				return;
			}
			String title = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
			TierObject object = new TierObject(title, p);
			if (object.exits()) {
				if (object.hasBought()) {
					// Il giocatore ha già comprato la Tier
					object.tpPlayer();
				} else {

					// Controllo se ha precedenti da controllare
					if (!object.hasRequirements()) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-bought-failed-requirements").replace("%1%", object.getRequirements())));
						return;
					}

					// Compro la Tier per il giocatore
					if (object.buy()) {
						if (object.addPlayer()) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-bought-success").replace("%1%", title)));
						}
						p.closeInventory();
						return;
					}
				}
			}
		}
	}
}
