package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;

import org.bukkit.plugin.Plugin;

public class WorldGuardHandler {
	public boolean enable = false;

	public WorldGuardHandler() {
		setupWorldGuard();
	}

	private void setupWorldGuard() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null) {
			LogHandler.log("WorldGuard is not installed");
			return;
		}
		LogHandler.log("WorldGuard is now enabled");
		enable = true;
	}
}
