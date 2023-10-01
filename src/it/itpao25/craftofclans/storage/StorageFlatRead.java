package it.itpao25.craftofclans.storage;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.clans.ClansTypes;
import it.itpao25.craftofclans.village.VillageId;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class StorageFlatRead {

	/**
	 * Lista dei player
	 */
	public static HashMap<String, HashMap<String, String>> getPlayers() {
		HashMap<String, HashMap<String, String>> users = new HashMap<>();
		String query = "SELECT * FROM `craftofclans_users`";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				HashMap<String, String> user = new HashMap<>();
				user.put("id", rs.getString("id"));
				user.put("username", rs.getString("username"));
				user.put("uuid", rs.getString("uuid"));
				user.put("gems", rs.getString("gems"));
				user.put("gold", rs.getString("gold"));
				user.put("elixir", rs.getString("elixir"));
				user.put("darkelixir", rs.getString("darkelixir"));
				user.put("trophies", rs.getString("trophies"));
				user.put("last_login", rs.getString("last_login"));
				users.put(rs.getString("uuid"), user);
			}
			s.close();
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Controllo se l'utente è presente nel database Flat
	 */
	public static boolean isUserExists(UUID id) {
		String query = "SELECT * FROM `craftofclans_users` WHERE uuid=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			s.setString(1, id.toString());
			ResultSet rs = s.executeQuery();

			int index = 0;
			while (rs.next()) {
				index++;
			}
			rs.close();
			s.close();
			return (index > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Registro l'utente nel database Flat
	 */
	public static boolean userRegister(Player p) {
		double gems = CraftOfClans.config.getString("newbie-player.gems") != null ? CraftOfClans.config.getDouble("newbie-player.gems") : 0;
		double elixir = CraftOfClans.config.getString("newbie-player.elixir") != null ? CraftOfClans.config.getDouble("newbie-player.elixir") : 0;
		double gold = CraftOfClans.config.getString("newbie-player.gold") != null ? CraftOfClans.config.getDouble("newbie-player.gold") : 0;
		int trophies = CraftOfClans.config.getString("newbie-player.trophies") != null ? CraftOfClans.config.getInt("newbie-player.trophies") : 0;

		// Controllo se l'utente è già registrato
		if (isUserExists(p.getUniqueId()))
			return false;
		UUID id = p.getUniqueId();
		String query = "INSERT INTO `craftofclans_users` (`id`, `username`, `uuid`, `gems`, `gold`, `elixir`, `darkelixir`, `trophies`, `attacks_win`, `attacks_lost`) VALUES (NULL, ?, ?, '" + gems + "','" + gold + "','" + elixir + "','0', '" + trophies
				+ "', '0', '0');";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, p.getName());
			s.setString(2, id.toString());
			if (s.execute()) {
				return true;
			}
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con l'id del giocatore nel database, avendo l'uuid
	 */
	public static int getuserId(UUID uuid) {
		if (StorageFlat.con == null)
			return 0;
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			int num = 0;
			while (rs.next()) {
				num = rs.getInt("id");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Ritorno con il nickname del giocatore tramite l'id salvato nel database
	 */
	public static String getPlayerName(int id) {

		String query = "SELECT * FROM `craftofclans_users` WHERE `id`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();
			String nick = null;
			while (rs.next()) {
				nick = rs.getString("username");
			}
			s.close();
			return nick;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ritorno con l'id del giocatore nel database, avendo l'uuid
	 */
	public static boolean hasUserVillo(int id) {

		String query = "SELECT * FROM `craftofclans_villages` WHERE `id_player`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();

			int index = 0;
			while (rs.next()) {
				index++;
			}
			rs.close();
			s.close();
			return (index > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con l'id del giocatore nel database, avendo l'uuid
	 */
	public static String getUserVillo(int id) {

		String query = "SELECT * FROM `craftofclans_villages` WHERE `id_player`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();
			String id_villo = null;
			while (rs.next()) {
				String x = rs.getString("pointX");
				String y = rs.getString("pointZ");
				id_villo = x + ";" + y;
			}
			s.close();
			return id_villo;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Controllo se un villaggio è disponbile per il claim
	 */
	public static boolean isVillageAvailable(int x, int z) {
		long start_time = new Date().getTime();
		String query = "SELECT * FROM `craftofclans_villages` WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			s.setLong(1, x);
			s.setLong(2, z);
			ResultSet rs = s.executeQuery();
			int index = 0;
			while (rs.next()) {
				index++;
			}
			rs.close();
			s.close();
			long end_time = new Date().getTime();
			long differenza = end_time - start_time;
			System.out.println("isVillageAvailable(" + x + "," + z + ") require ms: " + differenza);
			s.close();
			return (index <= 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con tutti i villaggi registrati
	 */
	public static ArrayList<String> getVillageNotAvailable() {
		ArrayList<String> lista = new ArrayList<String>();
		String query = "SELECT * FROM `craftofclans_villages`";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				String x = rs.getString("pointX");
				String z = rs.getString("pointZ");
				String finale = x + ";" + z;
				lista.add(finale);
			}
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	/**
	 * Registro il villaggio per l'utente
	 */
	public static boolean villoRegister(int x, int z, int id_user, int height) {
		String query = "INSERT INTO `craftofclans_villages` (`id`, `id_player`, `pointX`, `pointZ`, `expanded`, `height`) VALUES (NULL, ?, ?, ?, '0', ?);";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_user);
			s.setLong(2, x);
			s.setLong(3, z);
			s.setInt(4, height);
			s.execute();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo un espansione
	 */
	public static boolean updateExpandedVillo(int x, int z) {
		String query = "UPDATE `craftofclans_villages` SET expanded=expanded+1 WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setLong(1, x);
			s.setLong(2, z);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Elimino il villaggio
	 */
	public static boolean deleteVillage(int x, int z, int id_user) {
		String query = "DELETE FROM `craftofclans_villages` WHERE `id_player`=(?) AND `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_user);
			s.setLong(2, x);
			s.setLong(3, z);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Get numero delle espansioni
	 */
	public static int getExpandedVillo(int x, int z) {
		String query = "SELECT * FROM `craftofclans_villages` WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setLong(1, x);
			s.setLong(2, z);
			ResultSet rs = s.executeQuery();
			int id = 0;
			while (rs.next()) {
				id = rs.getInt("expanded");
			}
			s.close();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Prendo l'owner del villaggio
	 */
	public static int getOwnerVillo(int x, int z) {
		String query = "SELECT * FROM `craftofclans_villages` WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setLong(1, x);
			s.setLong(2, z);
			ResultSet rs = s.executeQuery();
			int id = 0;
			while (rs.next()) {
				id = rs.getInt("id_player");
			}
			s.close();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Prendo le informazioni del villaggio
	 */
	public static HashMap<String, String> getInfoVillage(int x, int z) {
		HashMap<String, String> mappa = new HashMap<>();

		String query = "SELECT * FROM `craftofclans_villages` WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setLong(1, x);
			s.setLong(2, z);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				mappa.put("id_player", rs.getString("id_player"));
				mappa.put("pointX", rs.getString("pointX"));
				mappa.put("pointZ", rs.getString("pointZ"));
				mappa.put("expanded", rs.getString("expanded"));
				mappa.put("data_shield", rs.getString("data_shield"));
				mappa.put("data_last_attack", rs.getString("data_last_attack"));
				mappa.put("height", rs.getInt("height") + "");
			}
			s.close();
			return mappa;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mappa;
	}

	/**
	 * Inserisco lo scudo per il villaggio
	 */
	public static boolean VillosetScudo(int x, int z, String time) {
		String query = "UPDATE `craftofclans_villages` SET data_shield = (?) WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, time);
			s.setLong(2, x);
			s.setLong(3, z);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Imposto la ultima data di attacco
	 */
	public static boolean VillosetDataAttacco(int x, int z) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		String query = "UPDATE `craftofclans_villages` SET data_last_attack = (?) WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, dateFormat.format(date));
			s.setLong(2, x);
			s.setLong(3, z);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Prendo la data dello scudo
	 */
	public static String getDataShield(int x, int z) {
		String query = "SELECT data_shield FROM `craftofclans_villages` WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setLong(1, x);
			s.setLong(2, z);
			ResultSet rs = s.executeQuery();
			String data = "";
			while (rs.next()) {
				data = rs.getString("data_shield");
			}
			s.close();
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Ritorno con il numero delle gemme di un utente
	 */
	public static double getGemsUser(UUID uuid) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			double num = 0;
			while (rs.next()) {
				num = rs.getDouble("gems");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Rimuovo le gemme all'utente
	 */
	public static boolean removeGemsUser(UUID uuid, double quantita) {
		String query = "UPDATE `craftofclans_users` SET gems=gems-" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo le gemme all'utente
	 */
	public static boolean addGemsUser(UUID uuid, double quantita) {
		String query = "UPDATE `craftofclans_users` SET gems=gems+" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Imposto le gemme all'utente
	 */
	public static boolean setGemsUser(UUID uuid, double quantita) {
		String query = "UPDATE `craftofclans_users` SET gems=" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con il numero dell'elixir dell'utente
	 */
	public static double getElixirUser(UUID uuid) {
		if (uuid == null)
			return 0;
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			double num = 0;
			while (rs.next()) {
				num = rs.getDouble("elixir");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Rimuovo elixir dall'utente
	 */
	public static boolean removeElixirUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET elixir=elixir-" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo elixir all'utente
	 */
	public static boolean addElixirUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET elixir=elixir+" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Imposto elixir all'utente
	 */
	public static boolean setElixirUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET elixir=" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con il numero dell'oro dell'utente
	 */
	public static double getGoldUser(UUID uuid) {
		if (uuid == null)
			return 0;
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();

			double num = 0;
			while (rs.next()) {
				num = rs.getDouble("gold");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Rimuovo oro dall'utente
	 */
	public static boolean removeGoldUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET gold=gold-" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo gold all'utente
	 */
	public static boolean addGoldUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET gold=gold+" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Imposto gold all'utente
	 */
	public static boolean setGoldUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET gold=" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con il numero dell'elixir nero dell'utente
	 */
	public static double getElixirNeroUser(UUID uuid) {
		if (uuid == null)
			return 0;
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			double num = 0;
			while (rs.next()) {
				num = rs.getDouble("darkelixir");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Rimuovo elixir nero dall'utente
	 */
	public static boolean removeElixirNeroUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET darkelixir=darkelixir-" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo elixir nero all'utente
	 */
	public static boolean addElixirNeroUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET darkelixir=darkelixir+" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Imposto elixir nero all'utente
	 */
	public static boolean setElixirNeroUser(UUID uuid, double quantita) {
		if (uuid == null)
			return false;
		String query = "UPDATE `craftofclans_users` SET darkelixir=" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con il numero totale dei villaggi
	 */
	public static int getVillagesNum() {
		String query = "SELECT * from `craftofclans_villages`";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			ResultSet rs = s.executeQuery();

			int index = 0;
			while (rs.next()) {
				index++;
			}
			rs.close();
			s.close();
			return index;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * GESTIONE DEI TROFEI DEL GIOCATORE
	 */
	public static int getTrofeiUser(UUID uuid) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			int num = 0;
			while (rs.next()) {
				num = rs.getInt("trophies");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Rimuovo trofei dall'utente
	 */
	public static boolean removeTrofeiUser(UUID uuid, double quantita) {
		String query = "UPDATE `craftofclans_users` SET trophies=trophies-" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo trofei all'utente
	 */
	public static boolean addTrofeiUser(UUID uuid, double quantita) {
		String query = "UPDATE `craftofclans_users` SET trophies=trophies+" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Imposto trofei all'utente
	 */
	public static boolean setTrofeiUser(UUID uuid, double quantita) {
		String query = "UPDATE `craftofclans_users` SET trophies=" + quantita + " WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Aggiungo una vincita all'utente
	 */
	public static boolean addWin(UUID uuid) {
		String query = "UPDATE `craftofclans_users` SET `attacks_win`=attacks_win+1 WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con il numero delle vittorie effettuate dal giocatore
	 */
	public static int getWin(UUID uuid) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			int num = 0;
			while (rs.next()) {
				num = rs.getInt("attacks_win");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Aggiungo una perdita all'utente
	 */
	public static boolean addLost(UUID uuid) {
		String query = "UPDATE `craftofclans_users` SET attacks_lost = attacks_lost + 1 WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ritorno con il numero delle partite perse dal giocatore
	 */
	public static int getLost(UUID uuid) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			int num = 0;
			while (rs.next()) {
				num = rs.getInt("attacks_lost");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Ritorno con le Tiers acquistate dal giocatore
	 */
	public static String getTiers(UUID uuid) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, uuid.toString());
			ResultSet rs = s.executeQuery();
			String num = "";
			while (rs.next()) {
				num = rs.getString("tiers");
			}
			s.close();
			return num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Aggiungo una Tier all'utente
	 */
	public static boolean addTier(UUID uuid, String name) {
		String query = "UPDATE `craftofclans_users` SET `tiers`=(?) WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);

			String before = getTiers(uuid);
			List<String> tiers_parse = new ArrayList<String>();
			if (before != null && before != "" && before.contains(",")) {
				tiers_parse.addAll(Arrays.asList(before.split(",")));
			}
			if (before != null && before.contains(",")) {
				tiers_parse.add(name);
			} else {
				tiers_parse.add(name + ",");
			}
			String result = tiers_parse.toString().replace("[", "").replace("]", "").replace(" ", "");
			s.setString(1, result);
			s.setString(2, uuid.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Rimuovo una Tier all'utente
	 */
	public static boolean removeTier(UUID uuid, String name) {
		String query = "UPDATE `craftofclans_users` SET `tiers`=(?) WHERE `uuid`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);

			String before = getTiers(uuid);
			List<String> tiers_parse = new ArrayList<String>();
			if (before != null && before != "" && before.contains(",")) {
				for (String nometier : before.split(",")) {
					if (!nometier.equals(name)) {
						tiers_parse.add(nometier);
					}
				}
			}
			String result = tiers_parse.toString().replace("[", "").replace("]", "").replace(" ", "");
			s.setString(1, result);
			s.setString(2, uuid.toString());
			s.executeUpdate();

			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * GESTIONE CLANS Controllo se esiste già un clan con quel Name
	 */
	public static boolean existClan(String name) {
		String query = "SELECT * FROM `craftofclans_clans` WHERE name=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			s.setString(1, name);
			ResultSet rs = s.executeQuery();

			int index = 0;
			while (rs.next()) {
				index++;
			}
			rs.close();
			s.close();
			return (index > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * GESTIONE CLANS Creo il clan
	 */
	public static boolean createClan(String name, String desc, ClansTypes type, int min_trophies, UUID id) {
		String query = "INSERT INTO `craftofclans_clans` (`id`, `id_owner`, `name`, `desc`, `type`, `min_trophies`) VALUES (NULL, ?, ?, ?, ?, ?);";
		if (!isUserExists(id))
			return false;
		int id_user = getuserId(id);
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			s.setInt(1, id_user);
			s.setString(2, name);
			s.setString(3, desc);
			s.setString(4, type.toString());
			s.setInt(5, min_trophies);
			s.execute();
			int id_last = s.getGeneratedKeys().getInt(1);

			// Aggiorno lo stato del giocatore
			String query2 = "UPDATE `craftofclans_users` SET id_clan=(?) WHERE `id`=(?)";
			PreparedStatement s2 = (PreparedStatement) StorageFlat.con.prepareStatement(query2);
			s2.setInt(1, id_last);
			s2.setInt(2, id_user);
			s2.execute();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Controllo se il giocatore ha un clan
	 */
	public static boolean hasPlayerClan(UUID id) {
		if (!isUserExists(id))
			return false;
		int id_user = getuserId(id);
		String query = "SELECT * FROM `craftofclans_users` WHERE id=(?) AND id_clan != 0";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			s.setInt(1, id_user);
			ResultSet rs = s.executeQuery();

			int index = 0;
			while (rs.next()) {
				index++;
			}
			rs.close();
			s.close();
			return (index > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Informazioni del clan
	 */
	public static HashMap<String, String> getClanInfo(int id) {
		HashMap<String, String> clans = new HashMap<>();
		String query = "SELECT * FROM `craftofclans_clans` WHERE `id`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				clans.put("id", rs.getString("id"));
				clans.put("id_owner", rs.getString("id_owner"));
				clans.put("name", rs.getString("name"));
				clans.put("desc", rs.getString("desc"));
				clans.put("type", rs.getString("type"));
				clans.put("min_trophies", rs.getString("min_trophies"));
			}
			s.close();
			return clans;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ritorno con l'id del clan del player
	 */
	public static int getClanIdFromPlayer(UUID id) {
		if (!hasPlayerClan(id))
			return 0;
		int id_user = getuserId(id);
		String query = "SELECT * FROM `craftofclans_users` WHERE id=(?)";
		int id_clan = 0;

		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_user);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				// Prendo l'id del clan
				id_clan = rs.getInt("id_clan");
			}
			s.close();
			return id_clan;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Aggiorno la data dell'ultimo login
	 */
	public static boolean updateLastLogin(UUID player) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		int id_user = getuserId(player);
		String query = "UPDATE `craftofclans_users` SET `last_login` = (?) WHERE `id` = (?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, dateFormat.format(date));
			s.setInt(2, id_user);
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Prendo l'id del clan utilizzando il nome
	 */
	public static int getClanByName(String name) {
		String query = "SELECT * FROM `craftofclans_clans` WHERE `name` = (?)";
		int id_clan = 0;

		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				id_clan = rs.getInt("id");
			}
			s.close();
			return id_clan;
		} catch (SQLException e) {

		}
		return id_clan;
	}

	/**
	 * Ritorno con il nickname del giocatore tramite l'id salvato nel database
	 */
	public static UUID getUUIDFromPlayer(int id) {

		String query = "SELECT * FROM `craftofclans_users` WHERE `id`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();
			UUID nick = null;
			while (rs.next()) {
				nick = UUID.fromString(rs.getString("uuid"));
			}
			s.close();
			return nick;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Imposto l'id del clan all'utente
	 */
	public static void setPlayerClan(UUID player, Integer id_clan) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		int id_user = getuserId(player);
		String query = "UPDATE `craftofclans_users` SET `id_clan` = (?), `last_update_clan` = (?) WHERE `id` = (?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_clan);
			s.setString(2, dateFormat.format(date));
			s.setInt(3, id_user);
			s.executeUpdate();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ritorno con la lista dei nomi presenti nel clan
	 */
	public static ArrayList<UUID> getMembersOfClan(Integer id_clan) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `id_clan`=(?)";
		ArrayList<UUID> lista_membri = new ArrayList<>();
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_clan);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				lista_membri.add(UUID.fromString(rs.getString("uuid")));
			}
			s.close();
			return lista_membri;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Elimino un clan (Avendo l'id)
	 */
	public static void deleteClan(int id_clan) {
		String query = "DELETE FROM `craftofclans_clans` WHERE id=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_clan);
			s.executeUpdate();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cerco un nuovo membro da assegnare come owner ( Il proprietario ha lasciato
	 * il clan)
	 */
	public static int getNewOwnerID(int id_clan, int id_before) {
		String query = "SELECT * FROM `craftofclans_users` WHERE `id_clan`=(?) AND `id` != (?) ORDER BY `last_update_clan` ASC LIMIT 1";
		int id_owner = 0;
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_clan);
			s.setInt(2, id_before);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				id_owner = rs.getInt("id");
			}
			s.close();
			return id_owner;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id_owner;
	}

	/**
	 * Aggiorno l'id dell'owner
	 */
	public static void updateIdOwner(int id_clan, int id_owner) {
		String query = "UPDATE `craftofclans_clans` SET `id_owner`=(?) WHERE `id`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_owner);
			s.setInt(2, id_clan);
			s.executeUpdate();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Faccio l'update di un campo specifico per il clan
	 */
	public static void updateClanData(String key, String value, int id_clan) {
		String query = "UPDATE `craftofclans_clans` SET `" + key + "`=(?) WHERE `id`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setString(1, value);
			s.setInt(2, id_clan);
			s.executeUpdate();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Faccio l'update di un campo specifico per il clan
	 */
	public static void updateClanData(String key, int value, int id_clan) {
		String query = "UPDATE `craftofclans_clans` SET `" + key + "`=(?) WHERE `id`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, value);
			s.setInt(2, id_clan);
			s.executeUpdate();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lista dei clan registrati nel server
	 */
	public static ArrayList<Integer> getClans() {
		ArrayList<Integer> ids = new ArrayList<>();
		String query = "SELECT id FROM `craftofclans_clans`";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				ids.add(rs.getInt("id"));
			}
			s.close();
			return ids;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Prendo la data dall'ultimo attacco
	 */
	public static String getLastVillageAttack(int x, int z) {
		String query = "SELECT data_last_attack FROM `craftofclans_villages` WHERE `pointX`=(?) AND `pointZ`=(?)";
		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setLong(1, x);
			s.setLong(2, z);
			ResultSet rs = s.executeQuery();
			String data = "";
			while (rs.next()) {
				data = rs.getString("data_last_attack");
			}
			s.close();
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Registro un attacco
	 */
	public static boolean registroAttacco(UUID id, VillageId villo, double gems_won, double gold_won, double elixir_won, double darkelixir_won, int percentage, int trofei_vinti, int trofei_persi) {

		String query = "INSERT INTO `craftofclans_attacks` (`id`, `village`, `id_attacker`, `gems_won`, `gold_won`, `elixir_won`, " + "`darkelixir_won`, `percentage`, `trophies_won`, `trophies_lost`, `data_attack`) VALUES " + "(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		if (!isUserExists(id))
			return false;
		int id_user = getuserId(id);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			s.setString(1, villo.getID());
			s.setInt(2, id_user);
			s.setDouble(3, gems_won);
			s.setDouble(4, gold_won);
			s.setDouble(5, elixir_won);
			s.setDouble(6, darkelixir_won);
			s.setDouble(7, percentage);
			s.setDouble(8, trofei_vinti);
			s.setDouble(9, trofei_persi);
			s.setString(10, dateFormat.format(date));
			s.execute();
			s.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Lista degli attacchi
	 */
	public static HashMap<String, HashMap<String, String>> getAttacks(UUID id, int index) {
		HashMap<String, HashMap<String, String>> attacchi = new HashMap<>();

		int id_user = getuserId(id);
		int perpagina = 10;
		int pagina = index == 0 || index < 0 ? 1 : index;
		int paginastart = perpagina * (pagina - 1);

		String query = "SELECT * FROM `craftofclans_attacks` WHERE `id_attacker` = ? ORDER BY id DESC";
		if (index != -1) {
			query += " LIMIT " + paginastart + "," + perpagina;
		}

		try {
			PreparedStatement s = (PreparedStatement) StorageFlat.con.prepareStatement(query);
			s.setInt(1, id_user);

			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				HashMap<String, String> attacco = new HashMap<>();
				attacco.put("id", rs.getString("id"));
				attacco.put("village", rs.getString("village"));
				attacco.put("gems_won", rs.getString("gems_won"));
				attacco.put("gold_won", rs.getString("gold_won"));
				attacco.put("elixir_won", rs.getString("elixir_won"));
				attacco.put("darkelixir_won", rs.getString("darkelixir_won"));
				attacco.put("percentage", rs.getString("percentage"));
				attacco.put("trophies_won", rs.getString("trophies_won"));
				attacco.put("trophies_lost", rs.getString("trophies_lost"));
				attacco.put("data_attack", rs.getString("data_attack"));
				attacchi.put(rs.getString("id"), attacco);
			}
			s.close();
			return attacchi;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attacchi;
	}
}
