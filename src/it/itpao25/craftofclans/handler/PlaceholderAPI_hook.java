package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPI_hook {

	public boolean enable = false;

	public void setup() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI");
		if (plugin == null) {
			LogHandler.log("PlaceholderAPI is not installed!");
			return;
		}
		LogHandler.log("PlaceholderAPI is now enabled");
		enable = true;
	}
}
