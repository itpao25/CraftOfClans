package it.itpao25.craftofclans.village;

import java.util.List;

import org.bukkit.Material;

import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.world.block.BlockTypes;

import it.itpao25.craftofclans.CraftOfClans;

@SuppressWarnings("deprecation")
public class VillageSettings {

	/**
	 * Altezza per la generazione delle strutture
	 * 
	 * @return
	 */
	public static int getHeight(VillageId village) {
		
		// Se è modalità freemode e prendo l'altezza dinamica
		if (CraftOfClans.freemode && village != null) {
			if (village.freemode_height != 0) {
				return village.freemode_height;
			}
		}
		
		// Se è impostata l'height
		if (CraftOfClans.config.getString("village.height") != null) {
			int number = CraftOfClans.config.getInt("village.height");
			if (number != 0) {
				return number;
			}
		}
		return 60;
	}

	public static int sizeX() {
		return 96;
	}

	public static int sizeZ() {
		return 96;
	}

	/**
	 * Materiale per la generazione del mondo
	 * 
	 * @return
	 */
	public static Material getMaterialGeneration() {
		String name = CraftOfClans.config.getString("generator.surface") != null ? CraftOfClans.config.getString("generator.surface") : "DIRT";
		if (Material.getMaterial(name) == null) {
			return Material.DIRT;
		}
		Material superficie = Material.getMaterial(name);
		return superficie;
	}

	/**
	 * Materiale per la generazione dell'espansione
	 * 
	 * @return
	 */
	public static RandomPattern getMaterialExpanded() {
		String string = CraftOfClans.config.getString("expand.floor");
		RandomPattern pat = new RandomPattern();

		if (string.contains(",")) {
			String[] list = string.split(",");
			for (String value : list) {
				pat.add(new BlockPattern(BlockTypes.get(value.toLowerCase()).getDefaultState()), 1);
			}
		} else {
			pat.add(new BlockPattern(BlockTypes.get(string.toLowerCase()).getDefaultState()), 1);
		}

		return pat;
	}

	/**
	 * Materiale per la generazione dell'espansione (solo ARIA)
	 * 
	 * @return
	 */
	public static RandomPattern getAirMaterial() {
		RandomPattern pat = new RandomPattern();
		pat.add(new BlockPattern(BlockTypes.AIR.getDefaultState()), 1);
		return pat;
	}
	
	/**
	 * Materiale per la generazione
	 * 
	 * @return
	 */
	public static RandomPattern convertMaterial(List<Material> material_obj) {
		RandomPattern pat = new RandomPattern();
		for(Material materiale : material_obj) {
			pat.add(new BlockPattern(BlockTypes.get(materiale.toString().toLowerCase()).getDefaultState()), 1);
		}
		return pat;
	}

	/**
	 * Possibilità ai giocatori di costruire nel proprio villaggio
	 * 
	 * @return
	 */
	public static boolean isPlayerCanBuildOwn() {
		if (CraftOfClans.config.getString("village.player-can-build") != null) {
			return CraftOfClans.config.getBoolean("village.player-can-build");
		}
		return false;
	}
}
