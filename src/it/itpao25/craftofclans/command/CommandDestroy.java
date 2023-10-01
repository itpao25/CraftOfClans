package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillagesHandler;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CommandDestroy {

	public CommandDestroy(CommandSender sender, String[] args) {

		if (!PermissionUtil._has(sender, _Permission.PERM_DESTROY) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}

		if (args.length == 2) {
			String player = args[1];

			@SuppressWarnings("deprecation")
			OfflinePlayer pdestroy_offline = Bukkit.getOfflinePlayer(player);
			if (pdestroy_offline == null) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}

			PlayerStored pstored_destroy = new PlayerStored(pdestroy_offline.getUniqueId());
			if (pstored_destroy == null || !pstored_destroy.isExist()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}

			// Controllo se il giocatore ha già un villaggio
			if (pstored_destroy.hasVillage() == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village2")));
				return;
			}

			// Controllo se il villaggio di destinazione è sotto attacco
			if (pstored_destroy.getVillage().isAttacked()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.visit-village-in-attack")));
				return;
			}
			
			if (VillagesHandler.villages_to_destroy.containsKey(sender)) {
				VillagesHandler.villages_to_destroy.remove(sender);
			}
			HashMap<Long, VillageId> hashmap = new HashMap<Long, VillageId>();
			hashmap.put(System.currentTimeMillis(), pstored_destroy.getVillage());
			
			VillagesHandler.villages_to_destroy.put(sender, hashmap);
			sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.confirm-delete-structure")));
			
		} else {
			sender.sendMessage(Color.message("&eUse /coc destroy <player> to reset and destroy a village"));
		}
	}
}
