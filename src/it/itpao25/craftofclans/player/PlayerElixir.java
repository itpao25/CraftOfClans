package it.itpao25.craftofclans.player;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;

import java.util.UUID;

public class PlayerElixir {

	/**
	 * Prendo il nome da visualizzare
	 * 
	 * @return
	 */
	public static String getDisplayName() {
		String nome = CraftOfClansM.getString("resources.ELIXIR");
		return nome;
	}

	/**
	 * Ritorno con il numero dell'elixir del giocatore
	 * 
	 * @param uuid
	 * @return
	 */
	public static double get(UUID uuid) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getElixirUser(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getElixirUser(uuid);
		}
		return -1;
	}

	/**
	 * Rimuovo l'elixir dal giocatore
	 * 
	 * @param uuid
	 * @param quantita
	 * @return
	 */
	public static boolean remove(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.removeElixirUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.removeElixirUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo l'elixir dal giocatore
	 * 
	 * @return
	 */
	public static boolean add(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addElixirUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addElixirUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo l'elixir dal giocatore
	 * 
	 * @return
	 */
	public static boolean set(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.setElixirUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.setElixirUser(uuid, quantita);
		}
		return false;
	}
}
