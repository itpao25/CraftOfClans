package it.itpao25.craftofclans.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcmonkey.sentinel.SentinelTrait;

import com.google.common.collect.Lists;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.AttackComplete;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.handler.TitleManager;
import it.itpao25.craftofclans.handler.TitleManagerHook;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.worldmanager.Sounds;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;

public class Attack {

	// Giocatore che ha iniziato l'attacco
	public PlayerStored p;

	// Villaggio che si sta attaccando
	private final VillageId village;
	
	// Timer task
	private int id_timer;

	// Lista dei blocchi spawnati
	public HashMap<Block, StructuresId> block_list = new HashMap<Block, StructuresId>();

	// Lista delle strutture disabilitate
	public HashMap<StructuresId, VillageId> structures_disabled = new HashMap<StructuresId, VillageId>();

	// Lista dei blocchi protezione delle mura
	public HashMap<StructuresId, ArrayList<Block>> walls_protection = new HashMap<StructuresId, ArrayList<Block>>();

	public boolean isStopped = false;
	private int time_left;
	private int amount_percent_tot = 0;
	private int amount_percent = 0;

	// Lista delle risorse che ha raccolto fin ora il giocatore che attacca
	private static double registro_risorse_gold = 0;
	private static double registro_risorse_gems = 0;
	private static double registro_risorse_elixir = 0;

	// Sistema di scudo
	public boolean impostato_scudo_50 = false;
	public boolean impostato_scudo_85 = false;

	// Posizione dell'NPC prima dell'attacco
	private HashMap<NPC, Location> npcLocations = new HashMap<NPC, Location>();

