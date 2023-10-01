package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import org.bukkit.plugin.Plugin;

public class MVdWPlaceholderAPI_hook {

	public boolean enable = false;

	public void setup() {

		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
		if (plugin == null) {
			LogHandler.log("MVdWPlaceholderAPI is not installed!");
			return;
		}
		LogHandler.log("MVdWPlaceholderAPI is now enabled");
		enable = true;
	}

	public void registerPlacers() {

		MvdWPlaceholderAPI_register.registerPlacers();
	}
}
