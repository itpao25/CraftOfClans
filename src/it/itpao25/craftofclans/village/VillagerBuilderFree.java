package it.itpao25.craftofclans.village;

import java.util.ArrayList;

//import java.util.HashMap;

//import it.itpao25.craftofclans.CraftOfClans;

import java.util.Date;
import java.util.List;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.Structures;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.util.WorldGuardUtili;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VillagerBuilderFree {

	private Player player;
	// HashMap che contiene i cooldowns degli utenti
	// private HashMap<String, Long> cooldowns = new HashMap<String, Long>();

	public VillagerBuilderFree(Player player) {
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

		// Controllo se il giocatore sta già creando un villaggio
		if (p.is_creating_village) {
			player.sendMessage(Color.message(CraftOfClansM.getString("freemode.village-under-construction")));
			return false;
		}
		
		// Controllo se il giocatore ha già un villaggio
		if (p.hasVillage()) {
			player.sendMessage(Color.message(CraftOfClansM.getString("messages.already-has-village")).replace("%1%", "/coc tp"));
			return false;
		}

		LogHandler.log("Looking for the first village available...");
		p.sendMessage(Color.message(CraftOfClansM.getString("messages.village-creating-progress")));

		// Vedo se c'è spazio nella posizione scelta
		CraftOfClans.getInstance().getServer().getScheduler().runTask(CraftOfClans.getInstance(), new Runnable() {
			@Override
			public void run() {

				// Posizione del giocatore
				final Location loc = player.getLocation();

				if (!loc.getWorld().getName().equals("clansworld")) {
					player.sendMessage(Color.message("&cYou cannot create the village in this world"));
					return;
				}

				double min_height = CraftOfClans.config.getDouble("freemode.min-height");
				double max_height = CraftOfClans.config.getDouble("freemode.max-height");

				// Altezza minima di creazione
				if (loc.getY() < min_height) {
					player.sendMessage(Color.message(CraftOfClansM.getString("freemode.insufficient-minimum-height").replace("%1%", min_height + "")));
					return;
				}

				// Altezza massima di creazione
				if (loc.getY() > max_height) {
					player.sendMessage(Color.message(CraftOfClansM.getString("freemode.insufficient-max-height").replace("%1%", max_height + "")));
					return;
				}

				boolean is_disponibile = true;

				VillageCuboid cuboid = new VillageCuboid(loc, loc.clone().add(0, 1, 0));
				VillageCuboid cuboid_tocheck = cuboid.outset(CuboidDirection.Horizontal, VillageSettings.sizeX() / 2 + 10); // blocchi per lato: 128

				// Controllo se c'è posto nell'area
				for (Chunk chunk : cuboid_tocheck.getChunks()) {
					Block block = chunk.getBlock(7, loc.getBlockY(), 7);
					VillageId villo = new VillageId(block.getX(), block.getZ(), true);
					if (!villo.isAvailable()) {
						is_disponibile = false;
					}

					// Se c'è worldguard installato vedo se c'è un area nella zona
					if (CraftOfClans.isWorldGuard) {
						if (WorldGuardUtili.isSafeZone(new Location(block.getWorld(), block.getX(), loc.getBlockY(), block.getZ()))) {
							is_disponibile = false;
						}
					}
				}

				if (!is_disponibile) {
					p.sendMessage(Color.message(CraftOfClansM.getString("freemode.nospace")));
					return;
				}

				final VillageId villo = new VillageId(loc.getBlockX(), loc.getBlockZ(), loc.getBlockY());
				if (villo.isAvailable()) {
					LogHandler.log("Register new village: " + villo.getID());

					p.is_creating_village = true;

					// Svuoto dal pavimento in su
					for (Chunk chunk : villo.getCuboid().getChunks()) {
						Location loc1 = chunk.getBlock(0, 0, 0).getLocation();
						loc1.setY(loc.getBlockY());

						Location loc2 = chunk.getBlock(15, 0, 15).getLocation();
						loc2.setY(loc.getBlockY() + 50);

						WorldEditUtil.setBlock(VillageSettings.getAirMaterial(), loc1, loc2);
					}

					// Se bisogna mettere l'erba sopra
					List<Material> erbe = new ArrayList<>();
					int erba_rand = 0;

					boolean erba_creare = CraftOfClans.config.getBoolean("generator.surface-grass");
					if (erba_creare) {
						erbe = MapInfo.getErbaVillage();
						erba_rand = erbe.size() - 1;
					}

					// Creo il pavimento
					if (CraftOfClans.config.getBoolean("generator.surface-block-pattern")) {

						List<String> materiali = CraftOfClans.config.get().getStringList("generator.surface-block-list");
						List<Material> material_obj = new ArrayList<>();

						for (String value : materiali) {
							material_obj.add(Material.getMaterial(value));
						}

						for (Chunk chunk : villo.getCuboid().getChunks()) {

							Location loc1 = chunk.getBlock(0, 0, 0).getLocation();
							loc1.setY(loc.getBlockY() - 1);

							Location loc2 = chunk.getBlock(15, 0, 15).getLocation();
							loc2.setY(loc.getBlockY() - 1);

							WorldEditUtil.setBlock(VillageSettings.convertMaterial(material_obj), loc1, loc2);

							// Imposto l'erba
							if (erba_creare) {

								for (int chunk_x = 0; chunk_x < 16; chunk_x++) {
									for (int chunk_z = 0; chunk_z < 16; chunk_z++) {

										int interval_100 = (int) Math.floor(Math.random() * (100 - 0 + 1) + 0);
										if (interval_100 % 10 == 0) {
											int random_erba_int = (int) Math.floor(Math.random() * (erba_rand - 0 + 1) + 0);
											chunk.getBlock(chunk_x, loc.getBlockY(), chunk_z).setType(erbe.get(random_erba_int));
										}
									}
								}
							}

						}
					}

					// Posiziono i blocchi sotto
					for (Chunk chunk : villo.getCuboid().getChunks()) {
						Location loc1 = chunk.getBlock(0, 0, 0).getLocation();
						loc1.setY(loc.getBlockY() - 2);
						Location loc2 = chunk.getBlock(15, 0, 15).getLocation();
						loc2.setY(loc.getBlockY() - 21);

						List<Material> materali_sotto = new ArrayList<>();
						materali_sotto.add(Material.STONE);
						WorldEditUtil.setBlock(VillageSettings.convertMaterial(materali_sotto), loc1, loc2);
					}

					// Dopo finita la costruzione registro il villaggio
					if (villo.register(p)) {
						p.is_creating_village = false;

						Structures strutture = villo.getStructures();
						// Costruisco il municipio
						Bukkit.getScheduler().runTask(CraftOfClans.getInstance(), strutture.getMunicipio(p, villo, true, start_time));
					}
				}
			}
		});
		return true;
	}

}
