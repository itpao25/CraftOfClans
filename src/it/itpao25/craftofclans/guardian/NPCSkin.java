package it.itpao25.craftofclans.guardian;

import it.itpao25.craftofclans.config.CraftOfClansSkin;
import net.citizensnpcs.api.npc.NPC;

public class NPCSkin {

	/**
	 * Controllo e prendo la skin dal config skins.yml per caricarlo all'NPC
	 * 
	 * @param structure_name
	 * @return
	 */
	public static boolean getSkinFromConfig(NPC npc, String structure_name) {
		
		if (CraftOfClansSkin.getString("skins." + structure_name) != null) {
			String texture = CraftOfClansSkin.getString("skins." + structure_name + ".texture");
			String signature = CraftOfClansSkin.getString("skins." + structure_name + ".signature");
			
			npc.getOrAddTrait(net.citizensnpcs.trait.SkinTrait.class).setSkinPersistent(npc.getId() + "", signature, texture);
			
			return true;
		}
		return false;
	}
}
