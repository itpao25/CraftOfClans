package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.village.SpectatorMode;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave {

	public CommandLeave(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can create villages!"));
			return;
		}

		// Se è la modalità freemode
		if (!CraftOfClans.freemode) {
			sender.sendMessage(Color.translate("&cThis command is not enabled in the chosen server mode."));
			return;
		}

		Player p = (Player) sender;
		if (!SpectatorMode.inSpectator(p)) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-not-now")));
			return;
		}

		SpectatorMode.remove(p, false);

		Location back_location = SpectatorMode.player_original_position.get(p);

		Location post_plus = back_location.clone();
		post_plus.add(0, 0, +1);
		if (!post_plus.getBlock().getType().equals(Material.AIR)) {
			back_location.add(0, 0, -1);
		}
		
		Location post_min = back_location.clone();
		post_min.add(0, 0, -1);
		if (!post_min.getBlock().getType().equals(Material.AIR)) {
			back_location.add(0, 0, +1);
		}
		
		p.teleport(back_location);
		SpectatorMode.remove(p, true);

		sender.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-leaved")));
	}
}
