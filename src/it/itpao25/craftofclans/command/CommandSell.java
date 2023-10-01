package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSell {
	public CommandSell(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}
		if (!PermissionUtil._has(sender, _Permission.PERM_SELL) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}
		Player p = (Player) sender;
		if (args.length == 1) {
			InventoryHandler inv = new InventoryHandler(p, InventoryEnum.Sell);
			inv.openInventory();
			return;
		}
		p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.sell-use")).replace("%1%", "/coc sell"));
	}
}
