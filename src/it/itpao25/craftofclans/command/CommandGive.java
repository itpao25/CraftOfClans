package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.api.ResourceChangeValue;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGive {
	public CommandGive(CommandSender sender, String[] args) {
		if (args.length != 4) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.give-use")).replace("%1%", "/coc give <player> < gems | gold | elixir | dark_elixir > <amount>"));
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
			if (player == null || !player.isOnline()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}
			if (!player.isOnline()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-offline")));
				return;
			}
			if (!_Number.isNumero(args[3])) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.give-use")).replace("%1%", "/coc give <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}
			double amount = Double.parseDouble(args[3]);
			if (amount <= 0) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.give-use")).replace("%1%", "/coc give <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}
			Player p = Bukkit.getPlayer(player.getName());
			if (p == null) {
				return;
			}
			PlayerStored pstored = new PlayerStored(p);
			switch (args[2].toLowerCase()) {
			case "gems":
				if (!PermissionUtil._has(sender, _Permission.PERM_GIVE_GEMS) && !PermissionUtil._has(sender, _Permission.PERM_GIVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				pstored.addGems(amount);
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-gems").replace("%1%", amount + "").replace("%2%", pstored.getName())));
				break;
			case "gold":
				if (!PermissionUtil._has(sender, _Permission.PERM_GIVE_GOLD) && !PermissionUtil._has(sender, _Permission.PERM_GIVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				pstored.addGold(amount, false);
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-gold").replace("%1%", amount + "").replace("%2%", pstored.getName())));
				break;
			case "elixir":
				if (!PermissionUtil._has(sender, _Permission.PERM_GIVE_ELIXIR) && !PermissionUtil._has(sender, _Permission.PERM_GIVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				pstored.addElixir(amount, false);
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-elixir").replace("%1%", amount + "").replace("%2%", pstored.getName())));
				break;
			case "dark_elixir":
				if (!PermissionUtil._has(sender, _Permission.PERM_GIVE_ELIXIR_NERO) && !PermissionUtil._has(sender, _Permission.PERM_GIVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				pstored.addElixirNero(amount, false);
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-elixir-dark").replace("%1%", amount + "").replace("%2%", pstored.getName())));
				break;
			default:
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.give-use")).replace("%1%", "/coc give < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}
			// Avvio l'evento che sono state cambiate le risorse
			ResourceChangeValue event = new ResourceChangeValue(p);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}
}
