package it.itpao25.craftofclans.player;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;

import java.util.UUID;

public class PlayerGold {

	/**
	 * Prendo il nome da visualizzare
	 * 
	 * @return
	 */
	public static String getDisplayName() {
		String nome = CraftOfClansM.getString("resources.GOLD");
		return nome;
	}

	/**
	 * Ritorno con il numero dell'oro del giocatore
	 * 
	 * @param uuid
	 * @return
	 */
	public static double get(UUID uuid) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getGoldUser(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getGoldUser(uuid);
		}
		return 0;
	}

	/**
	 * Rimuovo l'oro dal giocatore
	 * 
	 * @param uuid
	 * @param quantita
	 * @return
	 */
	public static boolean remove(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.removeGoldUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.removeGoldUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo l'oro dal giocatore
	 * 
	 * @return
	 */
	public static boolean add(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addGoldUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addGoldUser(uuid, quantita);
		}
		return false;
	}

	/**
	 * Aggiungo l'oro dal giocatore
	 * 
	 * @return
	 */
	public static boolean set(UUID uuid, double quantita) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.setGoldUser(uuid, quantita);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.setGoldUser(uuid, quantita);
		}
		return false;
	}
}
