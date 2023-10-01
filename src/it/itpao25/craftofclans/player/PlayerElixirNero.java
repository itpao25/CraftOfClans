package it.itpao25.craftofclans.player;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;

import java.util.UUID;

public class PlayerElixirNero {

	/**
	 * Prendo il nome da visualizzare
	 * 
	 * @return
	 */
	public static String getDisplayName() {
		String nome = CraftOfClansM.getString("resources.DARK_ELIXIR");
		return nome;
	}

	/**
	 * Ritorno con il numero dell'elixir nero del giocatore
	 * 
	 * @param uuid
	 * @return
	 */
	public static double get(UUID uuid) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getElixirNeroUser(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getElixirNeroUser(uuid);
		}
		return -1;
	}

	/**
	 * Rimuovo l'elixir nero dal giocatore
	 * 
	 * @param uuid
	 * @param quantita
	 * @return
	 */
	public static boolean remove(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.removeElixirNeroUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.removeElixirNeroUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo l'elixir nero dal giocatore
	 * 
	 * @return
	 */
	public static boolean add(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addElixirNeroUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addElixirNeroUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo l'elixir nero dal giocatore
	 * 
	 * @return
	 */
	public static boolean set(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.setElixirNeroUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.setElixirNeroUser(uuid, quantita);
		}
		return false;
	}
}
