package it.itpao25.craftofclans.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.holo.HolographicUtil;
import it.itpao25.craftofclans.map.ExpanderRegister;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.map.PlaceHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.troops.TroopsId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.HologramTrait;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelTrait;

public class AttackListener implements Listener {

	// NPC che poi diventeranno invisibili
	public static HashMap<NPC, StructuresId> difese = new HashMap<NPC, StructuresId>();

	// NPC che vengono spawnati e gestiti da Sentinel
	public static HashMap<NPC, StructuresId> difese_sentinel = new HashMap<NPC, StructuresId>();

	// NPC truppe che vengono spawnate e gestite da Sentinel
	public static HashMap<NPC, Player> truppe_sentinel = new HashMap<NPC, Player>();

	// NPC sopra le strutture che rimangono fissi
	public static HashMap<StructuresId, ArrayList<NPC>> npc_structures = new HashMap<StructuresId, ArrayList<NPC>>();

	public static HashMap<UUID, Location> respawn_location = new HashMap<>();

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getPlayer().getWorld().getName().equals("clansworld")) {
			if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
				VillageId villo = AttackerManager.battles.get(event.getPlayer().getUniqueId()).getVillage();
				VillageId destinazione = MapInfo.getVillage(event.getTo());
				if (villo.equals(destinazione)) {
					event.setCancelled(false);
				} else {
					event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.attack-block-teletransport")));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
			// fermo l'attacco
			AttackerManager.battles.get(event.getPlayer().getUniqueId()).stop(true);
			AttackerManager.removePlayer(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
			AttackerManager.removePlayer(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onMobDamage(NPCDamageByEntityEvent event) {
		if (event.getNPC().getEntity() == null) {
			return;
		}
		
		// Se sta attaccando una difesa
		if (difese_sentinel.containsKey(event.getNPC()) || truppe_sentinel.containsKey(event.getNPC()) ||
				GuardianNPC.npc_guardian.containsKey(event.getNPC()) || AttackListener.difese.containsKey(event.getNPC())) {
			
			if(!event.getNPC().hasTrait(SentinelTrait.class)) {
				event.setCancelled(true);
				return;
			}
			
			double vita_massima = event.getNPC().getOrAddTrait(SentinelTrait.class).health;
			double vita_attuale = event.getNPC().getOrAddTrait(SentinelTrait.class).getLivingEntity().getHealth();
			
			String vita = _Number.getProgressHolo(vita_attuale, vita_massima);
			if (event.getNPC().getOrAddTrait(HologramTrait.class).getLines().size() > 0) {
				event.getNPC().getOrAddTrait(HologramTrait.class).setLine(0, vita);
			} else {
				event.getNPC().getOrAddTrait(HologramTrait.class).addLine(vita);
			}
			
			// Cancello il knockback dall'npc difensivo (arciere che protegge)
			if (AttackListener.difese.containsKey(event.getNPC())) {
				if(event.getNPC().data().has("originale")) {
					event.getNPC().teleport(event.getNPC().data().get("originale"), TeleportCause.PLUGIN);
				}
			}
			return;
		}
	}
	
	@EventHandler
	public void onMobDamage(NPCDamageEvent event) {
		if (event.getNPC().getEntity() == null) {
			return;
		}
		
		// Se sta attaccando una difesa
		if (difese_sentinel.containsKey(event.getNPC()) || truppe_sentinel.containsKey(event.getNPC()) || GuardianNPC.npc_guardian.containsKey(event.getNPC())) {
			if(!event.getNPC().hasTrait(SentinelTrait.class)) {
				event.setCancelled(true);
				return;
			}
			
			double vita_massima = event.getNPC().getOrAddTrait(SentinelTrait.class).health;
			double vita_attuale = event.getNPC().getOrAddTrait(SentinelTrait.class).getLivingEntity().getHealth();
			
			String vita = _Number.getProgressHolo(vita_attuale, vita_massima);
			if (event.getNPC().getOrAddTrait(HologramTrait.class).getLines().size() > 0) {
				event.getNPC().getOrAddTrait(HologramTrait.class).setLine(0, vita);
			} else {
				event.getNPC().getOrAddTrait(HologramTrait.class).addLine(vita);
			}
			return;
		}
	}
	
	/**
	 * Quando muovere una entità della struttura vado a disattivarla
	 * @param event
	 */
	@EventHandler
	public void onStructuresEntityDeath(NPCDeathEvent event) {
		if(AttackListener.difese.containsKey(event.getNPC())) {
			StructuresId struttura = AttackListener.difese.get(event.getNPC());
			disabilitaStruttura(struttura);
		}
	}
	
	@EventHandler
	public void onPlayerDead(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();

			if (AttackerManager.hasPlayer(p.getUniqueId())) {

				VillageId villo = AttackerManager.attackers.get(p.getUniqueId());

				// Quando il giocatore muore, posso fermare
				// l'esecuzioen della partita (attacco)
				AttackerManager.battles.get(p.getUniqueId()).stop(true);
				PlayerStored pstored = new PlayerStored(p);
				respawn_location.put(p.getUniqueId(), pstored.getVillage().locBase());

				// Se compatibile con Holographic Display creo l'item fluttuante
				Boolean is_grave_enable = CraftOfClans.config.getString("holographic-display.attack-chest-death-player") != null ? CraftOfClans.config.getBoolean("holographic-display.attack-chest-death-player") : false;

				if (CraftOfClans.isHolographicDisplay && is_grave_enable) {
					List<ItemStack> itemStackList = event.getDrops();
					// Se ha almeno un oggetto
					if (itemStackList.size() == 0)
						return;
					ItemStack[] stackArray = itemStackList.toArray(new ItemStack[0]);
					event.getDrops().clear();

					HolographicUtil.creaCassaMorto(p, p.getLocation(), stackArray, villo);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (respawn_location.containsKey(event.getPlayer().getUniqueId())) {
			event.setRespawnLocation(respawn_location.get(event.getPlayer().getUniqueId()));
			respawn_location.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerLeft(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (AttackerManager.hasPlayer(p.getUniqueId())) {
			// Imposto il danno dell'attacco a zero
			// Così impara ad uscire durante l'attacco
			AttackerManager.battles.get(p.getUniqueId()).setPercentDanno(0);
			AttackerManager.battles.get(p.getUniqueId()).stop(true);
		}
	}

	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent event) {
		if (event.getBlock() == null || event.getBlock().getType().equals(Material.AIR)) {
			return;
		}
		if (event.getPlayer().getWorld().getName().equals("clansworld")) {
			Player p = event.getPlayer();
			if (AttackerManager.hasPlayer(p.getUniqueId())) {

				String villo = AttackerManager.battles.get(p.getUniqueId()).getVillage().getID();
				Attack attacco = AttackerManager.battles.get(p.getUniqueId());
				String corso = MapInfo.getVillage(event.getBlock().getLocation()).getID();
				if (villo.equals(corso) == false) {
					return;
				}
				StructuresId structuresid = MapInfo.getStructures(event.getBlock().getLocation());

				if (structuresid != null) {
					
					if(event.getBlock().getLocation().getY() >= VillageSettings.getHeight(structuresid.getVillage())) {
						
						// Controllo se la struttura è un muro o un cancello
						if(structuresid.getType().equals(StructuresEnum.VILLAGE_WALL.toString()) || structuresid.getType().equals(StructuresEnum.VILLAGE_GATE_WALL.toString())) {
							
							// Rimuovo i blocchi di protezione
							attacco.removeBlocksProtection(structuresid);
							
							structuresid.disableOnAttack();
							return;
						}
					}
					
					event.setCancelled(true);

					if (AttackerManager.battles_blocks.containsKey(structuresid)) {
						HashMap<ArrayList<Block>, Integer> info = AttackerManager.battles_blocks.get(structuresid);

						int index = 1;
						ArrayList<Block> blocchi = null;
						int level = 0;

						for (Entry<ArrayList<Block>, Integer> id : info.entrySet()) {
							if (index == 1) {
								blocchi = id.getKey();
								level = id.getValue();
							}
							index++;
						}

						if (blocchi != null && blocchi.contains(event.getBlock())) {
							if (level != 0) {
								if (level == 1) {
									// Inserisco i blocchi
									HashMap<ArrayList<Block>, Integer> result = new HashMap<>();
									ArrayList<Block> array = new ArrayList<>();

									for (Block blocko : blocchi) {
										blocko.setType(Material.YELLOW_STAINED_GLASS);
										array.add(blocko);
									}
									result.put(array, 2);
									AttackerManager.battles_blocks.put(structuresid, result);

								} else if (level == 2) {

									HashMap<ArrayList<Block>, Integer> result = new HashMap<>();
									ArrayList<Block> array = new ArrayList<>();

									for (Block blocko : blocchi) {
										blocko.setType(Material.RED_STAINED_GLASS);
										array.add(blocko);
									}
									result.put(array, 3);
									AttackerManager.battles_blocks.put(structuresid, result);

								} else if (level == 3) {

									// Struttura disabilitata
									disabilitaStruttura(structuresid);
									
									// Rimuovere le risorse e le aggiungo al giocatore
									if (structuresid.getResourceAttack() >= 1) {

										PlayerStored pstored = new PlayerStored(p);
										double da_rimuovere = structuresid.getResourceAttack();
										PlayerStored pstored_victim = new PlayerStored(structuresid.getVillage().getOwnerID());

										// Tolgo le risorse al giocatore vittima e le aggiungo al giocatore che attacca
										if (structuresid.getType().equals("GOLD_MINE") || structuresid.getType().equals("GOLD_STORAGE")) {

											if (pstored.addGold(da_rimuovere) == false) {
												if (pstored.getLastResultFull()) {
													double attuale = pstored.getGold();
													double massimo = pstored.getMaxGold();

													da_rimuovere = massimo - attuale;
													da_rimuovere = da_rimuovere < 0 ? 0 : da_rimuovere;

													pstored.addGold(da_rimuovere);

													attacco.registroRisorse("gold", da_rimuovere);
													pstored_victim.removeGold(da_rimuovere);
												}
												p.sendMessage(Color.message(CraftOfClansM.getString("messages.gold-storage-full")));
											} else {
												// Aggiungo la risorsa raccolta
												attacco.registroRisorse("gold", da_rimuovere);
												pstored_victim.removeGold(da_rimuovere);
											}

										} else if (structuresid.getType().equals("COLLECTOR_ELIXIR") || structuresid.getType().equals("ELIXIR_STORAGE")) {

											if (pstored.addElixir(da_rimuovere) == false) {
												if (pstored.getLastResultFull()) {
													double attuale = pstored.getElixir();
													double massimo = pstored.getMaxElixir();

													da_rimuovere = massimo - attuale;
													da_rimuovere = da_rimuovere < 0 ? 0 : da_rimuovere;

													pstored.addElixir(da_rimuovere);

													attacco.registroRisorse("elixir", da_rimuovere);
													pstored_victim.removeElixir(da_rimuovere);
												}
												p.sendMessage(Color.message(CraftOfClansM.getString("messages.elixir-storage-full")));
											} else {
												// Aggiungo la risorsa raccolta
												attacco.registroRisorse("elixir", da_rimuovere);
												pstored_victim.removeElixir(da_rimuovere);
											}

										} else if (structuresid.getType().equals("GEMS_COLLECTOR")) {

											// Rimuovo le gemme e le do al giocatore
											if (pstored.addGems(da_rimuovere) == false) {
												// non può non aver spazio per le gemme
											} else {
												// Aggiungo la risorsa raccolta
												attacco.registroRisorse("gems", da_rimuovere);
												pstored_victim.removeGems(da_rimuovere);
											}
										}
									}
									
									// Se la percentuale dell'attacco è 100 allora fermo
									if (attacco.getPercentDamage() == 100) {
										// ha completato l'attacco
										attacco.stop(true);
									}
								}
							}
						} else {
							event.setCancelled(true);
							return;
						}
					} else {
					}

				} else {
					if (!VillageSettings.isPlayerCanBuildOwn()) {
						// event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.build-not-allowed")));
						event.setCancelled(true);
						return;
					}
					if (!ExpanderRegister.isPresent(event.getBlock().getChunk())) {
						event.setCancelled(true);
						return;
					}

					// Se è l'erba generata
					if (MapInfo.erbe_mappa.size() > 0) {
						for (Material material : MapInfo.erbe_mappa) {
							if (material.equals(event.getBlock().getType())) {
								event.setCancelled(true);
							}
						}
					}
				}

				if (event.getBlock().getY() < VillageSettings.getHeight(structuresid.getVillage())) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void disabilitaStruttura(StructuresId id) {
		
		// Prendo l'attacco su questa struttura
		Attack attacco = null;
		
		for (Entry<UUID, VillageId> attacchi : AttackerManager.attackers.entrySet()) {
			// Trovo l'UUID del player che sta attaccando il villaggio
			if(attacchi.getValue().equals(id.getVillage())) {
				UUID id_player = attacchi.getKey();
				
				for (Entry<UUID, Attack> battaglie : AttackerManager.battles.entrySet()) {
					if(battaglie.getKey().equals(id_player)) {
						attacco = battaglie.getValue();
					}
				}
			}
		}
		
		if(attacco == null) {
			LogHandler.error("Attack on disabled structure not found.");
			return;
		}
		
		HashMap<ArrayList<Block>, Integer> info = AttackerManager.battles_blocks.get(id);
		
		int index = 1;
		ArrayList<Block> blocchi_vetro = null;
		
		for (Entry<ArrayList<Block>, Integer> idLC : info.entrySet()) {
			if (index == 1) {
				blocchi_vetro = idLC.getKey();
			}
			index++;
		}
		
		HashMap<ArrayList<Block>, Integer> result = new HashMap<>();
		ArrayList<Block> array = new ArrayList<>();

		for (Block blocco : blocchi_vetro) {
			blocco.setType(Material.BLACK_STAINED_GLASS);
			array.add(blocco);
			
			// Spawno l'effetto
			for(int i = 0; i<5; i++) {
				Location loc = blocco.getLocation().clone().add(+0.5, 1, +0.5);
				blocco.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, loc, 1, 0, 0, 0, 0);
			}
		}
		
		result.put(array, 4);
		
		// Suono
		Location centro = id.getCuboid().getCenter();
		centro.getWorld().playSound(centro, Sound.ITEM_TOTEM_USE, 1, 1);
		
		AttackerManager.battles_blocks.put(id, result);
		attacco.disableStructure(id);
		
		// Aggiungo la struttura distrutta
		attacco.addPercentDanno();
	}
	
	@EventHandler
	public void onPlayerBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock() == null || event.getBlock().getType().equals(Material.AIR)) {
			return;
		}
		if (event.getBlock().getWorld().getName().equals("clansworld")) {
			Player p = event.getPlayer();
			if (AttackerManager.hasPlayer(p.getUniqueId())) {

				// Se è dentro una struttura
				StructuresId structuresid = MapInfo.getStructures(event.getBlock().getLocation());
				if (structuresid != null) {
					event.setCancelled(true);
					return;
				}

				// Se è abilitata la costruzione nel config
				if (!VillageSettings.isPlayerCanBuildOwn()) {
					// event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.build-not-allowed")));
					event.setCancelled(true);
					return;
				}

				// Se non è espanso in quel territorio
				if (!ExpanderRegister.isPresent(event.getBlock().getChunk())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}
		if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
			Player p = event.getPlayer();
			Attack attacco = AttackerManager.battles.get(p.getUniqueId());
			String corso = MapInfo.getVillage(p.getLocation()).getID();
			String villaggio_attacco = attacco.getVillage().getID();
			if (corso.equals(villaggio_attacco) == false) {
				event.setCancelled(true);
				attacco.getVillage().tp(p);
			}
		}
	}

	@EventHandler
	public void EsposioneMortaio(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (!AttackerManager.hasPlayer(p.getUniqueId())) {
				return;
			}
			if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
				e.setCancelled(true);
				Vector vector = new Vector(0, 0.5, 0);
				p.setVelocity(vector);
				p.damage(e.getFinalDamage());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (PermissionUtil._has(p, _Permission.PERM_ATTACK_BYPASSCMD) || p.isOp()) {
			return;
		}
		if (AttackerManager.hasPlayer(p.getUniqueId())) {
			String comando = event.getMessage();
			List<String> bCmds = CraftOfClans.config.get().getStringList("attack.commands-disabled-during-attack");

			for (String bCmd : bCmds) {
				if (!bCmd.startsWith("/")) {
					bCmd = "/" + bCmd;
				}
				if (comando.toLowerCase().startsWith(bCmd)) {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.attack-cmd-not-allowd")));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void spawnTroops(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}
		
		// Non può aprire i cancelli durante gli attacchi
		if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
			if(event.hasBlock()) {
				Material materiale_blocco = event.getClickedBlock().getType();
				if(materiale_blocco.toString().toLowerCase().contains("gate")) {
					event.setCancelled(true);
				}
			}
		}
		
		Player p = event.getPlayer();
		if (p.getInventory().getItemInMainHand() != null && event.hasBlock()) {

			Location loc = event.getClickedBlock().getLocation();
			VillageId villo = MapInfo.getVillage(loc);
			
			if (loc.getBlockY() != (VillageSettings.getHeight(villo)) - 1) {
				return;
			}

			loc.setY(VillageSettings.getHeight(villo));
			
			ItemStack item_in_hand = p.getInventory().getItemInMainHand();
			if(item_in_hand.getItemMeta() == null) {
				return;
			}
			
			ItemMeta data_item = item_in_hand.getItemMeta();

			if (!data_item.hasDisplayName()) {
				return;
			}

			String nome_item = data_item.getDisplayName();
			PlayerStored pstored = new PlayerStored(p);

			for (TroopsId id : pstored.getTroops()) {
				if (id.display_name.equals(Color.translate(nome_item))) {

					// Controllo se il vilaggio è quello del player che sta posizionando
					String playername_village = PlaceHandler.setOwnerFromLore(item_in_hand);
					if (playername_village != null) {
						String pstore = p.getName();
						if (!pstore.equals(playername_village)) {
							event.setCancelled(true);
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.place-not-allowed-own")));
							return;
						}
					}

					// Spawno gli npc
					if (!AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.troops-place-onlyattack")));
						return;
					}

					for (NPC npc : id.getNPCs()) {
						
						// Imposto l'obiettivo
						npc.getOrAddTrait(SentinelTrait.class).addTarget("squad:difese");
						npc.getOrAddTrait(SentinelTrait.class).squad = "truppe";
						
						/*
						VillageId villo_attaccato = AttackerManager.attackers.get(p.getUniqueId());
						for (Entry<NPC, VillageId> guardiano : GuardianNPC.npc_guardian.entrySet()) {
							if (!guardiano.getValue().equals(villo_attaccato)) {
								continue;
							}
							npc.getOrAddTrait(SentinelTrait.class).addTarget("npc:" + guardiano.getKey().getName());
						}
						*/
						
						npc.getOrAddTrait(SentinelTrait.class).addIgnore("player:"+ p.getName());
						npc.getOrAddTrait(SentinelTrait.class).respawnTime = 0;
						
						// Se ha un livello personalizzato di difesa
						if(id.getDamageValue() != -1) {
							npc.getOrAddTrait(SentinelTrait.class).damage = id.getDamageValue();
						}
						
						// Faccio seguire il player
						npc.getOrAddTrait(FollowTrait.class).toggle((OfflinePlayer) p, true); 
						
						npc.setProtected(false);
						npc.spawn(loc);

						truppe_sentinel.put(npc, p);
					}
					
					// Rimuovo l'item dall'inventario
					if (item_in_hand.getAmount() > 1) {
						p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
					} else {
						p.getInventory().removeItem(item_in_hand);
					}
					
					// Faccio il suono
					p.playSound(loc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);
				}
			}
		}
	}
}
