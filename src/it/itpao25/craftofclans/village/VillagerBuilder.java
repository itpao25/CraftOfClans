package it.itpao25.craftofclans.village;

//import java.util.HashMap;

//import it.itpao25.craftofclans.CraftOfClans;

import java.util.Date;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.Structures;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VillagerBuilder {

	private Player player;
	// HashMap che contiene i cooldowns degli utenti
	// private HashMap<String, Long> cooldowns = new HashMap<String, Long>();

	public VillagerBuilder(Player player) {
		this.player = player;
		create();
	}

	/**
	 * Creo il villaggio
	 * 
	 * @return
	 */
	private boolean create() {

		// Tempo di inizio per la creazione
		final long start_time = new Date().getTime();
		final PlayerStored p = new PlayerStored(player);

		// Controlllo se il giocatore esiste nel database
		if (!p.isExist()) {
			return false;
		}

		// Controllo se il giocatore ha già un villaggio
		if (p.hasVillage()) {
			player.sendMessage(Color.message(CraftOfClansM.getString("messages.already-has-village")).replace("%1%", "/coc tp"));
			return false;
		}
		
		LogHandler.log("Looking for the first village available...");
		p.sendMessage(Color.message(CraftOfClansM.getString("messages.village-creating-progress")));
		
		// Cerco il primo villaggio disponibile
		CraftOfClans.getInstance().getServer().getScheduler().runTaskAsynchronously(CraftOfClans.getInstance(), new Runnable() {
			@Override
			public void run() {
				loop: for (int i = 1; i < 50000; i++) {
					for (int x1 = -i; x1 <= i; x1++) {
						for (int z1 = -i; z1 <= i; z1++) {
							if (x1 != 0 && z1 != 0) {
								
								Chunk chunkRandom = Bukkit.getWorld("clansworld").getChunkAt(x1, z1);
								if(!chunkRandom.isLoaded()) {
									// Alzo il chunk se non è caricato
									chunkRandom.load(true);
								}
								
								int x = chunkRandom.getBlock(7, VillageSettings.getHeight(null), 7).getX();
								int z = chunkRandom.getBlock(7, VillageSettings.getHeight(null), 7).getZ();
								
								final VillageId villo = new VillageId(x, z);
								if (!villo.isAvailable()) {
									continue;
								}
								
								Location loc = new Location(p.get().getWorld(), x, VillageSettings.getHeight(villo), z);
								VillageCuboid cuboid = new VillageCuboid(loc, loc.clone().add(0, 1, 0));
								VillageCuboid cuboid_tocheck = cuboid.outset(CuboidDirection.Horizontal, VillageSettings.sizeX() / 2 + 10); // blocchi per lato: 128
								
								boolean is_disponibile = true;
								
								// Controllo se c'è posto nell'area
								for (Chunk chunk : cuboid_tocheck.getChunks()) {
									
									Block block = chunk.getBlock(7, loc.getBlockY(), 7);
									
									VillageId villo_inside = new VillageId(block.getLocation().getBlockX(), block.getLocation().getBlockZ(), true);
									if (!villo_inside.isAvailable()) {
										is_disponibile = false;
									}
								}
								
								if (!is_disponibile) {
									continue;
								}

								LogHandler.log("Register new village: " + villo.getID());
								if (villo.register(p)) {
									Structures strutture = villo.getStructures();
									// Costruisco il municipio
									Bukkit.getScheduler().runTask(CraftOfClans.getInstance(), strutture.getMunicipio(p, villo, true, start_time));
									break loop;
								}
							}
						}
					}
				}
			}
		});
		return true;
	}

}
