package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfo {
	public CommandInfo(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}

		Player p = (Player) sender;
		PlayerStored player = new PlayerStored(p);
		
		p.sendMessage("");
		player.sendMessage(Color.translate(CraftOfClansM.getString("messages.player-info-title")));
		p.sendMessage("");
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-gems").replace("%1%", _Number.showNumero(player.getGems()))));
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-gold").replace("%1%", _Number.showNumero(player.getGold()))));
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-elixir").replace("%1%", _Number.showNumero(player.getElixir()))));
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-dark-elixir").replace("%1%", _Number.showNumero(player.getElixirNero()))));
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-trophies").replace("%1%", _Number.showNumero(player.getTrofei()) + "")));
		
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-attacks-win").replace("%1%", _Number.showNumero(player.getWin()) + "")));
		player.sendMessage(Color.translate(CraftOfClansM.getString("commands-syntax.info-attacks-lost").replace("%1%", _Number.showNumero(player.getLost()) + "")));
		p.sendMessage("");
		
		if(player.hasVillage()) {
			for (String string : player.getVillage().info()) {
				p.sendMessage(string);
			}
		}
		
		// player.sendMessage(Color.translate("Sei nel villaggio "+
		// MapInfo.getVillage(p.getLocation()).getID()));
	}
}
