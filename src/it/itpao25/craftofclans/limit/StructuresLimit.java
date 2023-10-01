package it.itpao25.craftofclans.limit;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.StructuresBuild;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.village.VillageId;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StructuresLimit implements Listener {
	@EventHandler
	public void onBuild(StructuresBuild event) {
		
		if(!event.getStructure().equals(StructuresEnum.VILLAGE_WALL) && !event.getStructure().equals(StructuresEnum.VILLAGE_GATE_WALL)) {
			if (CraftOfClans.config.getString("limits.limit-per-expand-enable") != null) {
				if (CraftOfClans.config.getBoolean("limits.limit-per-expand-enable")) {
					int massimo = CraftOfClans.config.getString("limits.limit-per-expand-value") != null ? CraftOfClans.config.getInt("limits.limit-per-expand-value") : 0;
					if (massimo != 0) {
						// Gestione dei limiti per le strutture in un expansione (chunk)
						Chunk chunk = event.getLocation().getChunk();
						int numero = MapInfo.getStructuresAtChunk(chunk);
						if (numero >= massimo) {
							event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.limit-structures-per-expand")));
							event.setCancelled(true);
						}
					}
				}
			}
		}
		
		// Controllo se è stato superato il numero per la struttura
		String suid; 
		if (!event.getType().toString().equals(StructuresEnum.DECORATION.toString())) {
			suid = event.getType().toString();
		} else {
			suid = event.getType().type_struttura;
		}
		
		VillageId villaggio = MapInfo.getVillage(event.getLocation());
		
		int max_structures = CraftOfClans.config.getString("shop.structures-core." + suid + ".max-for-eachvillage") != null ? CraftOfClans.config.getInt("shop.structures-core." + suid + ".max-for-eachvillage") : 0;
		if (max_structures != 0) {
			int numero1 = MapInfo.getStructuresAtVillageByType(suid, villaggio);
			if (numero1 >= max_structures) {
				event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.shop-max-structures-reach").replace("%1%", max_structures + "")));
				event.setCancelled(true);
			}
		}
		
		// Se esistono limiti in base al livello del municipio
		if(CraftOfClans.config.getString("shop.structures-core." + suid + ".limit-based-townhall-level") != null) {
			int villo_lvl = villaggio.getLevelTownHall();
			int numero1 = MapInfo.getStructuresAtVillageByType(suid, villaggio);
			
			for(String key : CraftOfClans.config.get().getConfigurationSection("shop.structures-core." + suid + ".limit-based-townhall-level").getKeys(false)) {
				if(key.equals(villo_lvl + "")) {
					int limit_townontownhall = Integer.parseInt(CraftOfClans.config.getString("shop.structures-core." + suid + ".limit-based-townhall-level."+ key));
					if (numero1 >= limit_townontownhall) {
						event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.townhall-required-upgrade-toupdate")));
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
