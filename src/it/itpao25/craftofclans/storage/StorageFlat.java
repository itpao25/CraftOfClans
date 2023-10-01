package it.itpao25.craftofclans.storage;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StorageFlat {
	static Connection con = null;

	public StorageFlat() {

	}

	public void initialize() {
		File file = new File(CraftOfClans.getInstance().getDataFolder() + "/database.db");
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String uri = "jdbc:sqlite:" + file;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(uri);
			LogHandler.log("Database flat file connection success!");
			db();
		} catch (SQLException e) {
			LogHandler.error("Database flat file connection failed!");
		}
	}

	/**
	 * Ritorno con la connessione, se è attiva
	 * 
	 * @return
	 */
	public Connection getConnection() {
		if (con != null) {
			return con;
		}
		return null;
	}

	/**
	 * Chiudo la connessione al database
	 * 
	 */
	public static void close() {
		if (con != null) {
			try {
				LogHandler.log("SQLITE connection closed!");
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Funzioni primarie, per l'installamento del database
	 */
	private void db() {
		try {

			// Tabella degli utenti
			String sqluser = "CREATE TABLE IF NOT EXISTS craftofclans_users" + "(id INTEGER PRIMARY KEY AUTOINCREMENT," + "username varchar(255)," + "uuid varchar(255)," + "gems decimal(10,2)," + "gold decimal(10,2)," + "elixir decimal(10,2)," + "darkelixir decimal(10,2),"
					+ "trophies int(11)," + "attacks_win int(11)," + "attacks_lost int(11)," + "tiers TEXT," + "id_clan int(11) DEFAULT '0'," + "last_update_clan TEXT, data_shield TEXT)";
			
			// Tabella dei villaggi
			String sqlvillage = "CREATE TABLE IF NOT EXISTS craftofclans_villages" + "(id INTEGER PRIMARY KEY AUTOINCREMENT," + "id_player int(11)," + "pointX varchar(120)," + "pointZ varchar(120)," + "expanded int(11)," + "data_shield TEXT," + "data_last_attack TEXT, height int(11))";

			// Tabella dei clan
			String sqlclans = "CREATE TABLE IF NOT EXISTS craftofclans_clans" + "(id INTEGER PRIMARY KEY AUTOINCREMENT," + "id_owner int(11)," + "name varchar(120)," + "desc varchar(250)," + "type varchar(120)," + "min_trophies int(11))";
			
			// Tabella attacchi
			String sqlattacks = "CREATE TABLE IF NOT EXISTS craftofclans_attacks" + "(id INTEGER PRIMARY KEY AUTOINCREMENT," + "village varchar(30)," + "id_attacker int(11)," + "gems_won decimal(10,2)," + "gold_won decimal(10,2)," + "elixir_won decimal(10,2)," + "darkelixir_won decimal(10,2),"
					+ "percentage int(11), trophies_won int(11), trophies_lost int(11), data_attack TEXT)";
			
			java.sql.Statement stmt = con.createStatement();
			stmt.execute(sqluser);
			stmt.execute(sqlvillage);
			stmt.execute(sqlclans);
			stmt.execute(sqlattacks);
			stmt.close();

			String checkUpdate = "SELECT * FROM craftofclans_villages";

			// Update 0.6.2
			java.sql.Statement stmt1 = con.createStatement();
			if (hasColumn(stmt1.executeQuery(checkUpdate), "data_shield") == false) {
				String query = "ALTER TABLE `craftofclans_villages` ADD COLUMN `data_shield` TEXT";
				stmt1.execute(query);
				LogHandler.log("Update database: version 0.6.2 (data_shield)");
			}
			stmt1.close();
			
			// Update 0.8 - height del villaggio
			java.sql.Statement stmt08 = con.createStatement();
			if (hasColumn(stmt08.executeQuery(checkUpdate), "height") == false) {
				String query = "ALTER TABLE `craftofclans_villages` ADD COLUMN `height` int(11)";
				stmt08.execute(query);
				LogHandler.log("Update database: version 0.8 (height)");
			}
			stmt08.close();
			
			// Update 0.6.3
			java.sql.Statement stmt2 = con.createStatement();
			if (hasColumn(stmt2.executeQuery(checkUpdate), "data_last_attack") == false) {
				String query = "ALTER TABLE `craftofclans_villages` ADD COLUMN `data_last_attack` TEXT";
				stmt2.execute(query);
				LogHandler.log("Update database: version 0.6.3 (data_last_attack)");
			}
			stmt2.close();
			
			// Update 0.6.9
			String checkUpdate_users = "SELECT * FROM craftofclans_users";
			java.sql.Statement stmt_69 = con.createStatement();
			if (hasColumn(stmt_69.executeQuery(checkUpdate_users), "last_login") == false) {
				String query = "ALTER TABLE `craftofclans_users` ADD COLUMN `last_login` TEXT";
				stmt_69.execute(query);
				LogHandler.log("Update database: version 0.6.9 (last_login)");
			}
			stmt_69.close();
			
			// Update 0.6.6
			String check_colum = "PRAGMA table_info(craftofclans_users);";
			PreparedStatement stmt3 = (PreparedStatement) con.prepareStatement(check_colum);
			ResultSet rs = stmt3.executeQuery();
			while (rs.next()) {
				if(rs.getString("name").equals("gems")) {
					if(!rs.getString("type").equals("decimal(10,2)")) {
						
						// Rinomino la tabella attuale
						String replace_tmp = "ALTER TABLE craftofclans_users RENAME TO craftofclans_users_tmp;";
						java.sql.Statement stmt3_fix = con.createStatement();
						stmt3_fix.execute(replace_tmp);
						stmt3_fix.close();
						
						// Devo creare la nuova tabella chiamata craftofclans_users
						java.sql.Statement stmt3_fix2 = con.createStatement();
						stmt3_fix2.execute(sqluser);
						stmt3_fix2.close();
						
						// Trasferisco i dati nella nuova tabella
						String move_tmp_data = "INSERT INTO craftofclans_users(id, username, uuid, gems, gold, elixir, darkelixir, trophies, attacks_win, attacks_lost, tiers, id_clan, last_update_clan) "
								+ "SELECT id, username, uuid, gems, gold, elixir, darkelixir, trophies, attacks_win, attacks_lost, tiers, id_clan, last_update_clan FROM craftofclans_users_tmp;";
						java.sql.Statement stmt3_fix3 = con.createStatement();
						stmt3_fix3.execute(move_tmp_data);
						stmt3_fix3.close();
						
						// Elimino la vecchia tabella
						String data_del = "DROP TABLE craftofclans_users_tmp;";
						java.sql.Statement stmt3_fix4 = con.createStatement();
						stmt3_fix4.execute(data_del);
						stmt3_fix4.close();
						
						LogHandler.log("Update database: version 0.6.6 (resources now support decimals)");
					}
				}
			}
			stmt3.close();
			
			// Eseguo la query per disabilitare il Journal file (@since 0.5)
			java.sql.Statement stmt05_fix1 = con.createStatement();
			String stmt05_fixquery = "pragma journal_mode=memory;";
			stmt05_fix1.executeQuery(stmt05_fixquery);
			stmt05_fix1.close();

		} catch (SQLException e) {
			LogHandler.error("Control or installation of the Flat database failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Controllo se una colonna esiste nel database
	 * 
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}
}
