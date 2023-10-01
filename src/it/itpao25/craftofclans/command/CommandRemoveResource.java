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

public class CommandRemoveResource {
	public CommandRemoveResource(CommandSender sender, String[] args) {
		if (args.length != 4) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.removeresource-use")).replace("%1%", "/coc removeresource-use <player> < gems | gold | elixir | dark_elixir > <amount>"));
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
			if (player == null) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}
			if (!_Number.isNumero(args[3])) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.removeresource-use")).replace("%1%", "/coc removeresource-use <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}
			double amount = Double.parseDouble(args[3]);
			if (amount < 0) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.removeresource-use")).replace("%1%", "/coc removeresource-use <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}

			PlayerStored pstored = new PlayerStored(player.getUniqueId());
			switch (args[2].toLowerCase()) {
			case "gems":
				if (!PermissionUtil._has(sender, _Permission.PERM_REMOVE_GEMS) && !PermissionUtil._has(sender, _Permission.PERM_REMOVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (pstored.hasGems(amount)) {
					pstored.removeGems(amount);
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-player-gems").replace("%1%", _Number.showNumero(amount)).replace("%2%", pstored.getName())));
					return;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-not-resources").replace("%1%", pstored.getName())));
				break;
			case "gold":
				if (!PermissionUtil._has(sender, _Permission.PERM_REMOVE_GOLD) && !PermissionUtil._has(sender, _Permission.PERM_REMOVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (pstored.hasGold(amount)) {
					pstored.removeGold(amount);
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-player-gold").replace("%1%", _Number.showNumero(amount)).replace("%2%", pstored.getName())));
					return;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-not-resources").replace("%1%", pstored.getName())));
				break;
			case "elixir":
				if (!PermissionUtil._has(sender, _Permission.PERM_REMOVE_ELIXIR) && !PermissionUtil._has(sender, _Permission.PERM_REMOVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (pstored.hasElixir(amount)) {
					pstored.removeElixir(amount);
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-player-elixir").replace("%1%", _Number.showNumero(amount)).replace("%2%", pstored.getName())));
					return;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-not-resources").replace("%1%", pstored.getName())));
				break;
			case "dark_elixir":
				if (!PermissionUtil._has(sender, _Permission.PERM_REMOVE_ELIXIR_NERO) && !PermissionUtil._has(sender, _Permission.PERM_REMOVE) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (pstored.hasElixirNero(amount)) {
					pstored.removeElixirNero(amount);
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-player-elixir-dark").replace("%1%", _Number.showNumero(amount)).replace("%2%", pstored.getName())));
					return;
				}
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.remove-not-resources").replace("%1%", pstored.getName())));
				break;
			default:
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.removeresource-use")).replace("%1%", "/coc removeresource-use <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}

			if (player.isOnline()) {
				Player p = Bukkit.getPlayer(player.getName());
				// Avvio l'evento che sono state cambiate le risorse
				ResourceChangeValue event = new ResourceChangeValue(p);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}
	}
}
