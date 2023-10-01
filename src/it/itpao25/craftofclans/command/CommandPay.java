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

public class CommandPay {
	public CommandPay(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can pay another user! Use instead /coc give "));
			return;
		}
		if (args.length != 4) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.pay-use")).replace("%1%", "/coc pay <player> < gems | gold | elixir | dark_elixir > <amount>"));
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
			if (player == null) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
				return;
			}
			if (!player.isOnline()) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-offline")));
				return;
			}
			if (!_Number.isNumero(args[3])) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.pay-use")).replace("%1%", "/coc play <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}
			
			double amount = Double.parseDouble(args[3]);
			if (amount <= 0) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.pay-use")).replace("%1%", "/coc play <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}
			String amount_str = _Number.showNumero(amount);
			
			Player p = Bukkit.getPlayer(player.getName());
			if (p == null) {
				return;
			}

			PlayerStored pstored = new PlayerStored(p);
			PlayerStored from = new PlayerStored((Player) sender);

			if (pstored.equals(from)) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.pay-yourself")));
				return;
			}

			switch (args[2].toLowerCase()) {
			case "gems":
				if (!PermissionUtil._has(sender, _Permission.PERM_PAY_GEMS) && !PermissionUtil._has(sender, _Permission.PERM_PAY) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (paga(pstored, from, amount, "gems")) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-gems").replace("%1%", amount_str).replace("%2%", pstored.getName())));
				}
				break;

			case "gold":
				if (!PermissionUtil._has(sender, _Permission.PERM_PAY_GOLD) && !PermissionUtil._has(sender, _Permission.PERM_PAY) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (paga(pstored, from, amount, "gold")) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-gold").replace("%1%", amount_str).replace("%2%", pstored.getName())));
				}
				break;

			case "elixir":
				if (!PermissionUtil._has(sender, _Permission.PERM_PAY_ELIXIR) && !PermissionUtil._has(sender, _Permission.PERM_PAY) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (paga(pstored, from, amount, "elixir")) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-elixir").replace("%1%", amount_str).replace("%2%", pstored.getName())));
				}
				break;

			case "dark_elixir":
				if (!PermissionUtil._has(sender, _Permission.PERM_PAY_ELIXIR_NERO) && !PermissionUtil._has(sender, _Permission.PERM_PAY) && !sender.isOp()) {
					_String.nopermission(sender);
					return;
				}
				if (paga(pstored, from, amount, "dark_elixir")) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-elixir-dark").replace("%1%", amount_str).replace("%2%", pstored.getName())));
				}
				break;

			default:
				sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.pay-use")).replace("%1%", "/coc pay <player> < gems | gold | elixir | dark_elixir > <amount>"));
				return;
			}

			// Avvio l'evento che sono state cambiate le risorse
			ResourceChangeValue event = new ResourceChangeValue(p);
			ResourceChangeValue event1 = new ResourceChangeValue(from.get());

			Bukkit.getServer().getPluginManager().callEvent(event);
			Bukkit.getServer().getPluginManager().callEvent(event1);
		}
	}
	
	public boolean paga(PlayerStored target, PlayerStored from, double amount, String tipo) {
		switch (tipo.toLowerCase()) {
		case "gems":
			if (from.hasGems(amount)) {
				if (target.addGems(amount) == false) {
					if (target.getLastResultFull()) {
						from.sendMessage(Color.message(CraftOfClansM.getString("messages.pay-not-success")));
					}
					return false;
				}
				from.removeGems(amount);
				return true;
			}
			from.sendMessage(CraftOfClansM.getString("messages.pay-not-resources"));
			return false;
		case "gold":
			if (from.hasGold(amount)) {
				if (target.addGold(amount) == false) {
					if (target.getLastResultFull()) {
						from.sendMessage(Color.message(CraftOfClansM.getString("messages.pay-not-success")));
					}
					return false;
				}
				from.removeGold(amount);
				return true;
			}
			from.sendMessage(CraftOfClansM.getString("messages.pay-not-resources"));
			return false;
		case "elixir":
			if (from.hasElixir(amount)) {
				if (target.addElixir(amount) == false) {
					if (target.getLastResultFull()) {
						from.sendMessage(Color.message(CraftOfClansM.getString("messages.pay-not-success")));
					}
					return false;
				}
				from.removeElixir(amount);
				return true;
			}
			from.sendMessage(CraftOfClansM.getString("messages.pay-not-resources"));
		case "dark_elixir":
			if (from.hasElixirNero(amount)) {
				if (target.addElixirNero(amount) == false) {
					if (target.getLastResultFull()) {
						from.sendMessage(Color.message(CraftOfClansM.getString("messages.pay-not-success")));
					}
					return false;
				}
				from.removeElixirNero(amount);
				return true;
			}
			from.sendMessage(CraftOfClansM.getString("messages.pay-not-resources"));
			break;
		default:
			return false;
		}

		return false;
	}
}
