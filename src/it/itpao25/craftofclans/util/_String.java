package it.itpao25.craftofclans.util;

import it.itpao25.craftofclans.config.CraftOfClansM;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class _String {

	public static void nopermission(CommandSender p) {
		String str = CraftOfClansM.getString("messages.nopermission");
		String finale = str != null ? str : "&cNo permission";
		p.sendMessage(Color.message(finale));
	}

	public static void nopermission(Player p) {
		String str = CraftOfClansM.getString("messages.nopermission");
		String finale = str != null ? str : "&cNo permission";
		p.sendMessage(Color.message(finale));
	}

	public static boolean commandMain(CommandSender p) {
		p.sendMessage(Color.translate("&6&l     CraftOfClans commands:"));
		p.sendMessage(Color.translate("&e/coc start - Create your personal village"));
		p.sendMessage(Color.translate("&e/coc shop - Buy the structures"));
		p.sendMessage(Color.translate("&e/coc sell - Sell your items"));
		p.sendMessage(Color.translate("&e/coc expand - Expand your village"));
		p.sendMessage(Color.translate("&e/coc expand-list - List of attacks made by you"));
		p.sendMessage(Color.translate(""));
		p.sendMessage(Color.translate("&e/coc give <player> < gems | gold | elixir | dark_elixir > <amount> - Give resources"));
		p.sendMessage(Color.translate("&e/coc pay <player> < gems | gold | elixir | dark_elixir > <amount> - Pay resources"));
		p.sendMessage(Color.translate("&e/coc set-resources <player> < gems | gold | elixir | dark_elixir > <amount> - Set resources"));
		p.sendMessage(Color.translate("&e/coc remove-resources <player> < gems | gold | elixir | dark_elixir > <amount> - Remove resources"));
		p.sendMessage(Color.translate("&e/coc info - View your personal stats"));
		p.sendMessage(Color.translate("&e/coc tp - Reach your village"));
		p.sendMessage(Color.translate("&e/coc tp <player name> - Visit a village"));
		p.sendMessage(Color.translate("&e/coc attack - Run the attack"));
		p.sendMessage(Color.translate("&e/coc show <player name> - View user info"));
		p.sendMessage(Color.translate("&e/clan - Clan management"));
		p.sendMessage(Color.translate("&e/tier - Tiers management"));
		return true;
	}
}
