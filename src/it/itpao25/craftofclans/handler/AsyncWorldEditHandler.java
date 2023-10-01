package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;

import org.bukkit.plugin.Plugin;

public class AsyncWorldEditHandler {
	public boolean enable = false;
	
	public AsyncWorldEditHandler() {
		setupAsyncWorldEdit();
	}

	private void setupAsyncWorldEdit() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("AsyncWorldEdit");
		if (plugin == null) {
			LogHandler.log("AsyncWorldEdit is not installed");
			return;
		}
		LogHandler.log("AsyncWorldEdit is now enabled");
		enable = true;
	}
}
