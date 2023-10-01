package it.itpao25.craftofclans.player;

import java.util.UUID;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;

public class PlayerGems {

	/**
	 * Prendo il nome da visualizzare
	 * 
	 * @return
	 */
	public static String getDisplayName() {
		String nome = CraftOfClansM.getString("resources.GEMS");
		return nome;
	}

	/**
	 * Ritorno con il numero delle gemme del giocatore
	 * 
	 * @param uuid
	 * @return
	 */
	public static double get(UUID uuid) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getGemsUser(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getGemsUser(uuid);
		}
		return 0;
	}

	/**
	 * Rimuovo le gemme al giocatore
	 * 
	 * @param uuid
	 * @param quantita
	 * @return
	 */
	public static boolean remove(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.removeGemsUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.removeGemsUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo le gemme al giocatore
	 * 
	 * @return
	 */
	public static boolean add(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addGemsUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addGemsUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Imposto le gemme al giocatore
	 * 
	 * @return
	 */
	public static boolean set(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.setGemsUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.setGemsUser(uuid, quantita);
		}
		return false;
	}
}
