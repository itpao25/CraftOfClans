package it.itpao25.craftofclans.holo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.util.Color;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

public class HolographicHook {
	public boolean enable = false;

	public void setup() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("HolographicDisplays");
		if (plugin == null) {
			LogHandler.log("HolographicDisplays is not installed!");
			return;
		}
		LogHandler.log("HolographicDisplays is now enabled");
		enable = true;
		
		runDeathHoloTime();
	}

	/**
	 * Elimino i vecchi hologrammi (Tombe dei giocatori morti in attacco)
	 */
	public void runDeathHoloTime() {

		int c_found = 0;
		String to_replace = null;
		List<String> list_lore = CraftOfClans.config.get().getStringList("holographic-display.attack-chest-death-player-lore");
		for (int c = 0; c <= list_lore.size() - 1; c++) {
			String current = list_lore.get(c);
			if (current.contains("%2%")) {
				c_found = c;
				to_replace = current;
			}
		}

		final int c_found_final = c_found;
		final String to_replace_final = to_replace;

		new BukkitRunnable() {
			@Override
			public void run() {
				if (HolographicUtil.holo_death_attack.size() > 0) {

					int time = CraftOfClans.config.getInt("holographic-display.attack-chest-death-player-time");
					long tempo_di_vita = time * 60 * 1000;

					for (Hologram hologram : HolographicUtil.holo_death_attack) {
						if (hologram.isDeleted()) {
							HolographicUtil.holo_death_attack.remove(hologram);
							continue;
						}
						long elapsedMillis = System.currentTimeMillis() - hologram.getCreationTimestamp();
						long rimasto = tempo_di_vita - elapsedMillis;
						
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
						String temporim = sdf.format(new Date(rimasto));

						String str_lc = to_replace_final;
						str_lc = to_replace_final.replace("%2%", temporim);
						str_lc = Color.translate(str_lc);

						hologram.removeLine(c_found_final);
						hologram.insertTextLine(c_found_final, Color.translate(str_lc));

						if (elapsedMillis > tempo_di_vita) {
							hologram.delete();
						}
					}
				}
			}
		}.runTaskTimer(CraftOfClans.getInstance(), 1 * 20L, 1 * 20L);
	}
}
