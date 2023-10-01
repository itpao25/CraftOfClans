package it.itpao25.craftofclans.storage;

import it.itpao25.craftofclans.handler.LogHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StorageMySQL {

	private String host;
	private int port;
	private String username;
	private String password;
	private String database;
	static Connection con = null;

	/**
	 * Usato per la creazione della connessione al databae mysql
	 * 
	 * @param host     Database host (server)
	 * @param database Database name
	 * @param username Username
	 * @param password Password
	 */
	public StorageMySQL(String host, int port, String database, String username, String password) {
		this.database = database;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public void initialize() {
		
		String uri = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true";
		
		java.util.Properties options = new java.util.Properties();
		options.put("user", this.username);
		options.put("password", this.password);

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(uri, options);
			LogHandler.log("Database connection success!");
			db();
		} catch (SQLException e) {
			LogHandler.error("Database connection failed!");
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

	public static void close() {
		if (con != null) {
			try {
				con.close();
				LogHandler.log("MySQL connection closed!");
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
			java.sql.Statement stmt = con.createStatement();

			// Tabella degli utenti
			String sqluser = "CREATE TABLE IF NOT EXISTS craftofclans_users" + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY," + "username varchar(255)," + "uuid varchar(255)," + "gems decimal(10,2)," + "gold decimal(10,2)," + "elixir decimal(10,2)," + "darkelixir decimal(10,2),"
					+ "trophies int(11)," + "attacks_win int(11)," + "attacks_lost int(11)," + "tiers TEXT," + "id_clan int(11) DEFAULT '0'," + "last_update_clan DATETIME, last_login DATETIME)";

			// Tabella dei villaggi
			String sqlvillage = "CREATE TABLE IF NOT EXISTS craftofclans_villages" + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY," + "id_player int(11)," + "pointX varchar(120)," + "pointZ varchar(120)," + "expanded int(11)," + "data_shield DATETIME," + "data_last_attack DATETIME, height int(11))";
			
			// Tabella dei clan
			String sqlclan = "CREATE TABLE IF NOT EXISTS craftofclans_clans" + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY," + "id_owner int(11)," + "name varchar(120)," + "`desc` varchar(120)," + "type varchar(120)," + "min_trophies int(11))";
			
			// Tabella degli attacchi
			String sqlattacks = "CREATE TABLE IF NOT EXISTS craftofclans_attacks" + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY," + "village varchar(30)," + "id_attacker int(11)," + "gems_won decimal(10,2)," + "gold_won decimal(10,2)," + "elixir_won decimal(10,2)," + "darkelixir_won decimal(10,2),"
					+ "percentage int(11), trophies_won int(11), trophies_lost int(11), data_attack DATETIME)";

			stmt.execute(sqluser);
			stmt.execute(sqlvillage);
			stmt.execute(sqlclan);
			stmt.execute(sqlattacks);
			
			String checkUpdate = "SELECT * FROM craftofclans_villages";
			
			// Update 0.6.2
			if (hasColumn(stmt.executeQuery(checkUpdate), "data_shield") == false) {
				String query = "ALTER TABLE `craftofclans_villages` ADD `data_shield` DATETIME";
				stmt.execute(query);
				LogHandler.log("Update database: version 0.6.2 (data_shield)");
			}
			
			// Update 0.6.3
			if (hasColumn(stmt.executeQuery(checkUpdate), "data_last_attack") == false) {
				String query = "ALTER TABLE `craftofclans_villages` ADD `data_last_attack` DATETIME";
				stmt.execute(query);
				LogHandler.log("Update database: version 0.6.3 (data_last_attack)");
			}
			
			// Update 0.8
			if (hasColumn(stmt.executeQuery(checkUpdate), "height") == false) {
				String query = "ALTER TABLE `craftofclans_villages` ADD `height` int(11)";
				stmt.execute(query);
				LogHandler.log("Update database: version 0.8 (height)");
			}
			
			// Update 0.6.6
			String query_066 = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'craftofclans_users' AND COLUMN_NAME = 'gems' limit 1 ";
			PreparedStatement stmt_066 = (PreparedStatement) con.prepareStatement(query_066);
			stmt_066.execute(query_066);
			ResultSet rs = stmt_066.executeQuery();
			while (rs.next()) {
				if(!rs.getString("DATA_TYPE").equals("decimal")) {
					
					String query = "ALTER TABLE craftofclans_users MODIFY COLUMN gems decimal(10,2);";
					stmt.execute(query);
					
					String query1 = "ALTER TABLE craftofclans_users MODIFY COLUMN gold decimal(10,2);";
					stmt.execute(query1);
					
					String query2 = "ALTER TABLE craftofclans_users MODIFY COLUMN elixir decimal(10,2);";
					stmt.execute(query2);
					
					String query3 = "ALTER TABLE craftofclans_users MODIFY COLUMN darkelixir decimal(10,2);";
					stmt.execute(query3);
					
					LogHandler.log("Update database: version 0.6.6 (resources now support decimals)");
				}
			}
			stmt_066.close();
			
			// Update 0.6.9
			String checkUpdate_user = "SELECT * FROM craftofclans_users";
			if (hasColumn(stmt.executeQuery(checkUpdate_user), "last_login") == false) {
				String query = "ALTER TABLE `craftofclans_users` ADD `last_login` DATETIME";
				stmt.execute(query);
				LogHandler.log("Update database: version 0.6.9 (craftofclans_users.last_login)");
			}
			
			stmt.close();
			
		} catch (SQLException e) {
			LogHandler.error("Control of installation of the MySQL database failed: " + e.getMessage());
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
