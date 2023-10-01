package it.itpao25.craftofclans.player;

import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;

import java.util.UUID;

public class PlayerTrophies {

	/**
	 * Ritorno con il numero dei trofei del giocatore
	 * 
	 * @param uuid
	 * @return
	 */
	public static int get(UUID uuid) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getTrofeiUser(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getTrofeiUser(uuid);
		}
		return 0;
	}

	/**
	 * Rimuovo trofei al giocatore
	 * 
	 * @param uuid
	 * @param quantita
	 * @return
	 */
	public static boolean remove(UUID uuid, int quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.removeTrofeiUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.removeTrofeiUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggungo trofei al giocatore
	 * 
	 * @return
	 */
	public static boolean add(UUID uuid, int quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addTrofeiUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addTrofeiUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * setto trofei al giocatore
	 * 
	 * @return
	 */
	public static boolean set(UUID uuid, int quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.setTrofeiUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.setTrofeiUser(uuid, quantita);
		}
		return false;
	}
}
