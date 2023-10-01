package it.itpao25.craftofclans.guardian;

import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillagesHandler;

public class GuardianHandler {

	public boolean registers() {
		
		Integer guardiani = 0;
		for (VillageId svillo : VillagesHandler.villages) {
			// Se non c'è ancora nessun guardiano
			if (svillo.getGuardians().size() > 0) {
				for (GuardianVillage npc : svillo.getGuardians()) {
					npc.spawn();
					guardiani++;
				}
			}
		}
		LogHandler.log("Loaded " + guardiani + " guardians!");
		return true;
	}
}