	public Attack(PlayerStored player, VillageId village) {

		this.p = player;
		this.village = village;

		if (this.start()) {

			startTimer();
			spawnBreakableBlocksForStructures();
			spawnDefense();
			spawnWallsProtection();

			// Aggiungo il giocatore al countdown degli attacchi
			AttackerManager.addInCooldown(player.getUUID());
			final Player player_finale = player.get();
			Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
				public void run() {
					// Teletrasporto il giocatore
					if (!Attack.this.village.tpAttack(player_finale)) {
						System.out.println("Si è verificato un errore!! Report this on spigotmc (PM: itpao25)");
					} else {
						// L'NPC attacca il player
						if (CraftOfClans.isSentinel) {
							NPCGuardianVillage("add");
						}
						AttackerManager.attackers.put(player.get().getUniqueId(), Attack.this.village);
					}
				}
			}, 20L);
		}
	}

	/**
	 * Inizio l'attacco
	 */
	private boolean start() {

		// Giocatore già in battaglia
		if (AttackerManager.hasPlayer(p.get().getUniqueId())) {
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.attack-already-start")));
			return false;
		}

		// Controllo se il giocatore ha un villaggio creato
		if (!p.hasVillage()) {
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village")));
			return false;
		}

		String title = Color.translate(CraftOfClansM.getString("messages.attack-start").replace("%1%", village.getOwnerName()));
		String info = Color.translate(CraftOfClansM.getString("messages.attack-start-info").replace("%1%", AttackerManager.getTimeMatch() + ""));

		if (TitleManagerHook.enable) {
			TitleManager.sendFloatingText(p.get(), title, info);
		}

		p.sendMessage(Color.message(title));
		p.sendMessage(Color.message(info));

		AttackerManager.battles.put(p.getUUID(), this);

		// Imposto la data di attacco
		getVillage().setLastAttack();

		// Controllo se ci sono stati attacchi precedenti in memoria
		// Rimuovo l'hashmap che contiene le strutture disattivate
		for (Entry<StructuresId, VillageId> id : structures_disabled.entrySet()) {
			if (id.getValue().equals(getVillage())) {
				structures_disabled.remove(id.getKey());
			}
		}

		return true;
	}

	private void startTimer() {
		final boolean isTitleManager = CraftOfClans.isTitleManager;
		final int tempo = AttackerManager.getTimeMatch();

		id_timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(CraftOfClans.getInstance(), new Runnable() {
			int index = tempo;
			// int valore_incremento = 0;

			@Override
			public void run() {

				// Riproduco il tempo per il countdown
				if (index <= 5) {
					p.get().playSound(p.get().getLocation(), Sounds.ORB_PICKUP.bukkitSound(), 1, 1);
				}

				if (index == 0) {
					// Finito il tempo
					stop(true);
				}

				if (isTitleManager) {
					String text = Color.translate(CraftOfClansM.getString("messages.attack-bar-progress").replace("%1%", index + ""));
					text = text.replace("%2%", getPercentDamage() + "%");

					TitleManager.sendActionbarMessage(p.get(), TitleManager.getProgressEx(index, tempo) + " " + text);
				}

				// Attivo lo scudo 50%
				if (getPercentDamage() >= 50 && !impostato_scudo_50) {
					impostato_scudo_50 = true;
					getVillage().setScudo("50");
				}

				// Attivo lo scudo 85%
				if (getPercentDamage() >= 85 && !impostato_scudo_85) {
					impostato_scudo_85 = true;
					getVillage().setScudo("85");
				}

				if (!isStopped()) {
					// if (valore_incremento % 2 == 0) {
					AttackStrategy lancio = new AttackStrategy(getVillage());
					lancio.run();
					// }
				}
				index--;
				// valore_incremento++;
				setTime(index);
			}
		}, 0L, 20L);

	}

	public void setTime(int index) {
		this.time_left = index;
	}

	private void stopTimer() {
		if (id_timer != 0) {
			Bukkit.getScheduler().cancelTask(id_timer);
		}
	}

	public VillageId getVillage() {
		return village;
	}

	/**
	 * Fermo l'attacco e teletrasporto il giocatore
	 */
	public boolean stop(boolean despawn_npc) {

		AttackComplete event = new AttackComplete(p.get(), getPercentDamage(), village);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return false;
		}

		setStopped(true);
		stopTimer();

		// Consegno i trofei al giocatore
		int trofei_vittoria = CraftOfClans.config.getString("attack.trophies-100") != null ? CraftOfClans.config.getInt("attack.trophies-100") : 25;
		int trofei_lost = CraftOfClans.config.getString("attack.trophies-lost") != null ? CraftOfClans.config.getInt("attack.trophies-lost") : 15;

		// Algoritmo per l'assegnazione dei trofei
		if (getPercentDamage() == 100) {
			
			p.addTrofei(trofei_vittoria);
			
			// Imposto i trofei persi a zero
			trofei_lost = 0;
			
		} else if (getPercentDamage() >= 50) {
			
			trofei_vittoria = (int) trofei_vittoria / 2;
			p.addTrofei(trofei_vittoria);
			
			// Imposto i trofei persi a zero
			trofei_lost = 0;
			
		} else {
			if (p.hasTrofei(trofei_lost)) {
				p.removeTrofei(trofei_lost);
			} else {
				p.setTrofei(0);
			}
			
			trofei_vittoria = 0;
		}

		AttackerManager.removePlayer(p.get().getUniqueId());
		AttackerManager.battles.remove(p.get().getUniqueId());

		if (despawn_npc) {
			// Entità di questo attacco
			Iterator<Entry<NPC, StructuresId>> it = AttackListener.difese.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry<NPC, StructuresId> id = (Map.Entry<NPC, StructuresId>) it.next();
				if (id.getValue().getVillage().equals(village)) {
					
					// Se aveva l'hologramma sulla testa
					if (id.getKey().hasTrait(HologramTrait.class)) {
						id.getKey().removeTrait(HologramTrait.class);
					}
					
					// Se non sono gli NPC che devono rimanere spawnati
					if (!id.getValue().getType().equals("ARCHER_TOWER") && !id.getValue().getType().equals("WIZARD_TOWER")) {
						id.getKey().destroy();
						
					} else {

						// Se aveva abilitato il combattimento
						if (id.getKey().hasTrait(SentinelTrait.class)) {
							id.getKey().removeTrait(SentinelTrait.class);
						}

						// Se non è spawnato (Arcieri e stregoni)
						if (id.getKey().isSpawned()) {
							id.getKey().despawn();
						}

						// Prendo la posizione dell'arciere/stregone registrato in precedenza
						if (id.getKey().data().has("originale")) {
							id.getKey().spawn(id.getKey().data().get("originale"));
						} else {
							id.getKey().spawn(id.getKey().getStoredLocation());
						}
					}
					it.remove();
				}
			}

			// NPC di difesa
			Iterator<Entry<NPC, StructuresId>> difese = AttackListener.difese_sentinel.entrySet().iterator();
			while (difese.hasNext()) {
				Map.Entry<NPC, StructuresId> id = (Map.Entry<NPC, StructuresId>) difese.next();
				if (id.getValue().getVillage().equals(village)) {
					
					// Se aveva l'hologramma sulla testa
					if (id.getKey().hasTrait(HologramTrait.class)) {
						id.getKey().removeTrait(HologramTrait.class);
					}
					
					id.getKey().destroy();
					difese.remove();
				}
			}

			// NPC truppe
			Iterator<Entry<NPC, Player>> truppe = AttackListener.truppe_sentinel.entrySet().iterator();
			while (truppe.hasNext()) {
				Map.Entry<NPC, Player> id = (Entry<NPC, Player>) truppe.next();
				if (id.getValue().equals(this.p.get())) {
					
					// Se aveva l'hologramma sulla testa
					if (id.getKey().hasTrait(HologramTrait.class)) {
						id.getKey().removeTrait(HologramTrait.class);
					}
					
					id.getKey().destroy();
					truppe.remove();
				}
			}

			// Rimuovo l'attacco dell'NPC
			if (CraftOfClans.isSentinel) {
				NPCGuardianVillage("remove");
			}
		}

		// Tolgo i blocchi di vetro per le strutture
		for (Entry<Block, StructuresId> id : this.block_list.entrySet()) {
			id.getKey().setType(Material.AIR);
		}

		// Tolgo i blocchi di protezione per le mura
		for (Entry<StructuresId, ArrayList<Block>> id : walls_protection.entrySet()) {
			for (Block blocco : id.getValue()) {
				blocco.setType(Material.AIR);
			}
		}

		// Attivo lo scudo 50%
		if (getPercentDamage() >= 50 && !impostato_scudo_50) {
			impostato_scudo_50 = true;
			getVillage().setScudo("50");
		}

		// Attivo lo scudo 85%
		if (getPercentDamage() >= 85 && !impostato_scudo_85) {
			impostato_scudo_85 = true;
			getVillage().setScudo("85");
		}

		// Invio il messaggio al giocatore
		if (getPercentDamage() >= 50) {

			// Attacco vinto con successo
			// Aggiungo la vittoria al giocatore (versione 0.4)
			Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
				public void run() {
					p.addWin();
				}
			}, 20 * 2);

			String win = Color.translate(CraftOfClansM.getString("messages.attack-over-win").replace("%1%", trofei_vittoria + ""));
			String percent = Color.translate(CraftOfClansM.getString("messages.attack-over-percent-win").replace("%2%", getPercentDamage() + "%").replace("%1%", trofei_vittoria + ""));
			p.get().sendMessage(Color.message(win));
			if (TitleManagerHook.enable) {
				TitleManager.sendFloatingText(p.get(), win, percent);
			} else {
				p.get().sendMessage(Color.message(percent));
			}

		} else {

			// Attacco perso
			// Aggiungo la perdita al giocatore (versione 0.4)
			Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
				public void run() {
					p.addLost();
				}
			}, 20 * 2);

			String lost = Color.translate(CraftOfClansM.getString("messages.attack-over-lost").replace("%1%", trofei_lost + ""));
			String percent = Color.translate(CraftOfClansM.getString("messages.attack-over-percent-lost").replace("%2%", getPercentDamage() + "%").replace("%1%", trofei_lost + ""));
			p.get().sendMessage(Color.message(lost));

			if (TitleManagerHook.enable) {
				TitleManager.sendFloatingText(p.get(), lost, percent);
			} else {
				p.get().sendMessage(Color.message(percent));
			}
		}

		// Invio un messaggio al giocatore con il numero di risorse che ha vinto
		// durante l'attacco
		p.get().sendMessage(Color.message(CraftOfClansM.getString("messages.attack-over-gold-collected").replace("%1%", _Number.showNumero(registro_risorse_gold))));
		p.get().sendMessage(Color.message(CraftOfClansM.getString("messages.attack-over-elixir-collected").replace("%1%", _Number.showNumero(registro_risorse_elixir))));
		p.get().sendMessage(Color.message(CraftOfClansM.getString("messages.attack-over-gems-collected").replace("%1%", _Number.showNumero(registro_risorse_gems))));
		
		// Inserisco l'attacco al giocatore
		p.addRegistredAttack(village, registro_risorse_gems, registro_risorse_gold, registro_risorse_elixir, 0, getPercentDamage(), trofei_vittoria, trofei_lost);
		
		Location spawn = p.getVillage().locBase();
		if (p.get().teleport(spawn)) {
			return true;
		}
		return false;
	}

	/**
	 * Creo le difese per il villaggio
	 */
	private void spawnDefense() {
		for (Entry<StructuresId, String> id : village.getStructuresList().entrySet()) {

			// Strutture che non aumentano il totale
			if (!id.getValue().equals(StructuresEnum.BOMB.toString()) && !id.getValue().equals(StructuresEnum.SKELETON_TRAP.toString()) && !id.getValue().equals(StructuresEnum.VILLAGE_WALL.toString()) && !id.getValue().equals(StructuresEnum.VILLAGE_GATE_WALL.toString())
					&& !id.getKey().isDecoration()) {
				// Aggiungo la struttura alla somma delle strutture
				addPercentTotDanno();
			}

			if (id.getValue().equals("ARCHER_TOWER") || id.getValue().equals("WIZARD_TOWER")) {

				// Prendo gli NPC presenti nella struttura spawnata
				if (AttackListener.npc_structures.containsKey(id.getKey())) {
					for (NPC npc : AttackListener.npc_structures.get(id.getKey())) {

						npc.getOrAddTrait(SentinelTrait.class).squad = "difese";
						npc.getOrAddTrait(SentinelTrait.class).respawnTime = 0;
						npc.getOrAddTrait(SentinelTrait.class).allowKnockback = false;
						npc.getOrAddTrait(SentinelTrait.class).speed = 0;
						npc.setProtected(false);

						AttackListener.difese.put(npc, id.getKey());
					}
				}

			} else if (id.getValue().equals("CANNON")) {

				// Gestione del cannone nella mappa
				Location loc1 = id.getKey().getCuboid().getFace(CuboidDirection.Up).corners()[7].getLocation();
				Location loc2 = id.getKey().getCuboid().getFace(CuboidDirection.Up).corners()[1].getLocation();
				VillageCuboid cuboid = new VillageCuboid(loc1, loc2);
				Location loc = cuboid.getCenter().getBlock().getLocation().add(+0.5, -0.5, +0.5);

				// Genero l'entità fake
				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ENDERMITE, "Cannon");
				npc.getOrAddTrait(SentinelTrait.class).squad = "difese";
				npc.getOrAddTrait(SentinelTrait.class).respawnTime = 0;
				npc.getOrAddTrait(SentinelTrait.class).setHealth(60);

				npc.spawn(loc);
				npc.getEntity().setGravity(false);
				npc.setProtected(false);

				((LivingEntity) npc.getEntity()).setInvisible(true);
				((LivingEntity) npc.getEntity()).setAI(false); // Così non si muove più

				AttackListener.difese.put(npc, id.getKey());

			} else if (id.getValue().equals("MORTAR")) {

				Location loc = id.getKey().getCuboid().getFace(CuboidDirection.Up).getCenter().add(0, 0, 0);

				// Genero l'entità fake
				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ENDERMITE, "Mortar");
				npc.getOrAddTrait(SentinelTrait.class).squad = "difese";
				npc.getOrAddTrait(SentinelTrait.class).respawnTime = 0;
				npc.getOrAddTrait(SentinelTrait.class).setHealth(60);

				npc.spawn(loc);
				npc.getEntity().setGravity(false);
				npc.setProtected(false);

				((LivingEntity) npc.getEntity()).setInvisible(true);
				((LivingEntity) npc.getEntity()).setAI(false); // Così non si muove più

				AttackListener.difese.put(npc, id.getKey());

			} else if (id.getValue().equals("TESLA")) {

				Location loc = id.getKey().getCuboid().getFace(CuboidDirection.Up).getCenter().add(0, -0.5, 0);

				// Genero l'entità fake
				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ENDERMITE, "Tesla");
				npc.getOrAddTrait(SentinelTrait.class).squad = "difese";
				npc.getOrAddTrait(SentinelTrait.class).respawnTime = 0;
				npc.getOrAddTrait(SentinelTrait.class).setHealth(60);

				npc.spawn(loc);
				npc.getEntity().setGravity(false);
				npc.setProtected(false);

				((LivingEntity) npc.getEntity()).setInvisible(true);
				((LivingEntity) npc.getEntity()).setAI(false); // Così non si muove più

				AttackListener.difese.put(npc, id.getKey());

				final StructuresId struttura = id.getKey();

				Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
					public void run() {

						// Prendo i blocchi e le rendo invisibili per il giocatore
						ArrayList<Block> blocchi = Lists.newArrayList(struttura.getCuboid().iterator());
						for (Block block : blocchi) {
							if (block.getY() > VillageSettings.getHeight(village) - 1) {
								p.get().sendBlockChange(block.getLocation(), Material.getMaterial("AIR").createBlockData());
							}
						}

						// Imposto invisibili i blocchi di disattivazione
						if (AttackerManager.battles_blocks.containsKey(struttura)) {
							HashMap<ArrayList<Block>, Integer> result = AttackerManager.battles_blocks.get(struttura);
							for (Entry<ArrayList<Block>, Integer> id1 : result.entrySet()) {
								ArrayList<Block> blocks = id1.getKey();
								for (Block object : blocks) {
									p.get().sendBlockChange(object.getLocation(), Material.AIR.createBlockData());
								}
							}
						}
					}
				}, 30L);

			} else if (id.getValue().equals("BOMB")) {

				Location loc = id.getKey().getCuboid().getFace(CuboidDirection.Up).getCenter();
				loc.setY(VillageSettings.getHeight(village));

				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARROW, "Bomb");

				npc.spawn(loc);
				npc.setProtected(true);

				// Nascondo l'NPC dai giocatori
				CraftOfClans.entityHider.hideEntity(p.get(), npc.getEntity());

				AttackListener.difese.put(npc, id.getKey());

				final StructuresId struttura = id.getKey();

				Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
					public void run() {
						// Prendo i blocchi e le rendo invisibili per il giocatore
						ArrayList<Block> blocchi = Lists.newArrayList(struttura.getCuboid().iterator());
						for (Block block : blocchi) {
							if (block.getY() > VillageSettings.getHeight(village) - 1) {
								p.get().sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
							}
						}
					}
				}, 30L);

			} else if (id.getValue().equals(StructuresEnum.SKELETON_TRAP.toString())) {

				Location loc = id.getKey().getCuboid().getFace(CuboidDirection.Up).getCenter().add(0, -0.5, 0);

				// Genero il player fake
				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARROW, "Skeleton Trap");
				npc.spawn(loc);
				npc.setProtected(true);

				// Nascondo l'NPC dai giocatori
				CraftOfClans.entityHider.hideEntity(p.get(), npc.getEntity());

				AttackListener.difese.put(npc, id.getKey());
			}
		}
	}

	/**
	 * Ritorno con il tempo rimanente per l'attacco
	 */
	public int getTimeLeft() {
		return time_left;
	}

	/**
	 * Blocchi che devono essere distrutti per avanzare con l'attacco
	 */
	public void spawnBreakableBlocksForStructures() {
		for (Entry<StructuresId, String> id : village.getStructuresList().entrySet()) {

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
			loc1.setY(VillageSettings.getHeight(village));
			loc2.setY(VillageSettings.getHeight(village));
			loc3.setY(VillageSettings.getHeight(village));
			loc4.setY(VillageSettings.getHeight(village));

			loc1.getBlock().setType(Material.LIME_STAINED_GLASS);
			block_list.put(loc1.getBlock(), id.getKey());

			loc2.getBlock().setType(Material.LIME_STAINED_GLASS);
			block_list.put(loc2.getBlock(), id.getKey());

			loc3.getBlock().setType(Material.LIME_STAINED_GLASS);
			block_list.put(loc3.getBlock(), id.getKey());

			loc4.getBlock().setType(Material.LIME_STAINED_GLASS);
			block_list.put(loc4.getBlock(), id.getKey());

			// Inserisco i blocchi
			HashMap<ArrayList<Block>, Integer> result = new HashMap<>();
			ArrayList<Block> array = new ArrayList<>();
			array.add(loc1.getBlock());
			array.add(loc2.getBlock());
			array.add(loc3.getBlock());
			array.add(loc4.getBlock());
			result.put(array, 1);

			AttackerManager.battles_blocks.put(id.getKey(), result);
		}
	}

	/**
	 * Controllo se l'attacco è ancora in corso
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 * Imposto se è stoppato l'attacco
	 */
	public void setStopped(boolean status) {
		isStopped = status;
	}

	/**
	 * Disabilito una struttura
	 */
	public void disableStructure(StructuresId id) {
		structures_disabled.put(id, getVillage());
	}

	/**
	 * Controllo se una struttura è disabilitata
	 */
	public boolean hasdisableDStructure(StructuresId id) {
		if (structures_disabled.containsKey(id)) {
			return true;
		}
		return false;
	}

	public int getPercentDamage() {
        return (int) ((double) amount_percent / amount_percent_tot * 100);
	}

	/**
	 * Aggiungo il valore per calcolare la valuta massima del villaggio
	 */
	public void addPercentTotDanno() {
		this.amount_percent_tot = amount_percent_tot + 10;
	}

	/**
	 * Aggiungo il valore dell'attacco attuale
	 */
	public void addPercentDanno() {
		this.amount_percent = amount_percent + 10;
	}

	/**
	 * Imposto la percentuale del danno
	 */
	public void setPercentDanno(int number) {
		this.amount_percent = number;
	}

	/**
	 * Registro i guadagni dell'attacco
	 */
	public void registroRisorse(String str, double amount) {
		switch (str) {
		case "gold":
			registro_risorse_gold = registro_risorse_gold + amount;
			break;
		case "gems":
			registro_risorse_gems = registro_risorse_gems + amount;
			break;
		case "elixir":
			registro_risorse_elixir = registro_risorse_elixir + amount;
			break;
		}
	}

	/**
	 * L'NPC del villaggio andrà ad attaccare il player attaccante
	 */
	public void NPCGuardianVillage(String action) {

		for (Entry<NPC, VillageId> entry : GuardianNPC.npc_guardian.entrySet()) {
			if (entry.getValue().equals(village)) {

				NPC npcvillo = entry.getKey();
				if (action.equals("add")) {

					npcLocations.put(npcvillo, npcvillo.getStoredLocation());

					npcvillo.getOrAddTrait(SentinelTrait.class).addTarget("uuid:" + this.p.getUUID());
					npcvillo.getOrAddTrait(SentinelTrait.class).squad = "difese";
					npcvillo.getOrAddTrait(SentinelTrait.class).respawnTime = 0;
					npcvillo.setProtected(false);

				} else if (action.equals("remove")) {

					npcvillo.removeTrait(SentinelTrait.class);
					if (npcvillo.hasTrait(HologramTrait.class)) {
						npcvillo.removeTrait(HologramTrait.class);
					}

					BukkitRunnable removeMe = new BukkitRunnable() {
						@Override
						public void run() {

							if (!npcvillo.isSpawned()) {
								npcvillo.spawn(npcLocations.get(npcvillo));
							} else {
								npcvillo.despawn();
								npcvillo.spawn(npcLocations.get(npcvillo));
							}
							npcvillo.setProtected(true);
						}
					};
					removeMe.runTaskLater(CraftOfClans.getInstance(), 1);
				}
			}
		}
	}

	/**
	 * Spawn invisible wall barrier
	 */
	public void spawnWallsProtection() {
		for (Entry<StructuresId, String> id : village.getStructuresList().entrySet()) {
			if (id.getKey().getType().equals(StructuresEnum.VILLAGE_WALL.toString()) || id.getKey().getType().equals(StructuresEnum.VILLAGE_GATE_WALL.toString())) {

				ArrayList<Block> blocchi = new ArrayList<>();
				
				for (int i = 1; i < 6; i++) {
					Location blocco_loc = id.getKey().getCuboid().getCenter();
					blocco_loc.setY(VillageSettings.getHeight(village) + i);

					Block block = blocco_loc.getWorld().getBlockAt(blocco_loc);
					block.setType(Material.BARRIER);
					blocchi.add(block);
				}
				walls_protection.put(id.getKey(), blocchi);
			}
		}
	}

	/**
	 * Rimuovo i blocchi di protezione per un muro
	 */
	public void removeBlocksProtection(StructuresId id) {
		if (walls_protection.containsKey(id)) {
			for (Block block : walls_protection.get(id)) {
				block.setType(Material.AIR);
			}
			walls_protection.remove(id);
		}
	}
}
