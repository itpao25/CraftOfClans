package it.itpao25.craftofclans.attack;

import java.util.ArrayList;
import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.PlayerAttackPrepare;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillagesHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FindVillage {

	private Player p;
	private ArrayList<VillageId> id = new ArrayList<VillageId>();

	public FindVillage(Player p) {
		this.p = p;
		find();
	}

	public boolean find() {
		CraftOfClans.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
			
			int pvillage_townhall = new PlayerStored(p).getVillage().getLevelTownHall();

			int max_range_village = Math.abs(AttackerManager.getRangeAttackTownhallMax());
			int min_range_village = Math.abs(AttackerManager.getRangeAttackTownhallMin()); // Converto in positivo

			@Override
			public void run() {

				PlayerStored player = new PlayerStored(p);
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.attack-search")));

				final PlayerAttackPrepare event = new PlayerAttackPrepare(p);
				Bukkit.getServer().getPluginManager().callEvent(event);

				for (VillageId villo : VillagesHandler.villages) {
					
					// Controllo se il villaggio è disponibile
					if (villo.isAvailable()) {
						event.setCancelled(true);
						continue;
					}
					
					// Controllo se il villaggio ha almeno un espansione ed una struttura
					// almeno una struttura, il municipio
					if (villo.getExpanses().size() == 0 && villo.getStructuresList().size() == 1) {
						event.setCancelled(true);
						continue;
					}
					if (villo.isOwner(player)) {
						event.setCancelled(true);
						continue;
					}
					if (villo.isAttacked()) {
						event.setCancelled(true);
						continue;
					}
					if (villo.isActiveScudo()) {
						event.setCancelled(true);
						continue;
					}

					// Controllo se il giocatore non è nello stesso clan
					if (player.hasClan()) {
						PlayerStored ptarget = new PlayerStored(villo.getOwnerID());
						if (ptarget.hasClan()) {
							if (player.getClan().equals(ptarget.getClan())) {
								continue;
							}
						}
					}

					// Controllo il livello del municipio
					int villo_leveltown = villo.getLevelTownHall(); // ID del villaggio candidato per l'attacco

					boolean found_village_compatibiliy = false;

					if (villo_leveltown != pvillage_townhall) {

						// Valori minimi
						int livello_differenza = Math.abs(villo_leveltown - pvillage_townhall);

						if (livello_differenza >= min_range_village) {
							found_village_compatibiliy = true;
						}

						// Valori massimi
						if (livello_differenza <= max_range_village) {
							found_village_compatibiliy = true;
						}

						if (!found_village_compatibiliy) {
							// Non può attaccarlo
							event.setCancelled(true);
							continue;
						}
					}

					if (!villo.isOwnerOnline()) {
						// Trovato villaggio libero
						// Aggiungo alla lista dei villaggi disponibili
						id.add(villo);
					}
				}

				if (id.size() == 0) {
					event.setCancelled(false);
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.attack-novillage")));
					return;
				}

				// Prendo un villaggio random
				int idx = _Number.random_int(0, id.size());
				VillageId villo = id.get(idx);
				new Attack(new PlayerStored(p), villo);
			}
		});
		return true;
	}
}
