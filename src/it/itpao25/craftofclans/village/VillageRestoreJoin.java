package it.itpao25.craftofclans.village;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;

public class VillageRestoreJoin implements Runnable {

	private VillageId villo;

	public VillageRestoreJoin(VillageId id) {
		this.villo = id;
	}

	@Override
	public void run() {

		// Rimuovo le protezioni dei blocchi
		deleteWallsProtection();

		// Rigenero le schematics
		regenSchematics();
		
		// Controllo i bordi di vetro rimasti dagli attacchi
		checkCornersGlass();
	}
	
	/**
	 * Rimuovo tutte le protezioni dei muri
	 */
	private void deleteWallsProtection() {
		for (Entry<StructuresId, String> id : this.villo.getStructuresList().entrySet()) {
			if (id.getKey().getType().equals(StructuresEnum.VILLAGE_WALL.toString()) || id.getKey().getType().equals(StructuresEnum.VILLAGE_GATE_WALL.toString())) {

				for (int i = 1; i < 6; i++) {
					Location blocco_loc = id.getKey().getCuboid().getCenter();
					blocco_loc.setY(VillageSettings.getHeight(this.villo) + i);

					Block block = blocco_loc.getWorld().getBlockAt(blocco_loc);
					if (block.getType().equals(Material.BARRIER)) {
						block.setType(Material.AIR);
					}
				}
			}
		}
	}

	/**
	 * Rigenero le strutture disattivate (ad esempio le mura)
	 */
	private void regenSchematics() {
		for (Entry<StructuresId, String> id : this.villo.getStructuresList().entrySet()) {
			if (id.getKey().is_disabled) {
				id.getKey().regenSchematic();
				id.getKey().setDisabled(false);
			}
		}
	}

	/**
	 * Controllo se sono rimasti dei blocchi di vetro da un attacco (bugfix)
	 */
	private void checkCornersGlass() {
		
		for (Entry<StructuresId, String> id : this.villo.getStructuresList().entrySet()) {
			// Strutture che non hanno blocchi di disattivazione
			if (id.getValue().equals(StructuresEnum.BOMB.toString()) || id.getValue().equals(StructuresEnum.SKELETON_TRAP.toString()) || id.getValue().equals(StructuresEnum.VILLAGE_WALL.toString()) || id.getValue().equals(StructuresEnum.VILLAGE_GATE_WALL.toString())) {
				continue;
			}
			
			if (id.getKey().isDecoration()) {
				continue;
			}
			
			Location loc1 = id.getKey().getCuboid().getFace(CuboidDirection.Down).corners()[0].getLocation();
			Location loc2 = id.getKey().getCuboid().getFace(CuboidDirection.Down).corners()[1].getLocation();
			Location loc3 = id.getKey().getCuboid().getFace(CuboidDirection.Down).corners()[5].getLocation();
			Location loc4 = id.getKey().getCuboid().getFace(CuboidDirection.Down).corners()[6].getLocation();

			// Location finali
			loc1.setY(VillageSettings.getHeight(this.villo));
			loc2.setY(VillageSettings.getHeight(this.villo));
			loc3.setY(VillageSettings.getHeight(this.villo));
			loc4.setY(VillageSettings.getHeight(this.villo));
			
			if(loc1.getBlock().getType().toString().contains("glass")) {
				loc1.getBlock().setType(Material.AIR);
			}
			if(loc2.getBlock().getType().toString().contains("glass")) {
				loc2.getBlock().setType(Material.AIR);
			}
			if(loc3.getBlock().getType().toString().contains("glass")) {
				loc3.getBlock().setType(Material.AIR);
			}
			if(loc4.getBlock().getType().toString().contains("glass")) {
				loc4.getBlock().setType(Material.AIR);
			}
		}
	}
}
