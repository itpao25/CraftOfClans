package it.itpao25.craftofclans.structures_resource;

import it.itpao25.craftofclans.api.ResourceCollected;
import it.itpao25.craftofclans.api.ResourceUpdate;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.village.VillageCuboid;

import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class ResourceTask implements Runnable {

	@Override
	public void run() {
		for (Entry<StructuresId, VillageCuboid> entry : SchematicsHandler.structures_registred.entrySet()) {
			if (entry.getKey().getType().equals("GOLD_MINE") || entry.getKey().getType().equals("COLLECTOR_ELIXIR") || entry.getKey().getType().equals("DARK_ELIXIR_DRILL")) {

				// Eseguo l'evento
				ResourceUpdate event = new ResourceUpdate(entry.getKey());
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					continue;
				}

				boolean to_update = true;
				double resource = entry.getKey().getResources();

				// Se le risorse sono occupate
				if (resource >= entry.getKey().getCapacity()) {
					event.setCancelled(true);
					to_update = false;
				}

				// Se non c'è più spazio per aggiornare
				if ((resource + entry.getKey().getIncrementResource()) >= entry.getKey().getCapacity()) {
					entry.getKey().setResources(entry.getKey().getCapacity());
					event.setCancelled(true);
					to_update = false;
				}
				
				// Aggiorno le risorse
				if (to_update) {
					entry.getKey().setResources(resource + entry.getKey().getIncrementResource());
				}

				// Aggiorno gli hologrammi
				ResourceCollected event1 = new ResourceCollected(entry.getKey());
				Bukkit.getServer().getPluginManager().callEvent(event1);
			}
		}
		CraftOfClansData.save();
		CraftOfClansData.reload();
	}
}
