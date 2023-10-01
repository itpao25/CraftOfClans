package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.guardian.GuardianNPCTrait;

import org.bukkit.plugin.Plugin;

public class CitizenHandler {
	public static boolean enable = false;

	public void setup() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("Citizens");
		if (plugin == null) {
			LogHandler.log("Citizens is not installed");
			return;
		}

		LogHandler.log("Citizens is now enabled");

		// Registro il Trait GuardianNPCTrait
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(GuardianNPCTrait.class).withName("GuardianNPCTrait"));
		
		/*
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(CraftOfClans.getInstance(), new Runnable() {
			public void run() {
				// Rimuovo tutti gli NPC salvati nel mondo clansworld
				for (NPCRegistry register : CitizensAPI.getNPCRegistries()) {
					for (NPC npc : register.sorted()) {
						
						System.out.print(npc.getFullName());
						
						if (npc.getStoredLocation() != null) {
							System.out.print(npc.getStoredLocation().getWorld().getName());


							 * if (npc.getStoredLocation().getWorld().getName().equals("clansworld")) {
							 * npc.destroy(); }
						}
					}
				}
			}
		}, 2 * 20);
		*/

		enable = true;
	}
}	
