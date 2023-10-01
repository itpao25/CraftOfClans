package it.itpao25.craftofclans.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.itpao25.craftofclans.api.ClanPlayerJoinEvent;
import it.itpao25.craftofclans.api.ClanPlayerLeave;
import it.itpao25.craftofclans.clans.ClanObject;
import it.itpao25.craftofclans.clans.ClansInvite;
import it.itpao25.craftofclans.clans.ClansManager;
import it.itpao25.craftofclans.clans.ClansTypes;
import it.itpao25.craftofclans.clans.ClansUIListeners;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.TabText;
import it.itpao25.craftofclans.util._Number;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClan implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return false;
		}
		if (args.length < 1) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.clan-use")).replace("%1%", "/clan < create | invite | join | show | list | stats | leave | kick | name | desc | min-trophies >"));
			return false;
		}

		Player p = (Player) sender;
		switch (args[0]) {
		case "create":
			// Creazione di un nuovo clan - Apro la GUI per il wizard
			ClansUIListeners.getRender(p);
			break;
		case "invite":

			if (args.length == 2) {
				PlayerStored pstored = new PlayerStored(p.getUniqueId());
				if (!pstored.hasClan()) {
					// Il Giocatore non ha un clan
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
					return false;
				}
				// Controllo se è l'owner del clan
				if (!pstored.hasOwnerClan()) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-owner-clan")));
					return false;
				}
				// Clan del giocatore
				ClanObject clanObj = pstored.getClan();

				// Controllo se il giocatore che vuole invitare è online
				String name_invited = args[1];
				@SuppressWarnings("deprecation")
				OfflinePlayer pinvited_offline = Bukkit.getOfflinePlayer(name_invited);
				if (pinvited_offline == null || pinvited_offline.isOnline() == false) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-offline")));
					return false;
				}

				Player pinvited = (Player) pinvited_offline;
				PlayerStored pstored_invited = new PlayerStored(pinvited.getUniqueId());

				// Player già presente in questo clan
				if (pstored_invited.hasClan() && pstored_invited.getClan().getId() == clanObj.getId()) {
					sender.sendMessage(Color.message("&6This player is already a member of your clan"));
					return false;
				}
				if (ClansInvite.hasInvite(pstored_invited, clanObj)) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-already-invited")));
					return false;
				}

				// Aggiungo l'invito
				ClansInvite.addInvite(pstored_invited, clanObj);
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-invite-success").replace("%1%", pstored_invited.getName())));
				pstored_invited.sendMessage(Color.message(CraftOfClansM.getString("clan.message-player-invited").replace("%1%", sender.getName()).replace("%2%", clanObj.getName())));

				return true;
			}

			sender.sendMessage(Color.message("&ePlease use /clan invite <player name>"));
			break;
		case "join":

			// Accetto la richiesta (Specificando il nome del clan)
			if (args.length == 2) {
				PlayerStored pstored = new PlayerStored(p.getUniqueId());
				if (pstored.hasClan()) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-already-in-clan")));
					return false;
				}
				String nome_clan = args[1];
				ClanObject clanObj = ClanObject.getByName(nome_clan);
				if (clanObj != null) {
					// Controllo se i trofei minimi sono corretti
					if (pstored.getTrofei() < clanObj.getMinTrophies()) {
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-trophies-insufficient").replace("%1%", clanObj.getMinTrophies() + "")));
						return false;
					}
					// Controllo il numero dei giocatori
					if (clanObj.getMembers().size() + 1 > ClansManager.getMaxMembers()) {
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-max-members-reached")));
						return false;
					}

					// Tutti i giocatori possono entrare
					if (clanObj.getType().equals(ClansTypes.ALL_CAN_JOIN)) {
						// Aggiungo il player al clan
						pstored.setIdClan(clanObj.getId());
						clanObj.addPlayerToMemory(pstored.getUUID());
						// Accetto l'invito (rimuovendo)
						ClansInvite.accept(pstored);

						clanObj.sendMessageOnlineMembers(Color.message(CraftOfClansM.getString("clan.message-join-success").replace("%1%", pstored.getName()).replace("%2%", clanObj.getName())));

						ClanPlayerJoinEvent event = new ClanPlayerJoinEvent(pstored.get());
						Bukkit.getServer().getPluginManager().callEvent(event);

						return true;
					} else if (clanObj.getType().equals(ClansTypes.JOIN_WITH_INVITE)) {
						// Se il clan è solo su invito
						if (ClansInvite.hasInvite(pstored, clanObj)) {
							// Aggiungo il giocatore al clan
							pstored.setIdClan(clanObj.getId());
							clanObj.addPlayerToMemory(pstored.getUUID());

							// Accetto l'invito (rimuovendo)
							ClansInvite.accept(pstored);

							ClanPlayerJoinEvent event = new ClanPlayerJoinEvent(pstored.get());
							Bukkit.getServer().getPluginManager().callEvent(event);

							clanObj.sendMessageOnlineMembers(Color.message(CraftOfClansM.getString("clan.message-join-success").replace("%1%", pstored.getName()).replace("%2%", clanObj.getName())));
							return true;
						}
						// Non è invitato
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-player-not-invited")));
					}
					return false;
				}
				// Il clan non esiste
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-clan-not-exists")));
				return false;
			}
			sender.sendMessage(Color.message("&ePlease use /clan join <clan name>"));
			break;
		case "leave":

			// Il player esce dal clan
			PlayerStored pstored = new PlayerStored(p.getUniqueId());
			if (pstored.hasClan()) {
				pstored.getClan().sendMessageOnlineMembers(Color.message(CraftOfClansM.getString("clan.message-quit-success").replace("%1%", pstored.getName()).replace("%2%", pstored.getClan().getName())));
				// Controllo se il giocatore è il creatore del clan
				if (pstored.getClan().getIDOwner() == pstored.getId()) {
					// Il giocatore owner sta provando a lasciare il clan
					pstored.getClan().tryToDisband(pstored.getId());
				}
				pstored.setIdClan(0);

				ClanPlayerLeave event = new ClanPlayerLeave();
				event.setPlayerLeaved(pstored.get());
				Bukkit.getServer().getPluginManager().callEvent(event);

				return false;
			}

			sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
			break;

		case "show":
			// Mostro le informazioni del clan
			ClanObject clanObj = null;
			boolean command_valid = false;

			if (args.length == 2) {
				String nome_clan = args[1];
				clanObj = ClanObject.getByName(nome_clan);

				command_valid = true;
			} else if (args.length == 1) {
				// Mostro le informazioni del proprio clan
				PlayerStored pstored1 = new PlayerStored(p);
				if (pstored1.hasClan()) {
					clanObj = pstored1.getClan();
				} else {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
					return false;
				}

				command_valid = true;
			}
			if (command_valid) {
				if (clanObj != null) {

					String title = CraftOfClansM.getString("messages.clan-info-title") != null ? CraftOfClansM.getString("messages.clan-info-title") : "&6=========== &e[ Clan info ] &6===========";
					String name = CraftOfClansM.getString("messages.clan-info-name") != null ? CraftOfClansM.getString("messages.clan-info-name").replace("%1%", clanObj.getName()) : "";
					String description = CraftOfClansM.getString("messages.clan-info-description") != null ? CraftOfClansM.getString("messages.clan-info-description").replace("%1%", clanObj.getDesc()) : "";
					String min_trophies = CraftOfClansM.getString("messages.clan-info-mintrophies") != null ? CraftOfClansM.getString("messages.clan-info-mintrophies").replace("%1%", clanObj.getMinTrophies() + "") : "";
					String members = CraftOfClansM.getString("messages.clan-info-members") != null ? CraftOfClansM.getString("messages.clan-info-members") : "";
					String total_trophies = CraftOfClansM.getString("messages.clan-info-total-trophies") != null ? CraftOfClansM.getString("messages.clan-info-total-trophies").replace("%1%", clanObj.getTotalTrophies() + "") : "";
					String name_string = CraftOfClansM.getString("messages.clan-info-name-player") != null ? CraftOfClansM.getString("messages.clan-info-name-player") : "&7Name";
					String name_trophies = CraftOfClansM.getString("messages.clan-info-trophies") != null ? CraftOfClansM.getString("messages.clan-info-trophies") : "&7Trophies";

					name_string = name_string.replace("`", "");
					name_trophies = name_trophies.replace("`", "");

					sender.sendMessage(Color.translate(title));
					sender.sendMessage("");
					sender.sendMessage(Color.translate(name));
					sender.sendMessage(Color.translate(description));
					sender.sendMessage(Color.translate(min_trophies));
					sender.sendMessage(Color.translate(members));
					String multilineString = Color.translate("     " + name_string + "`" + name_trophies + "\n");
					for (PlayerStored pstored_showing : clanObj.getMembers()) {
						if (pstored_showing.getId() == clanObj.getIDOwner()) {
							multilineString += "     " + (Color.translate("&4" + pstored_showing.getName() + "`&e" + pstored_showing.getTrofei() + "\n"));
							continue;
						}
						multilineString += "     " + (Color.translate("&e" + pstored_showing.getName() + "`&e" + pstored_showing.getTrofei() + "\n"));
					}
					TabText tt = new TabText(multilineString);
					tt.setTabs(30); // horizontal tabs positions
					String printedText = tt.getPage(0, false);
					sender.sendMessage(printedText);
					sender.sendMessage(Color.translate(total_trophies));
					sender.sendMessage("");
				} else {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-clan-not-exists")));
				}
				return false;
			}
			sender.sendMessage(Color.message("&ePlease use /clan show [clan name]"));
			break;

		case "kick":
			// Espello un giocatore dal clan
			if (args.length == 2) {
				PlayerStored pstored1 = new PlayerStored(p.getUniqueId());
				if (pstored1.hasClan()) {
					if (!pstored1.hasOwnerClan()) {
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-owner-clan")));
						return false;
					}
					String name_player = args[1];
					if (pstored1.getClan().hasMemberByName(name_player)) {
						PlayerStored targetstored = pstored1.getClan().getMemberByName(name_player);
						if (targetstored.getId() == pstored1.getId()) {
							pstored1.sendMessage(Color.message("&cYou must use &6/coc clan leave &cto quit"));
							return false;
						}

						ClanPlayerLeave event1 = new ClanPlayerLeave();
						targetstored.setIdClan(0);
						if (targetstored.isOnline()) {
							// Invio il messaggio è stato cacciato
							targetstored.sendMessage(Color.message(CraftOfClansM.getString("clan.message-player-kicked").replace("%1%", pstored1.getClan().getName())));
							event1.setPlayerLeaved(targetstored.get());
						}
						Bukkit.getServer().getPluginManager().callEvent(event1);

						pstored1.getClan().sendMessageOnlineMembers(Color.message(CraftOfClansM.getString("clan.message-player-kicked-success").replace("%1%", targetstored.getName()).replace("%2%", pstored1.getName())));
						return true;
					}
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-player-not-inclan")));
					return false;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
				return false;
			}
			sender.sendMessage(Color.message("&ePlease use /clan kick <player name>"));
			break;

		case "disband":
			// Disbando il clan
			PlayerStored pstored1 = new PlayerStored(p.getUniqueId());
			if (pstored1.hasClan()) {
				if (!pstored1.hasOwnerClan()) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-owner-clan")));
					return false;
				}

				// Conferma del disband
				if (!ClansUIListeners.waiting_disband.containsKey(p)) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-confirm-disband")));
					// Inserisco richiesta per la conferma
					// Il disband sarà gestito dal file ClansUIListeners
					ClansUIListeners.waiting_disband.put(p, false);
				}

				return true;
			}
			sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
			break;

		case "name":
			// Cambio il nome del clan
			if (args.length == 2) {
				PlayerStored pstored2 = new PlayerStored(p.getUniqueId());
				if (pstored2.hasClan()) {
					if (!pstored2.hasOwnerClan()) {
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-owner-clan")));
						return false;
					}
					String new_name = args[1];
					// Controllo se il nome è già stato usato
					if (ClansManager.existClan(new_name.toLowerCase())) {
						p.sendMessage(Color.message(CraftOfClansM.getString("clan.message-clan-already-exists")));
						return false;
					}
					ClanObject clan = pstored2.getClan();
					clan.updateData("name", new_name);

					clan.sendMessageOnlineMembers(Color.message("&6" + p.getName() + " &echanged your clan name to &6" + new_name));

					return true;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
				return false;
			}
			sender.sendMessage(Color.message("&ePlease use /clan name <new name>"));
			break;
		case "desc":
			// Cambio la descrizione del clan
			if (args.length == 2) {
				PlayerStored pstored2 = new PlayerStored(p.getUniqueId());
				if (pstored2.hasClan()) {
					if (!pstored2.hasOwnerClan()) {
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-owner-clan")));
						return false;
					}
					String new_desc = args[1];
					ClanObject clan = pstored2.getClan();
					clan.updateData("desc", new_desc);

					clan.sendMessageOnlineMembers(Color.message("&6" + p.getName() + " &echanged your clan description to &6" + new_desc));
					return true;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
				return false;
			}
			sender.sendMessage(Color.message("&ePlease use /clan desc <new desc>"));
			break;
		case "min-trophies":
			// Imposto il numero minimo dei trofei
			if (args.length == 2) {
				PlayerStored pstored2 = new PlayerStored(p.getUniqueId());
				if (pstored2.hasClan()) {
					if (!pstored2.hasOwnerClan()) {
						sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-owner-clan")));
						return false;
					}
					if (!_Number.isNumero(args[1])) {
						sender.sendMessage(Color.message("&eYou must use an number to indicate the trophies"));
						return false;
					}
					int new_int = Integer.parseInt(args[1]);
					ClanObject clan = pstored2.getClan();
					clan.updateData("min_trophies", new_int);

					clan.sendMessageOnlineMembers(Color.message("&6" + p.getName() + "&e changed your clan minimum trophies to join to &6" + new_int));
					return true;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-not-have-clan")));
				return false;
			}
			sender.sendMessage(Color.message("&ePlease use /clan desc <new desc>"));
			break;
		case "list":
		case "stats":

			int pagina = args.length == 2 && _Number.isNumero(args[1]) ? Integer.parseInt(args[1]) : 1;

			ArrayList<ClanObject> clans = ClansManager.lista_clan();
			int numero_clan = clans.size();

			if (numero_clan == 0) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("clan.message-no-clan-found")));
				return false;
			}

			int pagesize = numero_clan >= 10 ? 10 : clans.size();

			int totalpage = (int) Math.ceil(numero_clan / pagesize);

			String name_string = CraftOfClansM.getString("messages.clan-info-name-player") != null ? CraftOfClansM.getString("messages.clan-info-name-player") : "&7Name";
			String name_trophies = CraftOfClansM.getString("messages.clan-info-trophies") != null ? CraftOfClansM.getString("messages.clan-info-trophies") : "&7Trophies";
			String clan_stats = CraftOfClansM.getString("messages.clan-info-stats") != null ? CraftOfClansM.getString("messages.clan-info-stats") : "&6&lClan Stats";

			name_string = name_string.replace("`", "");
			name_trophies = name_trophies.replace("`", "");

			sender.sendMessage(Color.message(clan_stats));
			sender.sendMessage("");
			String multilineString = Color.translate("     " + name_string + "`" + name_trophies + "\n");

			// Ordino l'array in base al numero dei trofei
			Collections.sort(clans, Collections.reverseOrder(new Comparator<ClanObject>() {
				@Override
				public int compare(ClanObject o1, ClanObject o2) {
					return o1.getTotalTrophies().compareTo(o2.getTotalTrophies());
				}
			}));

			// Cred il paginator (0.5.5.1)
			List<ClanObject> clans_list = clans.subList(pagesize * (pagina - 1), pagesize * pagina);

			for (ClanObject clan : clans_list) {
				multilineString += "     " + (Color.translate("&6" + clan.getName() + "`&e" + clan.getTotalTrophies() + "\n"));
			}

			TabText tt = new TabText(multilineString);
			tt.setTabs(30); // horizontal tabs positions
			String printedText = tt.getPage(0, false);
			sender.sendMessage(printedText);

			sender.sendMessage("");

			String footer = Color.translate("&6------ &e([current]&6/&e[totpage])&6 ----- Found &e&l[totalclan] &6clan(s)");
			footer = footer.replace("[current]", pagina + "");
			footer = footer.replace("[totpage]", totalpage + "");
			footer = footer.replace("[totalclan]", numero_clan + "");

			sender.sendMessage(footer);

			break;
		default:
			sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.clan-use")).replace("%1%", "/clan < create | invite | show | list | stats | leave | kick | name | desc | min-trophies >"));
			break;
		}
		return false;
	}
}
