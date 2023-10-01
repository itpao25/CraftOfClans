package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;
import it.itpao25.craftofclans.village.VillagerBuilderFree;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreate {
	public CommandCreate(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can create villages!"));
			return;
		}

		if (!PermissionUtil._has(sender, _Permission.PERM_CREATE) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}
		
		// Se è la modalità freemode
		if (!CraftOfClans.freemode) {
			sender.sendMessage(Color.translate("&cThis command is not enabled in the chosen server mode."));
			return;
		}

		// Controllo se il mondo clansworld esiste
		World world = Bukkit.getWorld("clansworld");
		if (world == null) {
			sender.sendMessage(Color.translate("&cYou must create a world clansworld before! (Admin)"));
			return;
		}

		Player p = (Player) sender;

		@SuppressWarnings("unused")
		VillagerBuilderFree VillagerBuilderFree = new VillagerBuilderFree(p);
	}
}
