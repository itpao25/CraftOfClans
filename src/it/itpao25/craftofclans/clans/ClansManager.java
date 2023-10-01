package it.itpao25.craftofclans.clans;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import it.itpao25.craftofclans.api.ClanCreateEvent;
import it.itpao25.craftofclans.config.CraftOfClansClan;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;
import it.itpao25.craftofclans.util.Color;

public class ClansManager {

	/**
	 * CREO IL CLAN Funzione principale per la creazione di un nuovo clan
	 * 
	 * @param name
	 * @param type
	 * @param min_trophies
	 * @return
	 */
	public static boolean createClan(String name, String desc, ClansTypes type, int min_trophies, Player p) {

		if (name == null || name == "")
			return false;
		min_trophies = min_trophies > 0 ? min_trophies : 0;

		// Controllo se il giocatore ha già un clan
		if (hasPlayerClan(p.getUniqueId())) {
			p.sendMessage(Color.message(CraftOfClansM.getString("clan.message-already-has-clan")));
			return false;
		}

		// Controllo se il nome è già occupato
		if (existClan(name)) {
			p.sendMessage(Color.message(CraftOfClansM.getString("clan.message-clan-already-exists")));
			return false;
		}
		
		// Controllo la lunghezza del nome
		int min_length_name = CraftOfClansClan.getInt("clans-settings.min-length-name") != 0 ? CraftOfClansClan.getInt("clans-settings.min-length-name") : 0;
		int max_length_name = CraftOfClansClan.getInt("clans-settings.max-length-name") != 0 ? CraftOfClansClan.getInt("clans-settings.max-length-name") : 0;
		if (name.length() < min_length_name || name.length() > max_length_name) {
			p.sendMessage(Color.message("&cThe name must be between " + min_length_name + " and " + max_length_name + " characters"));
			return false;
		}

		// Creo il clan
		if (createDBClan(name, desc, type, min_trophies, p.getUniqueId())) {
			
			// Faccio il reset delle impostazioni
			ClansUIListeners.resetToPlayer(p);
			
			LogHandler.log("Creating clan " + name + "...");
			p.sendMessage(Color.message(CraftOfClansM.getString("clan.message-clan-created").replace("%1%", name)));

			ClanCreateEvent event = new ClanCreateEvent(p);
			Bukkit.getServer().getPluginManager().callEvent(event);

			return true;
		}
		return false;
	}

	/**
	 * Verifico se un clan esiste già con questo nome
	 * 
	 * @param name
	 * @return
	 */
	public static boolean existClan(String name) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.existClan(name);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.existClan(name);
		}
		return false;
	}

	public static boolean createDBClan(String name, String desc, ClansTypes type, int min_trophies, UUID id) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.createClan(name, desc, type, min_trophies, id);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.createClan(name, desc, type, min_trophies, id);
		}
		return false;
	}

	/**
	 * Controllo se il giocatore ha già un clan
	 * 
	 * @param id
	 * @return
	 */
	public static boolean hasPlayerClan(UUID id) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.hasPlayerClan(id);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.hasPlayerClan(id);
		}
		return false;
	}

	/**
	 * Numero massimo di membri che possono esserci per ogni clan
	 * 
	 * @return
	 */
	public static int getMaxMembers() {
		if (CraftOfClansClan.getString("clans-settings.max-members") != null) {
			return CraftOfClansClan.getInt("clans-settings.max-members");
		}
		return 25;
	}

	/**
	 * Lista del clan registrati nel server
	 * 
	 * @return
	 */
	public static ArrayList<ClanObject> lista_clan() {

		ArrayList<ClanObject> clans = new ArrayList<>();
		ArrayList<Integer> ids = new ArrayList<>();
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			ids = StorageMySQLRead.getClans();
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			ids = StorageFlatRead.getClans();
		}

		for (Integer id : ids) {
			clans.add(new ClanObject(id));
		}

		return clans;
	}
}
