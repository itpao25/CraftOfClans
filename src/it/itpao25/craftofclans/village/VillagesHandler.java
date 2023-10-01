package it.itpao25.craftofclans.village;

import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;
import it.itpao25.craftofclans.structures.StructuresId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

public class VillagesHandler {

	// Villaggi registrati
	public static ArrayList<VillageId> villages = new ArrayList<VillageId>();
	
	// Villaggi da distruggere dopo aver fatto il comando
	public static HashMap<CommandSender, HashMap<Long, VillageId>> villages_to_destroy = new HashMap<CommandSender, HashMap<Long, VillageId>>();
	
	public boolean registers() {

		ArrayList<String> local = new ArrayList<String>();
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			local.addAll(StorageMySQLRead.getVillageNotAvailable());
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			local.addAll(StorageFlatRead.getVillageNotAvailable());
		}
		for(String variable : local) {
			villages.add(new VillageId(variable));
		}
		
		for (String villo : local) {

			VillageId svillo = new VillageId(villo);

			// Se non c'è ancora nessun guardiano
			if (svillo.getGuardians().size() == 0) {
				// Se c'è almeno una struttura
				if (svillo.getStructuresList().size() > 0) {
					for (Entry<StructuresId, String> id : svillo.getStructuresList().entrySet()) {
						if (id.getValue().equals("TOWNHALL")) {
							GuardianNPC.generateNPC(id.getKey(), 1);
						}
					}
				}
			}
		}

		LogHandler.log("Loaded " + villages.size() + " villages!");
		return true;
	}
}
