package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import org.bukkit.plugin.Plugin;

public class SentinelHandler {
	public static boolean enable = false;
	
	public void setup() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("Sentinel");
		if (plugin == null) {
			LogHandler.log("Sentinel is not installed");
			return;
		}
		
		LogHandler.log("Sentinel is now enabled");
		enable = true;
	}
}
