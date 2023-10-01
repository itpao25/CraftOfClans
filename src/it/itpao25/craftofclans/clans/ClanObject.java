package it.itpao25.craftofclans.clans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;

public class ClanObject {

	private int id;
	private int id_owner;
	private String name;
	private String desc;
	private ClansTypes type;
	private int min_trophies;
	public ArrayList<UUID> id_members_list = new ArrayList<>();

	/**
	 * Prendo il clan dall'id
	 * 
	 * @param id
	 */
	public ClanObject(int id) {
		this.setId(id);

		HashMap<String, String> info = new HashMap<>();
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			info = StorageMySQLRead.getClanInfo(id);
		} else {
			info = StorageFlatRead.getClanInfo(id);
		}

		this.setId(Integer.parseInt(info.get("id")));
		this.id_owner = Integer.parseInt(info.get("id_owner"));
		this.setName(info.get("name"));
		this.setDesc(info.get("desc"));
		this.type = ClansTypes.valueOf(info.get("type"));
		this.min_trophies = Integer.parseInt(info.get("min_trophies"));

		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			id_members_list = StorageMySQLRead.getMembersOfClan(this.id);
		} else {
			id_members_list = StorageFlatRead.getMembersOfClan(this.id);
		}
	}

	/**
	 * Prendo il clan utilizzando il nome
	 * 
	 * @param name
	 * @return
	 */
	public static ClanObject getByName(String name) {
		int id_clan = 0;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			id_clan = StorageMySQLRead.getClanByName(name);
		} else {
			id_clan = StorageFlatRead.getClanByName(name);
		}
		if (id_clan != 0) {
			return new ClanObject(id_clan);
		}
		return null;
	}

	/**
	 * Update delle info data del clan ( Mysql o sqllite) *
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void updateData(String key, String value) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.updateClanData(key, value, this.id);
		} else {
			StorageFlatRead.updateClanData(key, value, this.id);
		}
	}

	public void updateData(String key, int value) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.updateClanData(key, value, this.id);
		} else {
			StorageFlatRead.updateClanData(key, value, this.id);
		}
	}

	/**
	 * @return the id_owner
	 */
	public int getIDOwner() {
		return id_owner;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc != null ? desc : "No description";
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the type
	 */
	public ClansTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ClansTypes type) {
		this.type = type;
	}

	/**
	 * @return the min_trophies
	 */
	public int getMinTrophies() {
		return min_trophies;
	}

	/**
	 * @param min_trophies the min_trophies to set
	 */
	public void setMinTrophies(int min_trophies) {
		this.min_trophies = min_trophies;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 * 
	 */
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof ClanObject) {
			if (((ClanObject) o).getId() == getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifico se l'id corrisponde all'owner
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasOwner(int id) {
		return id == getIDOwner();
	}

	/**
	 * Ritorno con i membri
	 * 
	 * HashMap PlayerStored - String (Ruolo nel clan)
	 * 
	 * @return
	 */
	public ArrayList<PlayerStored> getMembers() {
		ArrayList<PlayerStored> players = new ArrayList<>();
		for (UUID uuid : id_members_list) {
			players.add(new PlayerStored(uuid));
		}
		return players;
	}

	/**
	 * Aggiungo il player nell'HashMap
	 * 
	 * @param uuid
	 */
	public void addPlayerToMemory(UUID uuid) {
		this.id_members_list.add(uuid);
	}

	/**
	 * Ritorno con il numero dei trofei
	 * 
	 * @return
	 */
	public Integer getTotalTrophies() {
		int total = 0;
		for (PlayerStored pstored : this.getMembers()) {
			total = total + pstored.getTrofei();
		}
		return total;
	}

	/**
	 * Elimino il clan
	 * 
	 */
	public void disband() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.deleteClan(this.id);
		} else {
			StorageFlatRead.deleteClan(this.id);
		}
	}

	/**
	 * Il giocatore owner sta tentando di lasciare il clan
	 * 
	 * @return
	 */
	public boolean tryToDisband(int id_player) {

		// Controllo se ci sono oltre giocatori oltre a lui
		if (getMembers().size() == 1) {
			// Disbando anche il clan
			this.disband();
			return true;
		}

		// Ci sono altri giocatori nel clan
		int id_new_owner = 0;

		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			id_new_owner = StorageMySQLRead.getNewOwnerID(this.id, this.id_owner);
		} else {
			id_new_owner = StorageFlatRead.getNewOwnerID(this.id, this.id_owner);
		}
		this.updateIdOwner(id_new_owner);

		return false;
	}

	/**
	 * Controllo se il giocatore (byName) è presente nel clan
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasMemberByName(String name) {
		for (PlayerStored pstored : getMembers()) {
			if (pstored.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Ritorno con PlayerStored (Membro del clan)
	 * 
	 * @param name
	 * @return
	 */
	public PlayerStored getMemberByName(String name) {
		for (PlayerStored pstored : getMembers()) {
			if (pstored.getName().equals(name)) {
				return pstored;
			}
		}
		return null;
	}

	/**
	 * Aggiorno l'id del nuovo owner del clan
	 * 
	 * @param id_owner
	 */
	public void updateIdOwner(int id_owner) {
		this.id_owner = id_owner;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.updateIdOwner(this.id, id_owner);
		} else {
			StorageFlatRead.updateIdOwner(this.id, id_owner);
		}
	}

	/**
	 * Invia un messaggio a tutti gli utenti online del clan
	 * 
	 * @param str
	 */
	public void sendMessageOnlineMembers(String str) {
		for (PlayerStored pstored : getMembers()) {
			if (pstored.isOnline()) {
				pstored.sendMessage(str);
			}
		}
	}

}
