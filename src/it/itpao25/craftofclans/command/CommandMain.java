package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._String;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMain implements CommandExecutor {
	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Primo comando, che permette di creare l'isola del giocatore
		if ((args.length > 0 && args[0].equalsIgnoreCase("start"))) {
			CommandStart CommandStart = new CommandStart(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("info"))) {
			CommandInfo CommandInfo = new CommandInfo(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("tp"))) {
			CommandTp CommandTp = new CommandTp(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("expand"))) {
			CommandExpand CommandExpand = new CommandExpand(sender, args);
			
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("sell"))) {
			CommandSell CommandSell = new CommandSell(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("shop"))) {
			CommandShop CommandShop = new CommandShop(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("attack"))) {
			CommandAttack CommandAttack = new CommandAttack(sender, args);
			
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("attack-list"))) {
			CommandAttackList CommandAttackList = new CommandAttackList(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("reload"))) {
			CommandReload CommandReload = new CommandReload(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("clan"))) {
			sender.sendMessage(Color.message("&ePlease use /clan <params>"));

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("show"))) {
			CommandShow CommandShow = new CommandShow(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("set-resources"))) {
			CommandSetResource CommandSetResource = new CommandSetResource(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("remove-resources"))) {
			CommandRemoveResource CommandRemoveResource = new CommandRemoveResource(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("give"))) {
			CommandGive CommandGive = new CommandGive(sender, args);

		} else if ((args.length > 0 && args[0].equalsIgnoreCase("pay"))) {
			CommandPay CommandPay = new CommandPay(sender, args);
			
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("confirm"))) {
			CommandConfirm CommandConfirm = new CommandConfirm(sender, args);
			
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("destroy"))) {
			CommandDestroy CommandDestroy = new CommandDestroy(sender, args);
			
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("create"))) {
			CommandCreate CommandCreate = new CommandCreate(sender, args);
		
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("leave"))) {
			CommandLeave CommandLeave = new CommandLeave(sender, args);
			
		} else if ((args.length > 0 && args[0].equalsIgnoreCase("test"))) {
			CommandTest CommandTest = new CommandTest(sender, args);

		} else {
			_String.commandMain(sender);
		}
		return false;
	}
}
