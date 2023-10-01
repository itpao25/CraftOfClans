package it.itpao25.craftofclans.guardian;

import java.util.HashMap;

import org.bukkit.Location;

import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import net.citizensnpcs.api.npc.NPC;

public class GuardianNPC {
	
	public static HashMap<NPC, VillageId> npc_guardian = new HashMap<NPC, VillageId>();
	
	/**
	 * Salvo solo l'NPC e non lo spawno
	 * 
	 * @param structure
	 */
	public static GuardianVillage generateNPC(StructuresId structure, Integer livello) {
		Location centro = structure.getCuboid().getCenter();
		centro.setY(VillageSettings.getHeight(structure.getVillage()) + 1);
		centro.setX(centro.getX() + 3);
		centro.setZ(centro.getZ() + 2);
		
		VillageId villo = MapInfo.getVillage(centro);
		
		return saveNPC(villo, centro, livello);
	}
	
	public static GuardianVillage generateNPC(Location location, Integer livello) {
		VillageId villo = MapInfo.getVillage(location);
		return saveNPC(villo, location, livello);
	}
	
	/**
	 * Salvo l'NPC nuovo nel villaggio
	 * 
	 * @param villo
	 * @param location
	 * @return 
	 */
	public static GuardianVillage saveNPC(VillageId villo, Location location, Integer livello) {
		int conto = getIndexNPCs(villo);
		
		CraftOfClansData.get().createSection("villages." + villo.getIDescaped() + ".guardian." + conto);
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".guardian." + conto + ".coord", location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ());
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".guardian." + conto + ".liv", livello);
		if (conto == 1) {
			CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".guardian." + conto + ".is_first", "true");
		}
		CraftOfClansData.save();
		
		GuardianVillage svillage = new GuardianVillage(location, conto);
		return svillage;
	}

	/**
	 * Prendo l'incrementale dei guardiani per un villaggio
	 * @param id
	 * @return
	 */
	public static int getIndexNPCs(VillageId id) {
		if (CraftOfClansData.get().getConfigurationSection("villages." + id.getIDescaped() + ".guardian") == null) {
			CraftOfClansData.get().createSection("villages." + id.getIDescaped() + ".guardian");
			CraftOfClansData.save();
			return 1;
		}
		int conto = CraftOfClansData.get().getConfigurationSection("villages." + id.getIDescaped() + ".guardian").getKeys(false).size();
		if (conto == 0) {
			return 1;
		}
		int index = 1;
		int last = -1;
		for (String config : CraftOfClansData.get().getConfigurationSection("villages." + id.getIDescaped() + ".guardian").getKeys(false)) {
			if (index == conto) {
				last = Integer.parseInt(config);
				last = last + 1;
			}
			index++;
		}
		return last;
	}
	
	
}
