package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import org.bukkit.plugin.Plugin;

import io.puharesource.mc.titlemanager.TitleManagerPlugin;

public class TitleManagerHook {

	public static boolean enable = false;

	public void setup() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("TitleManager");
		if (plugin == null) {
			LogHandler.log("TitleManager is not installed");
			return;
		}
		
		TitleManager.plugin = (TitleManagerPlugin) plugin;
		
		LogHandler.log("TitleManager is now enabled");
		enable = true;
	}
}
