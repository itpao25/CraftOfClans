package it.itpao25.craftofclans.structures_resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import java.util.UUID;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;
import it.itpao25.craftofclans.village.VillageId;

public class InactivePlayerTask implements Runnable {

	@Override
	public void run() {
		if (CraftOfClans.config.getBoolean("task-search-inactive-player.enable")) {

			// Prendo tutti i player del server
			HashMap<String, HashMap<String, String>> hashmap = new HashMap<>();
			if (DatabaseHandler.getType() == DatabaseType.MySQL) {
				hashmap.putAll(StorageMySQLRead.getPlayers());
			} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
				hashmap.putAll(StorageFlatRead.getPlayers());
			}

			// Numero di giorni
			int days_max = CraftOfClans.config.getInt("task-search-inactive-player.player-days-inactive");

			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date_now = new Date();

			for (Entry<String, HashMap<String, String>> entry : hashmap.entrySet()) {

				HashMap<String, String> user_info = entry.getValue();
				String data_last_login = user_info.get("last_login");
				if (data_last_login == "" || data_last_login == null) {
					continue;
				}

				try {
					Date date1 = myFormat.parse(data_last_login);
					long diff = Math.abs(date_now.getTime() - date1.getTime());
					float days = diff / (24 * 60 * 60 * 1000);
					
					if (days > days_max) {
						PlayerStored pstored = new PlayerStored(UUID.fromString(user_info.get("uuid")));
						if (pstored.hasVillage() && !pstored.isOnline()) {
							LogHandler.log("Player " + pstored.getName() + " hasn't logged in for " + days_max + " days (last login " + data_last_login + "). Delete his village...");
							
							VillageId villo = pstored.getVillage();
							villo.destroy(Bukkit.getConsoleSender());
							LogHandler.log("" + pstored.getName() + "'s village deleted!");
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else {
			CraftOfClans.inactive_task.cancel();
		}
	}
}
