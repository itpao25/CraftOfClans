package it.itpao25.craftofclans.config;

import it.itpao25.craftofclans.CraftOfClans;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.YamlConfiguration;

public class CraftOfClansSkin {

	private static File dir = CraftOfClans.getInstance().getDataFolder();
	private static File cfg = new File(dir, "skins.yml");
	private static YamlConfiguration cfgYml;

	public CraftOfClansSkin() {
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!cfg.exists()) {
			CraftOfClans.getInstance().saveResource("skins.yml", true);
		}
		
		cfgYml = YamlConfiguration.loadConfiguration(cfg);
		
		try {
			check();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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

	private void check() throws UnsupportedEncodingException {

		Reader defConfigStream = new InputStreamReader(CraftOfClans.getInstance().getResource("skins.yml"), "UTF8");
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		for (String key : defConfig.getConfigurationSection("").getKeys(false)) {

			if (key != null && defConfig.getConfigurationSection(key) != null) {
				for (String key2 : defConfig.getConfigurationSection(key).getKeys(false)) {

					if (key2 != null && defConfig.getConfigurationSection(key + "." + key2) != null) {
						for (String key3 : defConfig.getConfigurationSection(key + "." + key2).getKeys(false)) {

							if (key3 != null && defConfig.getConfigurationSection(key + "." + key2) != null) {
								if (!cfgYml.contains(key + "." + key2 + "." + key3)) {
									cfgYml.set(key + "." + key2 + "." + key3, defConfig.get(key + "." + key2 + "." + key3));
									try {
										cfgYml.options().copyDefaults(true);
										cfgYml.save(cfg);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}

						}
					}

					if (!cfgYml.contains(key + "." + key2)) {
						cfgYml.set(key + "." + key2, defConfig.get(key + "." + key2));
						try {
							cfgYml.options().copyDefaults(true);
							cfgYml.save(cfg);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			if (!cfgYml.contains(key)) {
				cfgYml.set(key, defConfig.get(key));
				try {
					cfgYml.options().copyDefaults(true);
					cfgYml.save(cfg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void reload() {
		cfgYml = YamlConfiguration.loadConfiguration(cfg);
	}

	public static void save() {
		try {
			cfgYml.save(cfg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
