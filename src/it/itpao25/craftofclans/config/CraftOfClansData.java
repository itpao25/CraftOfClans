package it.itpao25.craftofclans.config;

import it.itpao25.craftofclans.CraftOfClans;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class CraftOfClansData {
	private static File dir;
	private static File cfg;
	private static YamlConfiguration cfgYml;

	public CraftOfClansData() {
		File file = new File(CraftOfClans.getInstance().getDataFolder() + "/data");
		cfg = new File(file + "/villages.yml");
		if (!file.exists()) {
			file.mkdirs();
		}
		dir = file;
		if (!dir.exists())
			dir.mkdir();
		if (!cfg.exists())
			CraftOfClans.getInstance().saveResource("data/villages.yml", true);
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
	
	public static double getDouble(String string) {
		if (cfgYml != null) {
			return cfgYml.getDouble(string);
		}
		return 0;
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
