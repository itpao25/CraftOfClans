package it.itpao25.craftofclans.structures_resource;

import it.itpao25.craftofclans.api.ResourceUpdate;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.village.VillageCuboid;

import java.util.Map.Entry;

import org.bukkit.Bukkit;

public class ResourceTaskGems implements Runnable {

	@Override
	public void run() {
		for (Entry<StructuresId, VillageCuboid> entry : SchematicsHandler.structures_registred.entrySet()) {
			if (entry.getKey().getType().equals("GEMS_COLLECTOR")) {

				// Eseguo l'evento
				ResourceUpdate event = new ResourceUpdate(entry.getKey());
				Bukkit.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					continue;
				}
				double resource = entry.getKey().getResources();
				entry.getKey().setResources(resource + entry.getKey().getIncrementResource());
			}
		}
		CraftOfClansData.save();
		CraftOfClansData.reload();
	}
}
