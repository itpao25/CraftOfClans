package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.village.VillageDestroy;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillagesHandler;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandConfirm {
	public CommandConfirm(CommandSender sender, String[] args) {
		
		// Reset di un villaggio
		if (PermissionUtil._has(sender, _Permission.PERM_DESTROY) || sender.isOp()) {
			if (VillagesHandler.villages_to_destroy.containsKey(sender)) {
				
				for (Map.Entry<Long, VillageId> entry : VillagesHandler.villages_to_destroy.get(sender).entrySet()) {
					Long timestamp = entry.getKey();
					long coolDownRimasto = (System.currentTimeMillis() - timestamp);
					
					VillagesHandler.villages_to_destroy.remove(sender);
					if (coolDownRimasto <= (1000 * 60)) {
						
						// Eseguo l'eliminazione
						CraftOfClans.getInstance().getServer().getScheduler().runTaskAsynchronously(CraftOfClans.getInstance(), new Runnable() {
							@Override
							public void run() {
								Bukkit.getScheduler().runTask(CraftOfClans.getInstance(), new VillageDestroy(entry.getValue(), sender));
							}
						});
						return;
					}
				}

				VillagesHandler.villages_to_destroy.remove(sender);
				sender.sendMessage(Color.message("&4Nothing to do"));
				return;
			}
		}

		sender.sendMessage(Color.message("&4Nothing to do"));
	}
}
