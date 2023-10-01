package it.itpao25.craftofclans.config;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class CraftOfClansTroops {

	public static File dir = CraftOfClans.getInstance().getDataFolder();
	public static File cfg = new File(dir, "troops.yml");

	private static YamlConfiguration cfgYml;

	public CraftOfClansTroops() {
		if (!dir.exists())
			dir.mkdir();
		if (!cfg.exists())
			CraftOfClans.getInstance().saveDefaultConfig();

		checkFileYml(cfg);

		cfgYml = YamlConfiguration.loadConfiguration(cfg);
		try {
			check();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Se il file non è valido creo una copia creando il file config_bank.yml
	 * 
	 * @param file
	 * @return
	 */
	public boolean checkFileYml(File file) {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
			LogHandler.error("Cannot load " + file + ". " + ex);
			return false;
		} catch (IOException ex) {
			LogHandler.error("Cannot load " + file + ". " + ex);
			return false;
		} catch (InvalidConfigurationException ex) {

			// Copio il file e lo cambio config_bank.yml
			File cfg_bank = new File(dir, "troops_bank.yml");
			try {
				FileUtils.copyFile(file, cfg_bank);
			} catch (IOException e) {
				e.printStackTrace();
			}

			LogHandler.error("!! CREATED CONFIG TROOPS BANK FILE !!. Cannot load " + file + ". " + ex);
			return false;
		}
		return true;
	}

	public String getString(String string) {
		if (cfgYml != null) {
			return cfgYml.getString(string);
		}
		return null;
	}

	public Boolean getBoolean(String string) {
		if (cfgYml != null) {
			return cfgYml.getBoolean(string);
		}
		return null;
	}

	public Integer getInt(String string) {
		if (cfgYml != null) {
			return cfgYml.getInt(string);
		}
		return null;
	}

	public double getDouble(String string) {
		if (cfgYml != null) {
			return cfgYml.getDouble(string);
		}
		return 0;
	}

	public YamlConfiguration get() {
		if (cfgYml != null) {
			return cfgYml;
		}
		return null;
	}

	public void reload() {
		if(!checkFileYml(cfg)) {
			return;
		}
		cfgYml = YamlConfiguration.loadConfiguration(cfg);
	}

	private void check() throws IOException {

		Reader defConfigStream = new InputStreamReader(CraftOfClans.getInstance().getResource("troops.yml"), "UTF8");
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		for (String key : defConfig.getConfigurationSection("").getKeys(false)) {

			if (key != null && defConfig.getConfigurationSection(key) != null) {
				for (String key2 : defConfig.getConfigurationSection(key).getKeys(false)) {

					if (key2 != null && defConfig.getConfigurationSection(key + "." + key2) != null) {
						for (String key3 : defConfig.getConfigurationSection(key + "." + key2).getKeys(false)) {

							if (key3 != null && defConfig.getConfigurationSection(key + "." + key2) != null) {
								if (!cfgYml.contains(key + "." + key2 + "." + key3)) {

									Object valore = defConfig.get(key + "." + key2 + "." + key3);
									if (key3.equalsIgnoreCase("cost_elixir") || key3.equalsIgnoreCase("cost_gold") || key3.equalsIgnoreCase("cost_dark_elixir") || key3.equalsIgnoreCase("cost_gems")) {
										valore = 0;
									}

									cfgYml.set(key + "." + key2 + "." + key3, valore);
									cfgYml.save(cfg);
									save();
								}
							}
						}
					}

					if (!cfgYml.contains(key + "." + key2)) {

						Object valore = defConfig.get(key + "." + key2);
						if (key2.equalsIgnoreCase("cost_elixir") || key2.equalsIgnoreCase("cost_gold") || key2.equalsIgnoreCase("cost_dark_elixir") || key2.equalsIgnoreCase("cost_gems")) {
							valore = 0;
						}

						cfgYml.set(key + "." + key2, valore);
						cfgYml.save(cfg);
						save();
					}
				}
			}

			if (!cfgYml.contains(key)) {
				cfgYml.set(key, defConfig.get(key));
				cfgYml.save(cfg);
				save();
			}
		}
	}

	public void save() {
		if(!checkFileYml(cfg)) {
			return;
		}
		try {
			ArrayList<String> ignorati = new ArrayList<>();
			ConfigUpdater.update(CraftOfClans.getInstance(), "troops.yml", cfg, ignorati);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
