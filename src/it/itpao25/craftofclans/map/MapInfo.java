package it.itpao25.craftofclans.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillagesHandler;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

public class MapInfo {

	public static List<Material> erbe_mappa = new ArrayList<>();
	
	/**
	 * Posizione del villaggio avendo la location
	 */
	public static VillageId getVillage(Location loc) {
		for (VillageId c : VillagesHandler.villages) {
			if (c.getCuboid().contains(loc)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Ritorno con la struttura in una determinata posizione
	 * 
	 * @param loc
	 * @return
	 */
	public static StructuresId getStructures(Location loc) {
		for (Entry<StructuresId, VillageCuboid> item : SchematicsHandler.structures_registred.entrySet()) {
			if (item.getValue().contains(loc)) {
				return item.getKey();
			}
		}
		return null;
	}

	public static Integer getTotalVillages() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getVillagesNum();
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getVillagesNum();
		}
		return 0;
	}

	/**
	 * Ritorno con il numero di strutture in quel chunk
	 * 
	 * @param chunk
	 * @return
	 */
	public static Integer getStructuresAtChunk(Chunk chunk) {
		Integer integer_return = 0;
		for (Entry<StructuresId, VillageCuboid> item : SchematicsHandler.structures_registred.entrySet()) {
			if(item.getKey().getType().equals(StructuresEnum.VILLAGE_WALL.toString()) || item.getKey().getType().equals(StructuresEnum.VILLAGE_GATE_WALL.toString())) {
				continue;
			}
			if (item.getValue().getLowerNE().getChunk().equals(chunk)) {
				integer_return++;
			}
		}
		return integer_return;
	}

	/**
	 * Controllo quante strutture di quel tipo ci sono nel villaggio
	 */
	public static Integer getStructuresAtVillageByType(String senum, VillageId village) {
		// Strutture
		Integer integer_return = 0;
		for (Entry<StructuresId, String> id : village.getStructuresList().entrySet()) {
			if (id.getKey().getType().equals(senum.toString())) {
				integer_return++;
			}
		}
		// Guardiani
		if (senum.equals(StructuresEnum.GUARDIAN.toString())) {
			integer_return = integer_return + village.getGuardians().size();
		}
		return integer_return;
	}

	/**
	 * Controllo quante strutture di quel tipo ci sono nel villaggio
	 */
	public static Integer getStructuresAtVillageByType(StructuresEnum senum, VillageId village) {
		return getStructuresAtVillageByType(senum.toString(), village);
	}

	public static List<Material> getErbaVillage() {
		List<Material> erbe = new ArrayList<>();
		List<String> erbe_lista = CraftOfClans.config.get().getStringList("generator.surface-grass-list");
		
		for (String value : erbe_lista) {
			erbe.add(Material.getMaterial(value));
		}
		return erbe;
	}

	/**
	 * Tolgo le risorse rompendo l'erba nel chunk espanso
	 * 
	 * @return
	 */
	public static String getElabGrassRemoval(PlayerStored sender) {

		double cost_gems = CraftOfClans.config.getString("expand.grass-removal-cost.cost_gems") != null ? CraftOfClans.config.getDouble("expand.grass-removal-cost.cost_gems") : 0;
		double cost_elixir = CraftOfClans.config.getString("expand.grass-removal-cost.cost_elixir") != null ? CraftOfClans.config.getDouble("expand.grass-removal-cost.cost_elixir") : 0;
		double cost_dark_elixir = CraftOfClans.config.getString("expand.grass-removal-cost.cost_dark_elixir") != null ? CraftOfClans.config.getDouble("expand.grass-removal-cost.cost_dark_elixir") : 0;
		double cost_gold = CraftOfClans.config.getString("expand.grass-removal-cost.cost_gold") != null ? CraftOfClans.config.getDouble("expand.grass-removal-cost.cost_gold") : 0;
		
		if (cost_gems > 0) {
			if (sender.hasGems(cost_gems) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", cost_gems + ""));
				return null;
			}
		}
		if (cost_elixir > 0) {
			if (sender.hasElixir(cost_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", cost_elixir + ""));
				return null;
			}
		}
		if (cost_gold > 0) {
			if (sender.hasGold(cost_gold) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", cost_gold + ""));
				return null;
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.hasElixirNero(cost_dark_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", cost_dark_elixir + ""));
				return null;
			}
		}

		String costo_pagato = "";
		// Rimuovo le risorse
		if (cost_gems > 0) {
			if (sender.removeGems(cost_gems)) {
				costo_pagato += Color.translate("&c" + _Number.showNumero(cost_gems) + " " + CraftOfClansM.getString("resources.GEMS")) + " ";
			}
		}
		if (cost_elixir > 0) {
			if (sender.removeElixir(cost_elixir)) {
				costo_pagato += Color.translate("&c" + _Number.showNumero(cost_elixir) + " " + CraftOfClansM.getString("resources.ELIXIR")) + " ";
			}
		}
		if (cost_gold > 0) {
			if (sender.removeGold(cost_gold)) {
				costo_pagato += Color.translate("&c" + _Number.showNumero(cost_gold) + " " + CraftOfClansM.getString("resources.GOLD")) + " ";
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.removeElixirNero(cost_dark_elixir)) {
				costo_pagato += Color.translate("&c" + _Number.showNumero(cost_dark_elixir) + " " + CraftOfClansM.getString("resources.DARK_ELIXIR")) + " ";
			}
		}

		return costo_pagato;
	}
	
	/**
	 * Guardo verso un punto (utile per /coc attack)
	 * @param loc
	 * @param lookat
	 * @return
	 */
	public static Location lookAt(Location loc, Location lookat) {

		// Clone the loc to prevent applied changes to the input loc
		loc = loc.clone();

		// Values of change in distance (make it relative)
		double dx = lookat.getX() - loc.getX();
		double dy = lookat.getY() - loc.getY();
		double dz = lookat.getZ() - loc.getZ();

		// Set yaw
		if (dx != 0) {
			// Set yaw start value based on dx
			if (dx < 0) {
				loc.setYaw((float) (1.5 * Math.PI));
			} else {
				loc.setYaw((float) (0.5 * Math.PI));
			}
			loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
		} else if (dz < 0) {
			loc.setYaw((float) Math.PI);
		}

		// Get the distance from dx/dz
		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

		// Set pitch
		loc.setPitch((float) -Math.atan(dy / dxz));

		// Set values, convert to degrees (invert the yaw since Bukkit uses a different
		// yaw dimension format)
		loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
		loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

		return loc;
	}
}
