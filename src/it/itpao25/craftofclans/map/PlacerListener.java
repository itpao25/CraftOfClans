package it.itpao25.craftofclans.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

public class PlacerListener implements Runnable {
	public static HashMap<Player, ArrayList<Location>> blocks = new HashMap<>();
	public static HashMap<Player, String> eccezzioni = new HashMap<>();
	
	@Override
	public void run() {
		// Controllo tutti i giocatori che hanno l'item in mano
		for (Player p : _Number.getOnlinePlayers()) {
			
			// Rimuovo i blocchi precedenti
			for (Entry<Player, ArrayList<Location>> item : blocks.entrySet()) {
				if (item.getKey().equals(p)) {
					for(Location loc : item.getValue()) {
						BlockData original = loc.getBlock().getBlockData();
						item.getKey().sendBlockChange(loc, original);
					}
					blocks.remove(item.getKey());
				}
			}
			
			// Se sono nel mondo giusto
			if (p.getWorld().getName().equals("clansworld")) {
				if (p.getInventory().getItemInMainHand() != null) {
					
					ItemStack item_in_hand = p.getInventory().getItemInMainHand();
					
					if (item_in_hand.getItemMeta() != null && 
							item_in_hand.getItemMeta().getDisplayName() != null && 
									item_in_hand.getItemMeta().getLore() != null) {
						
						if (!eccezzioni.containsKey(p)) {
							Set<Material> material = null;
							Location loc = p.getTargetBlock(material, 10).getLocation();
							VillageId villo = MapInfo.getVillage(loc);
							if (loc.getY() == VillageSettings.getHeight(villo) - 1) {
								Placer.placeListener(item_in_hand, p, loc);
							}
						}
					}
				}
			}
		}
	}

}
