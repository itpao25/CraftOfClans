package it.itpao25.craftofclans.map;

import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.village.VillageId;
import java.util.List;
import org.bukkit.Chunk;

public class ExpanderRegister {
	
	/**
	 * Controllo se un chunk è presente nella hashmap
	 * 
	 * @param chunk
	 * @return
	 */
	public static boolean isPresent(Chunk chunk) {
		int x = chunk.getX();
		int z = chunk.getZ();
		VillageId villo = MapInfo.getVillage(chunk.getBlock(0, 0, 0).getLocation());
		if (villo == null) {
			return false;
		}
		List<String> prima = CraftOfClansData.get().getStringList("villages." + villo.getIDescaped() + ".expanded");
		if (prima.contains(x + "_" + z)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Controllo se un chunk è una mura
	 * 
	 * @param chunk
	 * @return
	 */
	public static boolean isMuraExpanded(Chunk chunk) {
		int x = chunk.getX();
		int z = chunk.getZ();
		VillageId villo = MapInfo.getVillage(chunk.getBlock(0, 0, 0).getLocation());
		if (villo == null) {
			return false;
		}
		List<String> prima = CraftOfClansData.get().getStringList("villages." + villo.getIDescaped() + ".border");
		if (prima.contains(x + "_" + z)) {
			return true;
		}
		return false;
	}
}
