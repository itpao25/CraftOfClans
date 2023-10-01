package it.itpao25.craftofclans.config;

import it.itpao25.craftofclans.CraftOfClans;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class CraftOfClansTier {

	private static File dir;
	private static File cfg;
	private static YamlConfiguration cfgYml;

	public CraftOfClansTier() {
		File file = new File(CraftOfClans.getInstance().getDataFolder() + "/data");
		cfg = new File(file + "/tiers.yml");
		if (!file.exists()) {
			file.mkdirs();
		}
		dir = file;
		if (!dir.exists())
			dir.mkdir();
		if (!cfg.exists())
			CraftOfClans.getInstance().saveResource("data/tiers.yml", true);
		cfgYml = YamlConfiguration.loadConfiguration(cfg);
	}

	public static String getString(String string) {
		if (cfgYml != null) {
			return cfgYml.getString(string);
		}
		return null;
	}

	public static Boolean getBoolean(String string) {
		if (cfgYml != null) {
			return cfgYml.getBoolean(string);
		}
		return null;
	}

	public static Integer getInt(String string) {
		if (cfgYml != null) {
			return cfgYml.getInt(string);
		}
		return null;
	}

	public static YamlConfiguration get() {
		if (cfgYml != null) {
			return cfgYml;
		}
		return null;
	}

	public static void reload() {
		cfgYml = YamlConfiguration.loadConfiguration(cfg);
	}

	public static boolean save() {
		try {
			cfgYml.save(cfg);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
