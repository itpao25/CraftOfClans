package it.itpao25.craftofclans.map;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

public class ExpandBorder {
	
	private Chunk chunk_daclaim;
	private VillageId villo;

	/**
	 * Espansione del bordo villaggio per evitare che venga claimato
	 * 
	 * @param villo         Villaggio del giocatore
	 * @param chunk_claimed Chunk del giocatore
	 */
	public ExpandBorder(VillageId villo, Chunk chunk_claimed) {
		this.chunk_daclaim = chunk_claimed;
		this.villo = villo;
	}

	public boolean register() {
		if (villo.isExistInData() == false) {
			villo.setInData();
		}
		if (setInData()) {
			return true;
		}
		return false;
	}

	/**
	 * Aggiungo il file nel file config.yml
	 * 
	 * @return
	 */
	private boolean setInData() {

		List<String> prima = CraftOfClansData.get().getStringList("villages." + this.villo.getIDescaped() + ".border");
		int x = chunk_daclaim.getX();
		int z = chunk_daclaim.getZ();
		prima.add(x + "_" + z);

		CraftOfClansData.get().set("villages." + this.villo.getIDescaped() + ".border", prima);
		CraftOfClansData.save();

		setTerrain();
		
		return true;
	}
	
	
	/**
	 * Tolgo l'erba dal chunk
	 * 
	 * @return
	 */
	private boolean setTerrain() {
		if (this.chunk_daclaim == null) {
			return false;
		}
		
		boolean erba_creare = CraftOfClans.config.getBoolean("generator.surface-grass");
		if (erba_creare) {
			Location loc1_1 = this.chunk_daclaim.getBlock(0, VillageSettings.getHeight(villo), 0).getLocation();
			Location loc2_2 = this.chunk_daclaim.getBlock(15, VillageSettings.getHeight(villo), 15).getLocation();
			WorldEditUtil.setBlock(VillageSettings.getAirMaterial(), loc1_1, loc2_2);
		}
		return true;
	}
}
