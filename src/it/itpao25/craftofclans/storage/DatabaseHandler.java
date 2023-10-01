package it.itpao25.craftofclans.storage;

import it.itpao25.craftofclans.CraftOfClans;

public class DatabaseHandler {
	static DatabaseType db;

	public static void setHandlerDB(DatabaseType type) {
		db = type;
		switch (type) {
		case MySQL:
			String passwd = CraftOfClans.config.getString("storage.password");
			String user = CraftOfClans.config.getString("storage.username");
			String host = CraftOfClans.config.getString("storage.hostname");
			String database = CraftOfClans.config.getString("storage.database");
			int port = CraftOfClans.config.getInt("storage.port");
			StorageMySQL StorageMySQL = new StorageMySQL(host, port, database, user, passwd);
			StorageMySQL.initialize();
			break;
		case SQLite:
			StorageFlat StorageSqlLite = new StorageFlat();
			StorageSqlLite.initialize();
			break;
		default:
			break;
		}
	}

	public static DatabaseType getType() {
		return db;
	}
}
