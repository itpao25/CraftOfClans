package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CommandShow {
	@SuppressWarnings("deprecation")
	public CommandShow(CommandSender sender, String[] args) {
		if (args.length == 2) {
			String player_name = args[1];
			if (Bukkit.getOfflinePlayer(player_name) != null) {
				OfflinePlayer poffline = Bukkit.getOfflinePlayer(player_name);
				PlayerStored pstored = new PlayerStored(poffline.getUniqueId());
				if (pstored.isExist()) {
					// Lista delle informazioni
					String clan_name = pstored.hasClan() ? pstored.getClan().getName() : Color.translate(CraftOfClansM.getString("clan.clanless"));

					String title = CraftOfClansM.getString("messages.player-info-title") != null ? CraftOfClansM.getString("messages.player-info-title") : "&6=========== &e[ Player info ] &6===========";
					String name = CraftOfClansM.getString("messages.player-info-name") != null ? CraftOfClansM.getString("messages.player-info-name").replace("%1%", pstored.getName()) : "";
					String clan = CraftOfClansM.getString("messages.player-info-clan") != null ? CraftOfClansM.getString("messages.player-info-clan").replace("%1%", clan_name) : "";
					String trophies = CraftOfClansM.getString("messages.player-info-trophies") != null ? CraftOfClansM.getString("messages.player-info-trophies").replace("%1%", pstored.getTrofei() + "") : "";
					String battles_won = CraftOfClansM.getString("messages.player-info-battle-won") != null ? CraftOfClansM.getString("messages.player-info-battle-won").replace("%1%", pstored.getWin() + "") : "";
					String battles_lost = CraftOfClansM.getString("messages.player-info-battle-lost") != null ? CraftOfClansM.getString("messages.player-info-battle-lost").replace("%1%", pstored.getLost() + "") : "";

					sender.sendMessage(Color.translate(title));
					sender.sendMessage("");
					sender.sendMessage(Color.translate(name));
					sender.sendMessage(Color.translate(clan));
					sender.sendMessage(Color.translate(trophies));
					sender.sendMessage(Color.translate(battles_won));
					sender.sendMessage(Color.translate(battles_lost));
					sender.sendMessage("");

					return;
				}
			}
			// Player not found
			sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
			return;
		}
		sender.sendMessage(Color.message("&ePlease use /coc show <player name>"));
	}
}
