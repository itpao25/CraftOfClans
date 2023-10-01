package it.itpao25.craftofclans.player;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.attack.AttackerManager;
import it.itpao25.craftofclans.config.CraftOfClansClan;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.map.Expander;
import it.itpao25.craftofclans.map.ExpanderRegister;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.map.PlaceHandler;
import it.itpao25.craftofclans.map.PlacerListener;
import it.itpao25.craftofclans.structures.StructuresGUI;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.village.SpectatorMode;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageRestoreJoin;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.worldmanager.Sounds;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

	public static HashMap<String, Chunk> haveExpander = new HashMap<String, Chunk>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		CraftOfClans.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
			@Override
			public void run() {

				PlayerStored pstored = new PlayerStored(p);
				if (!pstored.isExist()) {
					pstored.register();
				}

				// Imposto l'ultima data di login
				pstored.setLastLogin();

				if (pstored.getVillage() == null) {
					return;
				}

				if (pstored.getVillage().isAttacked()) {
					p.kickPlayer(Color.translate(CraftOfClansM.getString("messages.village-inattack")).replace("\\n", "\n").replace("%1%", pstored.getVillage().getAttack().getTimeLeft() + ""));
					return;
				}

				// Controllo se nella configurazione è impostato il tp
				if (CraftOfClans.config.getString("player-settings.tp-ifvillage-onjoin") != null) {
					if (CraftOfClans.config.getBoolean("player-settings.tp-ifvillage-onjoin") == true) {
						Location locBase = pstored.getVillage().locBase();
						p.teleport(locBase);
					}
				}

				// Messaggio quando join
				if (CraftOfClans.config.getBoolean("player-settings.infovillage-onjoin")) {
					if (pstored.hasVillage()) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
							public void run() {
								p.sendMessage(Color.message(CraftOfClansM.getString("messages.village-info-welcomeback")));
								for (String string : pstored.getVillage().info()) {
									p.sendMessage(string);
								}
							}
						}, 20 * 2);
					}
				}
			}
		});

		// Ripristino del villaggio (esempio protezione delle mura)
		PlayerStored pstored = new PlayerStored(p);
		if (!pstored.hasVillage()) {
			return;
		}

		CraftOfClans.getInstance().getServer().getScheduler().runTaskAsynchronously(CraftOfClans.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().runTask(CraftOfClans.getInstance(), new VillageRestoreJoin(pstored.getVillage()));
			}
		});
	}

	@EventHandler
	public void onPlayerMoveExpander(PlayerMoveEvent event) {

		// controllo se ha l'espander attivo
		if (haveExpander.containsKey(event.getPlayer().getName())) {
			if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
				return;
			}
			// Controllo se il chunk è già stato claimato
			if (ExpanderRegister.isPresent(event.getPlayer().getLocation().getChunk())) {
				return;
			}
			final PlayerStored p = new PlayerStored(event.getPlayer());
			VillageId map = MapInfo.getVillage(p.get().getLocation());
			if (map.isOwner(p)) {

				// Controllo se ci sono espansioni vicine
				Chunk to_expand = event.getPlayer().getLocation().getChunk();
				World world = Bukkit.getWorld("clansworld");
				Chunk next_west = world.getChunkAt(to_expand.getX() - 1, to_expand.getZ());
				Chunk next_south = world.getChunkAt(to_expand.getX(), to_expand.getZ() + 1);
				Chunk next_east = world.getChunkAt(to_expand.getX() + 1, to_expand.getZ());
				Chunk next_north = world.getChunkAt(to_expand.getX(), to_expand.getZ() - 1);

				if (!ExpanderRegister.isPresent(next_west) && !ExpanderRegister.isPresent(next_south) && !ExpanderRegister.isPresent(next_east) && !ExpanderRegister.isPresent(next_north)) {
					// I chunk vicini non sono claimati
					return;
				}

				// Dopo aver effettuato i controlli, posso aggiungere una nuova espansione
				Expander expander = map.getExpander(event.getPlayer().getLocation().getChunk());
				if (expander.register(p) || expander.has_not_resources) {
					haveExpander.remove(event.getPlayer().getName());
				}

			} else {
				haveExpander.remove(event.getPlayer().getName());
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.expander-disactived")));
			}
		}
	}

	@EventHandler
	public void onPlayerChangeWorldExpander(PlayerChangedWorldEvent event) {
		// Controllo se il giocatore ha attivata la modalità espansore
		if (haveExpander.containsKey(event.getPlayer().getName())) {
			haveExpander.remove(event.getPlayer().getName());
			event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.expander-disactived")));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Controllo se il giocatore ha attivata la modalità espansore
		if (haveExpander.containsKey(event.getPlayer().getName())) {
			haveExpander.remove(event.getPlayer().getName());
		}

		// Controllo se il giocatore aveva il permesso per alcuni villaggi
		if (PlayerListener.teletrasport_eccezioni.containsKey(event.getPlayer())) {
			PlayerListener.teletrasport_eccezioni.remove(event.getPlayer());
			PlayerListener.teletrasport_eccezioni_primarie.remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerPlaceHandler(PlayerInteractEvent event) {

		if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}

		// Sfratto per questa funzione, se il giocatore sta eseguendo un attacco
		// si prosegue su AttackerListener.java
		if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
			return;
		}

		Player p = event.getPlayer();
		if (p.getInventory().getItemInMainHand() != null && event.hasBlock()) {

			ItemStack item_in_hand = p.getInventory().getItemInMainHand();
			PlaceHandler shop = new PlaceHandler(item_in_hand, new PlayerStored(p), event.getClickedBlock().getLocation());

			if (shop.isPresent()) {
				event.setCancelled(true);

				if (shop.getResponse()) {

					if (p.getInventory().getItemInMainHand().getAmount() > 1) {
						p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
					} else {
						p.getInventory().removeItem(item_in_hand);
					}
					return;

				} else {
					if (shop.is_cancelled) {
						return;
					}
				}
			}
		}

		if (PlacerListener.eccezzioni.containsKey(p)) {
			PlacerListener.eccezzioni.remove(p);
		}

		if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		// Se il player non è shiftato
		if (event.getClickedBlock() != null && !p.isSneaking()) {
			if (MapInfo.getStructures(event.getClickedBlock().getLocation()) != null) {
				event.setCancelled(true);

				StructuresId id = MapInfo.getStructures(event.getClickedBlock().getLocation());
				if (!id.getVillage().isOwner(new PlayerStored(event.getPlayer()))) {
					// La struttura non è sua
					return;
				}

				if (id.hasCommandCustom()) {
					if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						id.executeCommandCustom(event.getPlayer());
						return;
					}
				}
				@SuppressWarnings("unused")
				StructuresGUI gui = new StructuresGUI(event.getPlayer(), id, event.getClickedBlock());
			}
		}
	}

	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer() == null) {
			return;
		}
		if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}
		if (event.getBlock() != null) {

			// Sfratto per questa funzione, se il giocatore sta eseguendo un attacco
			// si prosegue su AttackerListener.java
			if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
				return;
			}

			// Controllo se il blocco è una barriera
			if (event.getBlock().getType().equals(Material.BARRIER)) {
				event.setCancelled(true);
				return;
			}

			VillageId villo = MapInfo.getVillage(event.getBlock().getLocation());
			if (villo == null && !CraftOfClans.freemode) {
				event.setCancelled(true);
				return;
			}

			PlayerStored player = new PlayerStored(event.getPlayer());

			if (villo != null && !villo.isOwner(player)) {
				player.sendMessage(Color.message(CraftOfClansM.getString("messages.build-nothis")));
				event.setCancelled(true);
				return;
			}

			if (villo != null) {
				if (ExpanderRegister.isPresent(event.getBlock().getLocation().getChunk())) {
					if (MapInfo.getStructures(event.getBlock().getLocation()) != null) {
						event.setCancelled(true);
						StructuresId id = MapInfo.getStructures(event.getBlock().getLocation());
						@SuppressWarnings("unused")
						StructuresGUI gui = new StructuresGUI(event.getPlayer(), id, event.getBlock());
					}

					// Se per rimuovere l'erba bisogna pagare
					if (!CraftOfClans.config.getBoolean("expand.grass-removal-automatically-onexpand")) {
						Block blocco = event.getBlock();
						if (MapInfo.getErbaVillage().contains(blocco.getType())) {
							event.setCancelled(true);
							
							String elabRes = MapInfo.getElabGrassRemoval(player);
							if (elabRes != null) {
								blocco.setType(Material.AIR);
								
								event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.grass-removed-bypaying").replace("%1%", elabRes)));
								player.get().playSound(player.get().getLocation(), Sounds.ORB_PICKUP.bukkitSound(), 1, 1);
								return;
							}
						}
					}

					if (!VillageSettings.isPlayerCanBuildOwn()) {
						event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.build-not-allowed")));
						event.setCancelled(true);
						return;
					}
					if (event.getBlock().getY() < VillageSettings.getHeight(villo)) {
						event.setCancelled(true);
					}
				} else {
					player.sendMessage(Color.message(CraftOfClansM.getString("messages.build-notexpanded")));
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer() == null) {
			return;
		}
		if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}
		if (event.getBlock() != null) {
			// Sfratto per questa funzione, se il giocatore sta eseguendo un attacco
			if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
				return;
			}
			VillageId villo = MapInfo.getVillage(event.getBlock().getLocation());
			if (villo == null && !CraftOfClans.freemode) {
				event.setCancelled(true);
				return;
			}

			PlayerStored player = new PlayerStored(event.getPlayer());
			if (villo != null && !villo.isOwner(player)) {
				player.sendMessage(Color.message(CraftOfClansM.getString("messages.build-nothis")));
				event.setCancelled(true);
				return;
			}
			if (villo != null && !ExpanderRegister.isPresent(event.getBlock().getLocation().getChunk())) {
				player.sendMessage(Color.message(CraftOfClansM.getString("messages.build-notexpanded")));
				event.setCancelled(true);
			}
			if (villo != null && MapInfo.getStructures(event.getBlock().getLocation()) != null) {
				event.setCancelled(true);
				StructuresId id = MapInfo.getStructures(event.getBlock().getLocation());
				@SuppressWarnings("unused")
				StructuresGUI gui = new StructuresGUI(event.getPlayer(), id, event.getBlock());
			}

			// Se il giocatore può buildare di un villaggio
			if (villo != null && !VillageSettings.isPlayerCanBuildOwn()) {
				event.getPlayer().sendMessage(Color.message(CraftOfClansM.getString("messages.build-not-allowed")));
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerInterectGUI(PlayerInteractEvent e) {

		if (!e.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}
		if (!(e.getPlayer() instanceof Player)) {
			return;
		}

		Player p = e.getPlayer();
		if (e.hasBlock()) {
			if (e.getClickedBlock() != null) {

				if (AttackerManager.hasPlayer(e.getPlayer().getUniqueId())) {
					// Controllo se i giocatori possono aprire le chest in attacco
					if (CraftOfClans.config.getString("attack.can-interect-on-attack") != null) {
						if (CraftOfClans.config.getBoolean("attack.can-interect-on-attack")) {
							return;
						}
					}
				}

				Block blocco = e.getClickedBlock();
				if (blocco.getType().equals(Material.CHEST) || blocco.getType().equals(Material.FURNACE) || blocco.getType().equals(Material.DISPENSER) || blocco.getType().equals(Material.DROPPER)) {
					VillageId villo = MapInfo.getVillage(blocco.getLocation());
					if (villo == null) {
						return;
					}
					if (!villo.isOwner(new PlayerStored(p))) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.build-nothis")));
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerExitDoor(PlayerInteractEvent e) {
		if (!e.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}

		Player p = e.getPlayer();
		Location loc = p.getLocation();

		// Se è un muro
		if (!ExpanderRegister.isMuraExpanded(loc.getChunk())) {
			return;
		}

		if (e.getAction().equals(Action.PHYSICAL) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			VillageId villo = MapInfo.getVillage(loc);
			if (villo == null) {
				return;
			}

			if (CraftOfClans.freemode) {
				// IN freemode il player non proprietario non può entrare nel villaggio
				if (!villo.owner.equals((OfflinePlayer) p)) {
					e.setCancelled(true);

					if (e.getAction().equals(Action.PHYSICAL)) {

						// Lo mando in spectator mode
						if (!SpectatorMode.inSpectator(p)) {
							SpectatorMode.sendInSpect(p, villo);
						}
					}
				}
			} else {
				
				// Gli oppati possono uscire se non stanno attaccando
				if (p.isOp()) {
					if (!AttackerManager.hasPlayer(p.getUniqueId())) {
						return;
					}
				}

				// Nel caso del flatworld nessuno può uscire dalla porta
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityExitDoor(EntityInteractEvent e) {
		if (!e.getEntity().getWorld().getName().equals("clansworld")) {
			return;
		}

		Entity ent = e.getEntity();
		Location loc = ent.getLocation();

		// Se è un muro o no
		if (!ExpanderRegister.isMuraExpanded(loc.getChunk())) {
			return;
		}

		VillageId villo = MapInfo.getVillage(loc);
		if (villo == null) {
			return;
		}
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String format = e.getFormat();
		if (format.contains("[coc_nameclan]")) {
			PlayerStored player = new PlayerStored(e.getPlayer());
			String name = player.hasClan() ? player.getClan().getName() : "";
			e.setFormat(format.replace("[coc_nameclan]", name));
		}
		if (format.contains("{coc_player_clan}")) {
			PlayerStored pstored = new PlayerStored(e.getPlayer().getUniqueId());
			String nome_clan = pstored != null && pstored.isExist() && pstored.hasClan() ? pstored.getClan().getName() : "";
			e.setFormat(format.replace("{coc_player_clan}", nome_clan));
		}

		// Player tag / ping tra i di loro
		if (e.getMessage().contains("@")) {
			if (CraftOfClans.config.getBoolean("player-tag.enable")) {

				PlayerStored pstored_from = new PlayerStored(e.getPlayer());

				for (Player pchat : _Number.getOnlinePlayers()) {
					if (e.getMessage().contains("@" + pchat.getName())) {

						PlayerStored ptagged = new PlayerStored(pchat);

						// Format del tag
						String format_tag = CraftOfClans.config.getString("player-tag.tag-format");
						format_tag = format_tag.replace("{player_name}", pchat.getName());
						format_tag = Color.translate(format_tag);

						e.setMessage(e.getMessage().replaceAll("@" + pchat.getName(), format_tag));

						// Suono
						String sound_tag;
						if ((pstored_from.hasClan() && ptagged.hasClan()) && (pstored_from.getClan().equals(ptagged.getClan()))) {
							sound_tag = CraftOfClans.config.getString("player-tag.notify-same-clan");
						} else {
							// Stesso clan
							sound_tag = CraftOfClans.config.getString("player-tag.notify-sound");
						}
						pchat.playSound(pchat.getLocation(), Sound.valueOf(sound_tag), 1.0F, 1.0F);
					}
				}
			}
		}
	}

	public static HashMap<Player, VillageId> teletrasport_eccezioni = new HashMap<Player, VillageId>();
	// Il primo teletrasporto è permesso
	public static HashMap<Player, VillageId> teletrasport_eccezioni_primarie = new HashMap<Player, VillageId>();

	@EventHandler
	public void onPlayerTeleport(PlayerMoveEvent event) {

		if (event.getPlayer().getWorld().getName().equals("clansworld")) {
			if (!CraftOfClans.freemode) {
				// Eccezioni per il teletrasporto tramite il comando /coc tp <player name>
				if (event.getPlayer().isOp()) {
					return;
				}
				// Se il giocatore è in attacco
				if (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) {
					return;
				}

				VillageId villo = new PlayerStored(event.getPlayer()).getVillage();
				VillageId destinazione = MapInfo.getVillage(event.getTo());

				if (PermissionUtil._has(event.getPlayer(), _Permission.PERM_TP_OTHER)) {
					// Controllo se il giocatore ha questo villaggio
					if (teletrasport_eccezioni.containsKey(event.getPlayer())) {
						if (teletrasport_eccezioni.get(event.getPlayer()).equals(destinazione)) {

							// Controllo se il villaggio è sotto attacco
							VillageId villaggiotar = teletrasport_eccezioni.get(event.getPlayer());
							if (!villaggiotar.isAttacked()) {
								return;
							}
						}
					}
				}

				if (destinazione != null && villo != null) {
					if (villo.equals(destinazione) == false) {
						villo.tp(event.getPlayer());
						// Elimino se era nelle eccezioni
						if (teletrasport_eccezioni.containsKey(event.getPlayer())) {
							PlayerListener.teletrasport_eccezioni.remove(event.getPlayer());
							PlayerListener.teletrasport_eccezioni_primarie.remove(event.getPlayer());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		/*
		 * if (event.getPlayer().getWorld().getName().equals("clansworld")) { //
		 * Eccezioni per il teletrasporto tramite il comando /coc tp <player name> if
		 * (event.getPlayer().isOp()) { return; }
		 * 
		 * // Se il giocatore è in attacco if
		 * (AttackerManager.hasPlayer(event.getPlayer().getUniqueId())) { return; }
		 * 
		 * // Se il giocatore non è in modalità visita if
		 * (teletrasport_eccezioni.containsKey(event.getPlayer())) {
		 * event.setCancelled(true); } }
		 */
	}

	@EventHandler
	public void onPlayerPickUp(EntityPickupItemEvent event) {
		/*
		 * if (event.getEntity() instanceof Player) { Player p = (Player)
		 * event.getEntity(); if (p.getWorld().getName().equals("clansworld")) {
		 * 
		 * // Eccezioni per il teletrasporto tramite il comando /coc tp <player name> if
		 * (p.isOp()) { return; }
		 * 
		 * // Se il giocatore è in attacco if
		 * (AttackerManager.hasPlayer(p.getUniqueId())) { return; } // Se il giocatore
		 * non è in modalità visita if (teletrasport_eccezioni.containsKey(p)) {
		 * event.setCancelled(true); } } }
		 */
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (CraftOfClans.freemode) {
			return;
		}

		// Disabilito il PVP per chi sta facendo visita (sia verso lui che da lui)
		if (event.getEntity() instanceof Player) {
			Player from = (Player) event.getEntity();
			if (teletrasport_eccezioni.containsKey(from)) {
				if (event.getDamager() instanceof Player) {
					((Player) event.getDamager()).sendMessage(Color.message(CraftOfClansM.getString("messages.visit-attack-player")));
				}
				event.setCancelled(true);
			}
		}
		if (event.getDamager() instanceof Player) {
			Player to = (Player) event.getDamager();
			if (teletrasport_eccezioni.containsKey(to)) {
				if (event.getEntity() instanceof Player) {
					to.sendMessage(Color.message(CraftOfClansM.getString("messages.visit-attacker-warning")));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		// Tolgo dalla modalità spettatore primarie
		if (PlayerListener.teletrasport_eccezioni_primarie.containsKey(event.getPlayer())) {
			PlayerListener.teletrasport_eccezioni_primarie.remove(event.getPlayer());
			return;
		}
		// Tolgo dalla modalità spettatore
		if (PlayerListener.teletrasport_eccezioni.containsKey(event.getPlayer())) {
			PlayerListener.teletrasport_eccezioni.remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onTestEntityDamage(EntityDamageByEntityEvent event) {

		// Controllo se il fuoco amico è abilitato
		if (CraftOfClansClan.enabled_fuoco_amico) {
			return;
		}

		// Controllo se il giocatore è nel suo villaggio
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();

			VillageId villo = MapInfo.getVillage(p.getLocation());
			if (villo != null) {
				if (villo.owner.equals((OfflinePlayer) p)) {
					// è nel suo villaggio
					event.setCancelled(true);

					if (event.getDamager() instanceof Player) {
						event.getDamager().sendMessage(Color.message(CraftOfClansM.getString("messages.player-in-own-territory")));
					}
				}
			}
		}

		if (event.getDamager() instanceof Player) {
			if (event.getEntity() instanceof Player) {

				PlayerStored pstored_from = new PlayerStored((Player) event.getDamager());
				PlayerStored pstored_taget = new PlayerStored((Player) event.getEntity());

				if (pstored_from.hasClan() && pstored_taget.hasClan()) {
					if (pstored_from.getClan().equals(pstored_taget.getClan())) {
						pstored_from.sendMessage(Color.message(CraftOfClansM.getString("clan.pvp-friendly-fire")));
						event.setCancelled(true);
					}
				}
			}

		} else if (event.getDamager() instanceof Arrow) {

			Arrow arrow = (Arrow) event.getDamager();
			Entity shooter = (Entity) arrow.getShooter();

			if (shooter instanceof Player && event.getEntity() instanceof Player) {

				PlayerStored pstored_from = new PlayerStored((Player) shooter);
				PlayerStored pstored_taget = new PlayerStored((Player) event.getEntity());

				if (pstored_from.hasClan() && pstored_taget.hasClan()) {
					if (pstored_from.getClan().equals(pstored_taget.getClan())) {
						pstored_from.sendMessage(Color.message(CraftOfClansM.getString("clan.pvp-friendly-fire")));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onLeaveSpect(PlayerQuitEvent event) {
		if (SpectatorMode.inSpectator(event.getPlayer())) {
			SpectatorMode.remove(event.getPlayer(), true);
		}
	}

	@EventHandler
	public void onTrasportSpect(PlayerTeleportEvent event) {
		if (event.getPlayer().getWorld().getName().equals("clansworld")) {
			if (SpectatorMode.inSpectator(event.getPlayer())) {
				if (event.getCause().equals(TeleportCause.SPECTATE)) {
					return;
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreprocessSpect(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if (p.isOp()) {
			return;
		}

		if (SpectatorMode.inSpectator(event.getPlayer())) {
			String comando = event.getMessage();

			boolean is_valid = false;

			List<String> bCmds = CraftOfClans.config.get().getStringList("spectator-mode.commands-enable-during-spectator");
			for (String bCmd : bCmds) {
				if (!bCmd.startsWith("/")) {
					bCmd = "/" + bCmd;
				}
				if (comando.toLowerCase().startsWith(bCmd)) {
					is_valid = true;
				}
			}

			if (!is_valid) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-cmd-not-allowd").replace("%1%", "/coc leave")));
				event.setCancelled(true);
			}
		}
	}
}
