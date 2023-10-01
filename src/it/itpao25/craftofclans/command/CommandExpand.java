package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerListener;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExpand {
	public CommandExpand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}
		if (!PermissionUtil._has(sender, _Permission.PERM_EXPAND) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}
		Player p = (Player) sender;
		if (args.length == 1) {
			PlayerListener.haveExpander.put(p.getName(), p.getLocation().getChunk());
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.expander-actived")).replace("%1%", "/coc expand -c"));
			return;
		} else if (args.length == 2) {
			if (args[1].equals("-c")) {
				if (PlayerListener.haveExpander.containsKey(p.getName())) {
					PlayerListener.haveExpander.remove(p.getName());
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.expander-disactived")));
					return;
				} else {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.expander-not-actived")).replace("%1%", "/coc expand"));
					return;
				}
			}
		}
		p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.expander-use")).replace("%1%", "/coc expand [-c]"));
	}
}
