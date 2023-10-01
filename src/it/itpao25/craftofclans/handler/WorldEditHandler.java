package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;

import org.bukkit.plugin.Plugin;

public class WorldEditHandler {
	public boolean enable = false;

	public WorldEditHandler() {
		setupWorldEdit();
	}

	private void setupWorldEdit() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null) {
			LogHandler.log("WorldEdit is not installed");
			return;
		}
		LogHandler.log("WorldEdit is now enabled");
		enable = true;
	}
}
