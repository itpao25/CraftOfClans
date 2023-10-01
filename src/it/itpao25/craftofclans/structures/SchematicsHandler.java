package it.itpao25.craftofclans.structures;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SchematicsHandler {
	private static File folder;

	public static HashMap<StructuresId, VillageCuboid> structures_registred = new HashMap<StructuresId, VillageCuboid>();
	public static HashMap<Player, HashMap<Long, StructuresId>> structures_delete = new HashMap<Player, HashMap<Long, StructuresId>>();
	public static HashMap<StructuresId, HashMap<String, Object>> structures_particle = new HashMap<StructuresId, HashMap<String, Object>>();
	
	public SchematicsHandler() {
		// Controllo se la cartella è stata creata
		// in caso, creo la cartella
		LogHandler.log("Initiating schematics...");
		File file = new File(CraftOfClans.getInstance().getDataFolder() + "/schematics");
		if (!file.exists()) {
			file.mkdirs();
		}
		SchematicsHandler.folder = file;
		// Controllo i file
		addDefault();
	}

	/**
	 * Ritorno con la cartella delle schematics
	 * 
	 * @return file Folder schematics
	 */
	public static File getFolder() {
		return SchematicsHandler.folder;
	}

	/**
	 * Controllo se una schematics esiste nella cartella "schematics"
	 * 
	 * @param schem_name
	 * @return
	 */
	public boolean isExits(String schem_name) {
		File file = new File(SchematicsHandler.getFolder() + File.separator + schem_name);
		if (!file.exists()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Aggiungo i file schematics la prima volta che avvia il plugin
	 * 
	 * @return
	 */
	public boolean addDefault() {
		
		String wall_corner_schem = CraftOfClans.config.getString("village.wall-corner-schematic");
		String wall_schem = CraftOfClans.config.getString("village.wall-schematic");
		String wall_door = CraftOfClans.config.getString("village.wall-door-schematic");
		
		String key_primaria = "shop.structures-core";
		for (String key : CraftOfClans.config.get().getConfigurationSection(key_primaria).getKeys(false)) {
			if (CraftOfClans.config.getString(key_primaria + "." + key + ".levels") != null) {
				for (String key2 : CraftOfClans.config.get().getConfigurationSection(key_primaria + "." + key + ".levels").getKeys(false)) {
					if (CraftOfClans.config.getString(key_primaria + "." + key + ".levels." + key2 + ".schematics-name") != null) {
						String name = CraftOfClans.config.getString(key_primaria + "." + key + ".levels." + key2 + ".schematics-name");
						LogHandler.log("Check schematic " + name);
						if (!isExits(name)) {
							LogHandler.error("schematic " + name + " does not exist!");
							if (isLocalStoraged(name)) {
								LogHandler.log("Saved the default schematic " + name);
							}
						}
					}
				}
			}
		}
		// Wall
		if (!isExits(wall_corner_schem)) {
			if (isLocalStoraged(wall_corner_schem)) {
				LogHandler.log("Saved the default schematic WALL_CORNER.schematic!");
			}
		}
		if (!isExits(wall_schem)) {
			if (isLocalStoraged(wall_schem)) {
				LogHandler.log("Saved the default schematic WALL.schematic!");
			}
		}
		if (!isExits(wall_door)) {
			if (isLocalStoraged(wall_door)) {
				LogHandler.log("Saved the default schematic WALL_DOOR.schematic!");
			}
		}
		return false;
	}

	/**
	 * Sposto il file da locale alla cartella schematics
	 * 
	 * @param name
	 * @return
	 */
	private boolean isLocalStoraged(String name) {

		InputStream i = CraftOfClans.getInstance().getResource("schematics/" + name);
		if (i == null) {
			return false;
		}
		File dir = new File(SchematicsHandler.getFolder() + "/" + name);
		try {
			FileUtils.copyInputStreamToFile(i, dir);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Con questa funzione registro le strutture presenti nel file expansor.yml
	 * 
	 * @return
	 */
	public boolean registerStructures() {
		if (CraftOfClansData.getString("villages") == null || CraftOfClansData.get().getConfigurationSection("villages") == null) {
			return false;
		}
		if (CraftOfClansData.get().getConfigurationSection("villages").getKeys(false).size() == 0) {
			return false;
		}
		for (String key : CraftOfClansData.get().getConfigurationSection("villages").getKeys(false)) {
			if (CraftOfClansData.get().getString("villages." + key + ".structures") == null || CraftOfClansData.get().getConfigurationSection("villages." + key + ".structures") == null) {
				continue;
			}
			for (String key2 : CraftOfClansData.get().getConfigurationSection("villages." + key + ".structures").getKeys(false)) {
				if (CraftOfClansData.getString("villages." + key + ".structures." + key2 + ".coord") != null) {
					String coordinate = CraftOfClansData.getString("villages." + key + ".structures." + key2 + ".coord");

					String[] finale = coordinate.split("_");

					// Min
					int min_x = Integer.parseInt(finale[0]);
					int min_y = Integer.parseInt(finale[1]);
					int min_z = Integer.parseInt(finale[2]);

					// Max
					int max_x = Integer.parseInt(finale[3]);
					int max_y = Integer.parseInt(finale[4]);
					int max_z = Integer.parseInt(finale[5]);

					// World
					World world = Bukkit.getServer().getWorld("clansworld");

					// Struttura
					Location loc1 = new Location(world, min_x, min_y, min_z);
					Location loc2 = new Location(world, max_x, max_y, max_z);

					if (loc1 != null && loc2 != null) {
						
						VillageCuboid struttura = new VillageCuboid(loc1, loc2);
						VillageId villo = MapInfo.getVillage(struttura.getCenter());
						StructuresId structures = new StructuresId(villo, Integer.parseInt(key2));
						
						// Se devo spawnare gli NPC della struttura
						structures.spawnNPC();
						// Se devo spawnare le particelle
						structures.spawnParticle();
						
						structures_registred.put(structures, struttura);

					} else {
						LogHandler.error("Error to load the structures villages." + key + ".structures." + key2);
					}
				}
			}
		}
		LogHandler.log("Loaded " + structures_registred.size() + " structures!");
		return true;
	}

	/**
	 * Controllo se nella posizione è presente una struttura
	 * 
	 * @param p
	 * @param loc
	 * @return
	 */
	public static boolean hasStructures(PlayerStored p, Location loc) {
		for (Entry<StructuresId, VillageCuboid> item : structures_registred.entrySet()) {
			if (item.getValue().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				p.sendMessage("ID " + item.getKey().getId() + " TIPO " + item.getKey().getType() + " LIVELLO " + item.getKey().getLevel() + " RISORSE " + item.getKey().getResources());
				return true;
			}
		}
		return false;
	}

	/**
	 * Controllo se nella posizione è presente una struttura
	 * 
	 * @param loc
	 * @return
	 */
	public static boolean hasStructures(Location loc) {
		for (Entry<StructuresId, VillageCuboid> item : structures_registred.entrySet()) {
			if (item.getValue().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				return true;
			}
		}
		return false;
	}

	public static int getIndexStructures(VillageId id) {
		if (CraftOfClansData.get().getConfigurationSection("villages." + id.getIDescaped() + ".structures") == null) {
			CraftOfClansData.get().createSection("villages." + id.getIDescaped() + ".structures");
			return 1;
		}
		int conto = CraftOfClansData.get().getConfigurationSection("villages." + id.getIDescaped() + ".structures").getKeys(false).size();
		if (conto == 0) {
			return 1;
		}
		int index = 1;
		int last = -1;
		for (String config : CraftOfClansData.get().getConfigurationSection("villages." + id.getIDescaped() + ".structures").getKeys(false)) {
			if (index == conto) {
				last = Integer.parseInt(config);
				last = last + 1;
			}
			index++;
		}
		return last;
	}
	
	public static int getMaxStructuresByType(VillageId villo, String type) {
		// Numero massimo generale
		int max_structures = CraftOfClans.config.getString("shop.structures-core." + type + ".max-for-eachvillage") != null ? CraftOfClans.config.getInt("shop.structures-core." + type + ".max-for-eachvillage") : 0;
		if (max_structures != 0) {
			return max_structures;
		}

		// Se esistono limiti in base al livello del municipio
		if (CraftOfClans.config.getString("shop.structures-core." + type + ".limit-based-townhall-level") != null) {
			int villo_lvl = villo.getLevelTownHall();
			for (String key : CraftOfClans.config.get().getConfigurationSection("shop.structures-core." + type + ".limit-based-townhall-level").getKeys(false)) {
				if (key.equals(villo_lvl + "")) {
					int limit_townontownhall = Integer.parseInt(CraftOfClans.config.getString("shop.structures-core." + type + ".limit-based-townhall-level." + key));
					return limit_townontownhall;
				}
			}
		}
		return -1;
	}
}
