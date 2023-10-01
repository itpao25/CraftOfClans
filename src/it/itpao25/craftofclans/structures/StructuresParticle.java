package it.itpao25.craftofclans.structures;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;

import it.itpao25.craftofclans.attack.AttackListener;

import org.bukkit.World;

public class StructuresParticle implements Runnable {

	@Override
	public void run() {

		World clansworld = Bukkit.getWorld("clansworld");

		for (Entry<StructuresId, HashMap<String, Object>> item : SchematicsHandler.structures_particle.entrySet()) {

			// Controllo se la struttura è in un'attacco (bomba)
			if (item.getKey().getType().equals(StructuresEnum.BOMB.toString())) {
				if (AttackListener.difese.containsValue(item.getKey())) {
					continue;
				}
			}

			HashMap<String, Object> options = item.getValue();

			Location loc = (Location) options.get("location");
			Particle particle = (Particle) options.get("particle");

			Particle.DustOptions dustdata = null;
			if (options.containsKey("particle_data")) {
				dustdata = (DustOptions) options.get("particle_data");
			}

			if (dustdata != null) {
				clansworld.spawnParticle(particle, loc, 0, 0, 1, 0, 1, dustdata);
			} else {
				clansworld.spawnParticle(particle, loc, 0, 0, 0, 0);
			}

		}
	}
}
