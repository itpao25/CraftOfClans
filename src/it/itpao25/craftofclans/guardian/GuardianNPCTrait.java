package it.itpao25.craftofclans.guardian;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.inventory.InventoryListener;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.village.VillageId;
import net.citizensnpcs.api.trait.Trait;

public class GuardianNPCTrait extends Trait {
	
	CraftOfClans plugin = null;
	public GuardianVillage stored_guardian = null;
	
	public GuardianNPCTrait() {
		super("GuardianNPCTrait");
		plugin = CraftOfClans.getInstance();
	}
	
	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
		if(event.getNPC() == this.getNPC()) {
			
			// Solo il primo NPC
			VillageId villo = new VillageId(stored_guardian.getVillage().getID());
			PlayerStored pstored_owner = new PlayerStored(villo.getOwnerID());
			Player p = event.getClicker();
			
			// Controllo se è il player proprietario del villaggio
			if(pstored_owner.isOnline()) {
				
				if(pstored_owner.get().equals(p)) {
					stored_guardian.openGUI(p);
					
					// Aggiungo alla lista delle GUI aperte
					if(InventoryListener.GuardianNPC.containsKey(p)) {
						InventoryListener.GuardianNPC.remove(p);
					}
					InventoryListener.GuardianNPC.put(p, stored_guardian);
				}
			}
		}
	}
	
	public void setGuardianVillage(GuardianVillage stored_guardian) {
		this.stored_guardian = stored_guardian;
	}
	
	// Run code when your trait is attached to a NPC.
	// This is called BEFORE onSpawn, so npc.getEntity() will return null
	// This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
	}

	// Run code when the NPC is despawned. This is called before the entity actually
	// despawns so npc.getEntity() is still valid.
	@Override
	public void onDespawn() {
	}

	// Run code when the NPC is spawned. Note that npc.getEntity() will be null
	// until this method is called.
	// This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {

	}

	// run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

}