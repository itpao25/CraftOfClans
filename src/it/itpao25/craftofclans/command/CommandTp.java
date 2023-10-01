package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerListener;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTp {
	public CommandTp(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}

		final Player p = (Player) sender;

		if (args.length == 1) {

			// comando /coc tp
			if (!PermissionUtil._has(sender, _Permission.PERM_TP) && !sender.isOp()) {
				_String.nopermission(sender);
				return;
			}

			PlayerStored player = new PlayerStored(p);

			if (player.hasVillage()) {
				player.getVillage().tp(p);
				player.sendMessage(Color.message(CraftOfClansM.getString("messages.tp-player-village")));

				if (PlayerListener.teletrasport_eccezioni.containsKey(p)) {
					PlayerListener.teletrasport_eccezioni.remove(p);
					PlayerListener.teletrasport_eccezioni_primarie.remove(p);
				}
				return;
			}

			player.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village")));
			return;

		} else if (args.length == 2) {

			// comando /coc tp <player name>
			if (!PermissionUtil._has(sender, _Permission.PERM_TP_OTHER) && !sender.isOp()) {
				_String.nopermission(sender);
				return;
			}

			String player = args[1];

			@SuppressWarnings("deprecation")
			OfflinePlayer pinvited_offline = Bukkit.getOfflinePlayer(player);
			if (pinvited_offline == null) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}

			PlayerStored pstored_invited = new PlayerStored(pinvited_offline.getUniqueId());
			if (pstored_invited == null || !pstored_invited.isExist()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}

			// Controllo se il giocatore ha già un villaggio
			if (pstored_invited.hasVillage() == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village2")));
				return;
			}

			// Controllo se il villaggio di destinazione è sotto attacco
			if (pstored_invited.getVillage().isAttacked()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.visit-village-in-attack")));
				return;
			}

			// Teletrasporto il player nel villaggio
			pstored_invited.getVillage().tp(p);

			if (PlayerListener.teletrasport_eccezioni.containsKey(p)) {
				PlayerListener.teletrasport_eccezioni.remove(p);
				PlayerListener.teletrasport_eccezioni_primarie.remove(p);
			}

			PlayerListener.teletrasport_eccezioni_primarie.put(p, pstored_invited.getVillage());
			PlayerListener.teletrasport_eccezioni.put(p, pstored_invited.getVillage());

			sender.sendMessage(Color.message(CraftOfClansM.getString("messages.tp-other-player-village").replace("%1%", pstored_invited.getName())));
			return;

		}
		sender.sendMessage(Color.message("&eUse /coc tp [player name] to go in your village"));
	}
}
