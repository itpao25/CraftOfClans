package it.itpao25.craftofclans.protocollib;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;

import org.bukkit.plugin.Plugin;

public class ProcolLibHook {
	public static boolean enable = false;

	public void setup() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("ProtocolLib");
		if (plugin == null) {
			LogHandler.log("ProtocolLib is not installed!");
			return;
		}
		LogHandler.log("ProtocolLib is now enabled");
		enable = true;
	}
}
