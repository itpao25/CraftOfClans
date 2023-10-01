package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;

import org.bukkit.plugin.Plugin;

public class EffectLib_hook {
	public boolean enable = false;

	public EffectLib_hook() {
		setupWorldEdit();
	}

	private void setupWorldEdit() {
		Plugin plugin = CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("EffectLib");
		if (plugin == null) {
			LogHandler.log("EffectLib is not installed");
			return;
		}
		LogHandler.log("EffectLib is now enabled");
		enable = true;
	}
}
