package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansClan;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.config.CraftOfClansSkin;
import it.itpao25.craftofclans.config.CraftOfClansTier;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.command.CommandSender;

public class CommandReload {
	public CommandReload(CommandSender sender, String[] args) {
		if (!PermissionUtil._has(sender, _Permission.PERM_RELOAD) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}
		if (args.length == 1) {
			
			CraftOfClans.config.save();
			CraftOfClans.config.reload();
			
			CraftOfClansM.reload();
			CraftOfClansTier.reload();
			CraftOfClansClan.reload();
			CraftOfClansSkin.reload();
			
			CraftOfClans.troops.save();
			CraftOfClans.troops.reload();
			
			sender.sendMessage(Color.message(CraftOfClansM.getString("messages.reload-success")));
			return;
		}
		sender.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.reload-use")).replace("%1%", "/coc reload"));
	}
}
