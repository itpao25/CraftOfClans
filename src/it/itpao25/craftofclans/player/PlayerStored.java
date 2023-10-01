package it.itpao25.craftofclans.player;

import it.itpao25.craftofclans.clans.ClanObject;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.tier.TierManager;
import it.itpao25.craftofclans.tier.TierObject;
import it.itpao25.craftofclans.troops.TroopsId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerStored {

	private Player p;
	private UUID uuid;
	private Integer id;
	private OfflinePlayer p_offline;
	private int id_clan = 0;
	public boolean is_creating_village = false;
	
	// Ultima transazione risultato - se true un addGold o addElixir hanno il valore massimo
	private boolean falso_per_pieno = false;

	public PlayerStored(Player player) {
		this.p = player;
		this.uuid = player.getUniqueId();
		this.p_offline = Bukkit.getOfflinePlayer(uuid);
		
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			this.id = StorageMySQLRead.getuserId(uuid);
		} else {
			this.id = StorageFlatRead.getuserId(uuid);
		}
		this.getIdClan();
	}

	public PlayerStored(UUID uuid) {
		if (Bukkit.getPlayer(uuid) != null) {
			this.p = Bukkit.getPlayer(uuid);
		}
		if (Bukkit.getOfflinePlayer(uuid) != null) {
			this.p_offline = Bukkit.getOfflinePlayer(uuid);
		}
		this.uuid = uuid;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			this.id = StorageMySQLRead.getuserId(uuid);
		} else {
			this.id = StorageFlatRead.getuserId(uuid);
		}

		this.getIdClan();
	}

	public PlayerStored(Integer id) {
		UUID uuid = null;
		
		this.id = id;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			uuid = StorageMySQLRead.getUUIDFromPlayer(id);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			uuid = StorageFlatRead.getUUIDFromPlayer(id);
		}
		if(uuid == null) {
			return;
		}
		
		
		OfflinePlayer playeroffline = Bukkit.getOfflinePlayer(uuid);
		if (playeroffline == null) {
			return;
		}
		p_offline = playeroffline;
		
		// Se il player è online
		if (p_offline.isOnline()) {
			this.p = playeroffline.getPlayer();
		}
		if (playeroffline.hasPlayedBefore()) {
			if (playeroffline.isOnline()) {
				Player player = null;
				if (DatabaseHandler.getType() == DatabaseType.MySQL) {
					player = Bukkit.getPlayer(StorageMySQLRead.getPlayerName(id));
				} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
					player = Bukkit.getPlayer(StorageFlatRead.getPlayerName(id));
				}
				if (player == null)
					return;
				this.p = player;
				this.uuid = player.getUniqueId();
			} else {
				this.uuid = playeroffline.getUniqueId();
			}
		}
		if (this.uuid != null) {
			this.getIdClan();
			return;
		}
	}

	/**
	 * Controllo se è presente l'utente nel sistema di storage
	 * 
	 * @return boolean - se l'utente esiste
	 */
	public boolean isExist() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.isUserExists(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.isUserExists(uuid);
		}
		return false;
	}

	/**
	 * Registro l'utente nel database
	 * 
	 * @return
	 */
	public boolean register() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.userRegister(p);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.userRegister(p);
		}
		return false;
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
		if (o instanceof PlayerStored) {
			if (((PlayerStored) o).getId() == getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Controllo se l'utente ha un villaggio salvato nei sistemi di storage
	 * 
	 * @return boolean - se ha un villaggio
	 */
	public boolean hasVillage() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.hasUserVillo(id);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.hasUserVillo(id);
		}
		return false;
	}

	/**
	 * Ritorno con il villaggio del giocatore
	 * 
	 * @return VillageID
	 */
	public VillageId getVillage() {
		if (hasVillage() == false)
			return null;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			String id = StorageMySQLRead.getUserVillo(getId());
			return new VillageId(id);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			String id = StorageFlatRead.getUserVillo(getId());
			return new VillageId(id);
		}
		return null;
	}

	/**
	 * Ritorno con l'uuid del giocatore
	 * 
	 * @return
	 */
	public UUID getUUID() {
		return uuid;

	}

	/**
	 * Ritorno con l'id del giocatore prelevato dal database
	 * 
	 * @return
	 */
	public int getId() {
		return id;

	}

	/**
	 * Ritorno con il nome del giocatore
	 * 
	 * @return
	 */
	public String getName() {
		if (p_offline != null) {
			return p_offline.getName();
		}
		return p.getName();
	}

	/**
	 * sendMessage per il giocatore
	 * 
	 * @param str
	 */
	public void sendMessage(String str) {
		if (isOnline())
			p.sendMessage(str);
	}

	/**
	 * Ritorno con l'instanza del giocatore
	 * 
	 * @return
	 */
	public Player get() {
		return p;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		if (p_offline != null) {
			return p_offline;
		}
		if (p != null) {
			return p;
		}
		return null;
	}
	
	public boolean isOnline() {
		if (p != null) {
			return p.isOnline();
		}
		if (p_offline != null) {
			return p_offline.isOnline();
		}
		return false;
	}

	/**
	 * GESTIONE DELLE GEMME ritorno con il numero delle gemme che ha il giocatore
	 * 
	 * @return float number of gems
	 */
	public double getGems() {
		return PlayerGems.get(uuid);
	}

	/**
	 * GESTIONE DELLE GEMME rimuovo delle gemme al giocatore
	 * 
	 * @return double number of gems
	 */
	public boolean removeGems(double numero_meno) {
		// Controllo se si sta tendando di rimuovere delle gemme
		// Che il giocatore non ha. Quindi il numero (numero_meno) è maggiore
		// delle gemme che ha il giocatore
		if (numero_meno > getGems()) {
			return false;
		}
		return PlayerGems.remove(uuid, numero_meno);
	}

	/**
	 * GESTIONE DELLE GEMME Controllo se il giocatore ha le gemme richieste, così da
	 * poter verificare prima di effettuare i pagamenti e gli scambi
	 */
	public boolean hasGems(double numero) {
		return getGems() >= numero ? true : false;
	}

	/**
	 * GESTIONE DELLE GEMME Imposto il numero delle gemme
	 * 
	 * @return double number of gems
	 */
	public boolean setGems(double numero_meno) {
		return PlayerGems.set(uuid, numero_meno);
	}

	/**
	 * GESTIONE DELLE GEMME
	 * 
	 * @param numero
	 * @return
	 */
	public boolean addGems(double numero) {
		return PlayerGems.add(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ELISIR ritorno con il numero massimo dell'elisir che è
	 * possibile avere nel giocatore Questo valore varia in base al numero dei
	 * depositi di elisir
	 * 
	 * @return Int numero massimo di elisir
	 */
	public double getMaxElixir() {
		if (hasVillage() == false) {
			return 0;
		}
		double max = 0;
		if (getVillage().getStructuresList().size() == max)
			return 0;
		for (Entry<StructuresId, String> item : getVillage().getStructuresList().entrySet()) {
			if (item.getKey().getType().equals("ELIXIR_STORAGE")) {
				max = max + item.getKey().getCapacity();
			}
		}
		return max;
	}

	/**
	 * GESTIONE DELL'ELISIR Ritorno con il value dell'elisir che ha il giocatore
	 * 
	 * @return
	 */
	public double getElixir() {
		return PlayerElixir.get(uuid);
	}

	/**
	 * GESTIONE DELL'ELISIR rimuovo l'elisir dal giocatore
	 * 
	 * @return
	 */
	public boolean removeElixir(double numero) {
		if (numero > getElixir()) {
			return false;
		}
		return PlayerElixir.remove(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ELISIR Controllo se il giocatore ha l'elixir richiesto, così da
	 * poter verificare prima di effettuare i pagamenti e gli scambi
	 */
	public boolean hasElixir(double numero) {
		return getElixir() >= numero ? true : false;
	}

	/**
	 * GESTIONE DELL'ELISIR Imposto il numero dell'elixir
	 * 
	 * @return double number of gems
	 */
	public boolean setElixir(double numero_meno) {
		return PlayerElixir.set(uuid, numero_meno);
	}

	/**
	 * GESTIONE DELL'ELISIR
	 */
	public boolean addElixir(double numero) {
		if (getElixir() + numero > getMaxElixir()) {
			falso_per_pieno = true;
			return false;
		}
		return PlayerElixir.add(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ELISIR
	 */
	public boolean addElixir(double numero, boolean ifControl) {
		if (ifControl == true) {
			if (getElixir() + numero > getMaxElixir()) {
				return false;
			}
		}
		return PlayerElixir.add(uuid, numero);
	}

	/**
	 * Controllo quanto spazio rimane al giocatore
	 */
	public double elixirSpaceRemains() {
		return getMaxElixir() - getElixir();
	}

	/**
	 * GESTIONE DELL'ORO ritorno con il numero massimo dell'oro che è possibile
	 * avere nel giocatore Questo valore varia in base al numero dei depositi di
	 * elisir
	 * 
	 * @return Int numero massimo di elisir
	 */
	public double getMaxGold() {
		if (hasVillage() == false) {
			return 0;
		}
		double max = 0;
		if (getVillage().getStructuresList().size() == max)
			return 0;
		for (Entry<StructuresId, String> item : getVillage().getStructuresList().entrySet()) {
			if (item.getKey().getType().equals("GOLD_STORAGE")) {
				max = max + item.getKey().getCapacity();
			}
		}
		return max;
	}

	/**
	 * GESTIONE DELL'ORO Ritorno con il value dell'oro che ha il giocatore
	 * 
	 * @return
	 */
	public double getGold() {
		return PlayerGold.get(uuid);
	}

	/**
	 * GESTIONE DELL'ORO rimuovo l'oro dal giocatore
	 * 
	 * @return
	 */
	public boolean removeGold(double numero) {
		if (numero > getGold()) {
			return false;
		}
		return PlayerGold.remove(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ORO rimuovo l'oro dal giocatore
	 * 
	 * @return
	 */
	public boolean addGold(double numero) {
		if (getGold() + numero > getMaxGold()) {
			falso_per_pieno = true;
			return false;
		}
		return PlayerGold.add(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ORO rimuovo l'oro dal giocatore
	 */
	public boolean addGold(double numero, boolean ifControl) {
		if (ifControl == true) {
			if (getGold() + numero > getMaxGold()) {
				return false;
			}
		}
		return PlayerGold.add(uuid, numero);
	}

	/**
	 * Controllo quanto spazio rimane al giocatore
	 * 
	 * @return
	 */
	public double goldSpaceRemains() {
		return getMaxGold() - getGold();
	}

	/**
	 * GESTIONE DELL'ORO Imposto il numero dell'oro
	 * 
	 * @return double number of gold
	 */
	public boolean setGold(double numero_meno) {
		return PlayerGold.set(uuid, numero_meno);
	}

	/**
	 * GESTIONE DELL'ORO Controllo se il giocatore ha l'oro richiesto, così da poter
	 * verificare prima di effettuare i pagamenti e gli scambi
	 */
	public boolean hasGold(double numero) {
		return getGold() >= numero ? true : false;
	}

	/**
	 * GESTIONE DELL'ELISIR NERO ritorno con il numero massimo dell'elisir nero che
	 * è possibile avere nel giocatore Questo valore varia in base al numero dei
	 * depositi di elisir
	 * 
	 * @return Int numero massimo di elisir nero
	 */
	public double getMaxElixirNero() {
		if (hasVillage() == false) {
			return 0;
		}
		double max = 0;
		if (getVillage().getStructuresList().size() == max)
			return 0;
		for (Entry<StructuresId, String> item : getVillage().getStructuresList().entrySet()) {
			if (item.getKey().getType().equals("DARK_ELIXIR_STORAGE")) {
				max = max + item.getKey().getCapacity();
			}
		}
		return max;
	}

	/**
	 * GESTIONE DELL'ELISIR NERO Ritorno con il value dell'elisir nero che ha il
	 * giocatore
	 * 
	 * @return
	 */
	public double getElixirNero() {
		return PlayerElixirNero.get(uuid);
	}

	/**
	 * GESTIONE DELL'ELISIR NERO rimuovo l'elisir nero dal giocatore
	 * 
	 * @return
	 */
	public boolean removeElixirNero(double numero) {
		if (numero > getElixirNero()) {
			return false;
		}
		return PlayerElixirNero.remove(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ELISIR NERO Controllo se il giocatore ha l'elixir nero
	 * richiesto, così da poter verificare prima di effettuare i pagamenti e gli
	 * scambi
	 */
	public boolean hasElixirNero(double numero) {
		return getElixirNero() >= numero ? true : false;
	}

	/**
	 * GESTIONE DELL'ELISIR NERO Imposto il numero dell'elixir nero
	 * 
	 * @return double number of elixir nero
	 */
	public boolean setElixirNero(double numero_meno) {
		return PlayerElixirNero.set(uuid, numero_meno);
	}

	/**
	 * GESTIONE DELL'ELISIR NERO
	 * 
	 * @param numero
	 * @return
	 */
	public boolean addElixirNero(double numero) {
		if (getElixirNero() + numero > getMaxElixirNero()) {
			falso_per_pieno = true;
			return false;
		}
		return PlayerElixirNero.add(uuid, numero);
	}

	/**
	 * GESTIONE DELL'ELISIR NERO
	 * 
	 * @param numero
	 * @param ifControl per impostare il controllo dello spazio o meno
	 * @return
	 */
	public boolean addElixirNero(double numero, boolean ifControl) {
		if (ifControl == true) {
			if (getElixirNero() + numero > getMaxElixirNero()) {
				return false;
			}
		}
		return PlayerElixirNero.add(uuid, numero);
	}

	/**
	 * Controllo quanto spazio rimane al giocatore
	 * 
	 * @return
	 */
	public double darkElixrSpaceRemains() {
		return getMaxElixirNero() - getElixirNero();
	}

	/**
	 * GESTIONE DEI TROFEI
	 * 
	 * @param numero
	 * @return
	 */
	public boolean addTrofei(int numero) {
		return PlayerTrophies.add(uuid, numero);
	}

	/**
	 * GESTIONE DEI TROFEI Ritorno con il value dell'oro che ha il giocatore
	 * 
	 * @return
	 */
	public Integer getTrofei() {
		return PlayerTrophies.get(uuid);
	}

	/**
	 * GESTIONE DEI TROFEI rimuovo i trofei dal giocatore
	 * 
	 * @return
	 */
	public boolean removeTrofei(int numero) {
		if (numero > getTrofei()) {
			return false;
		}
		return PlayerTrophies.remove(uuid, numero);
	}

	/**
	 * GESTIONE DEI TROFEI
	 */
	public boolean hasTrofei(int numero) {
		return getTrofei() >= numero ? true : false;
	}

	/**
	 * GESTIONE DEI TROFEI
	 */
	public boolean setTrofei(int numero) {
		return PlayerTrophies.set(uuid, numero);
	}

	/**
	 * Aggiungo al giocatore un attacco completato (vinto)
	 */
	public boolean addWin() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addWin(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addWin(uuid);
		}
		return false;
	}

	public int getWin() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getWin(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getWin(uuid);
		}
		return -1;
	}

	/**
	 * Aggiungo al giocatore un attacco completato (perso)
	 */
	public boolean addLost() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addLost(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addLost(uuid);
		}
		return false;
	}

	public int getLost() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getLost(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getLost(uuid);
		}
		return -1;
	}

	/**
	 * Controllo se il giocatore ha comprato la Tier
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasTier(String name) {

		String string = null;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			string = StorageMySQLRead.getTiers(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			string = StorageFlatRead.getTiers(uuid);
		}
		if (string == null || string == "") {
			return false;

		}
		if (string.contains(",") == false) {
			if (string.equals(name)) {
				return true;
			}
		}
		List<String> tiers_parse = Arrays.asList(string.split(","));
		if (tiers_parse.contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Aggiungo la Tier al giocatore
	 * 
	 * @param name
	 * @return
	 */
	public boolean addTier(String name) {
		// Se ha già la tier
		if (this.hasTier(name)) {
			return false;
		}

		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.addTier(this.uuid, name);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.addTier(this.uuid, name);
		}
		return true;
	}

	/**
	 * Remuovo la Tier al giocatore
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeTier(String name) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.removeTier(this.uuid, name);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.removeTier(this.uuid, name);
		}
		return true;
	}

	/**
	 * Prendo l'ultima tier acquistata
	 * 
	 * @return
	 */
	public TierObject getLastBuyedTier() {

		String string = null;
		String nome = null;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			string = StorageMySQLRead.getTiers(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			string = StorageFlatRead.getTiers(uuid);
		}
		if (string == null || string == "") {
			return null;

		}
		if (string.contains(",") == false) {
			nome = string;
		} else {
			List<String> tierList = Arrays.asList(string.split(","));
			nome = tierList.get(tierList.size() - 1);
		}

		TierObject tierobj = new TierObject(nome);
		if (tierobj.exits()) {
			return tierobj;
		}
		return null;
	}

	/**
	 * Prendo il prossimo tier da comprare
	 * 
	 * @return
	 */
	public TierObject getNextTier() {
		if (getLastBuyedTier() == null) {
			return null;
		}

		String nome_last = getLastBuyedTier().name;
		boolean is_found_last = false;

		ArrayList<String> next_tiers = new ArrayList<String>();
		for (String string : TierManager.ListTier()) {
			if (string.equals(nome_last)) {
				is_found_last = true;
				continue;
			}
			if (is_found_last) {
				next_tiers.add(string);
			}
		}

		if (next_tiers.size() == 0) {
			return null;
		}

		String next_tier = next_tiers.get(0);
		TierObject obj = new TierObject(next_tier);
		if (obj.exits()) {
			return obj;
		}
		return null;
	}

	/**
	 * Se il giocatore è in un clan o meno
	 * 
	 * @return
	 */
	public boolean hasClan() {
		return this.id_clan == 0 ? false : true;
	}

	/**
	 * Ritorno con l'id del clan del giocatore
	 * 
	 * @return int id clan
	 */
	public int getIdClan() {
		if (uuid == null)
			return 0;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			this.id_clan = StorageMySQLRead.getClanIdFromPlayer(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			this.id_clan = StorageFlatRead.getClanIdFromPlayer(uuid);
		}
		return this.id_clan;
	}

	/**
	 * Ritorno con l'oggetto del clan
	 * 
	 * @return
	 */
	public ClanObject getClan() {
		return new ClanObject(getIdClan());
	}

	public boolean hasOwnerClan() {
		return new ClanObject(getIdClan()).hasOwner(this.id);
	}

	/**
	 * Imposto il clan per il giocatore In questo momento viene aggiornato il campo
	 * "last_update_clan"
	 * 
	 * @return
	 */
	public void setIdClan(int id_clan) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.setPlayerClan(uuid, id_clan);
		} else {
			StorageFlatRead.setPlayerClan(uuid, id_clan);
		}
		this.id_clan = id_clan;
	}

	/**
	 * Ritorno con lo stato dell'ultima transazione addGold o addElixir
	 * 
	 * @return
	 */
	public boolean getLastResultFull() {
		return falso_per_pieno;
	}

	/**
	 * Aggiorno la data di ultimo login
	 * 
	 * @return
	 */
	public boolean setLastLogin() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.updateLastLogin(uuid);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.updateLastLogin(uuid);
		}
		return false;
	}

	/**
	 * Controllo se il giocatore ha comprato la truppa
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasTroop(String name, int level) {
		if (CraftOfClansData.get().getString("villages." + getVillage().getIDescaped() + ".troops") == null) {
			return false;
		}

		for (String key : CraftOfClansData.get().getConfigurationSection("villages." + getVillage().getIDescaped() + ".troops").getKeys(false)) {
			if (key.equalsIgnoreCase(name)) {
				if (level == 0) {
					return true;
				}
				// Se ha la truppa con quel livello
				if (CraftOfClansData.get().getString("villages." + getVillage().getIDescaped() + ".troops." + key + ".level." + level) != null) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	/**
	 * Aggiungo la truppa al giocatore
	 * 
	 * @param name
	 * @return
	 */
	public boolean addTroop(String name, int level) {

		// Se ha già la tier
		if (this.hasTroop(name, level)) {
			return false;
		}

		if (CraftOfClansData.get().getString("villages." + getVillage().getIDescaped() + ".troops") == null) {
			List<String> list = new ArrayList<String>();
			CraftOfClansData.get().set("villages." + getVillage().getIDescaped() + ".troops", list);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		// Inserisco la truppa nel file
		CraftOfClansData.get().set("villages." + getVillage().getIDescaped() + ".troops." + name + ".level", level);
		CraftOfClansData.get().set("villages." + getVillage().getIDescaped() + ".troops." + name + ".last_recharge", dateFormat.format(date));
		CraftOfClansData.get().set("villages." + getVillage().getIDescaped() + ".troops." + name + ".last_withdrawal", "0000-00-000 00:00:00");

		CraftOfClansData.save();

		return true;
	}

	/**
	 * Remuovo la truppa al giocatore
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeTroop(String name) {

		CraftOfClansData.get().set("villages." + getVillage().getIDescaped() + ".troops." + name, null);
		CraftOfClansData.save();
		return true;
	}

	/**
	 * Prendo la truppa
	 * 
	 * @param name
	 * @return
	 */
	public TroopsId getTroop(String name) {
		for (String key : CraftOfClansData.get().getConfigurationSection("villages." + getVillage().getIDescaped() + ".troops").getKeys(false)) {
			if (key.equalsIgnoreCase(name)) {
				int level = CraftOfClansData.getInt("villages." + getVillage().getIDescaped() + ".troops." + name + ".level");
				return new TroopsId(this, name, level);
			}
		}
		return null;
	}

	/**
	 * Prendo tutte le truppe del giocatore
	 */
	public ArrayList<TroopsId> getTroops() {
		ArrayList<TroopsId> truppe = new ArrayList<>();
		if (CraftOfClansData.get().getString("villages." + getVillage().getIDescaped() + ".troops") != null) {
			for (String key : CraftOfClansData.get().getConfigurationSection("villages." + getVillage().getIDescaped() + ".troops").getKeys(false)) {
				int level = CraftOfClansData.getInt("villages." + getVillage().getIDescaped() + ".troops." + key + ".level");

				// Truppe
				TroopsId truppaid = new TroopsId(this, key, level, "villages." + getVillage().getIDescaped() + ".troops." + key);
				truppe.add(truppaid);
			}
		}
		return truppe;
	}

	/**
	 * Registro un attacco nel server
	 * 
	 * @param villo
	 * @param gems_won
	 * @param gold_won
	 * @param elixir_won
	 * @param darkelixir_won
	 * @param percentage
	 */
	public boolean addRegistredAttack(VillageId villo, double gems_won, double gold_won, double elixir_won, double darkelixir_won, int percentage, int trofei_vinti, int trofei_persi) {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.registroAttacco(uuid, villo, gems_won, gold_won, elixir_won, darkelixir_won, percentage, trofei_vinti, trofei_persi);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.registroAttacco(uuid, villo, gems_won, gold_won, elixir_won, darkelixir_won, percentage, trofei_vinti, trofei_persi);
		}
		return false;
	}

	/**
	 * Lista degli attacchi effettuati dal player
	 * 
	 * @param p
	 * @param index
	 * @throws SQLException 
	 */
	public void getListAttacks(CommandSender p, int index) throws SQLException {
		
		int perpagina = 5;
		int pagina = index == 0 || index < 0 ? 1 : index;

		HashMap<String, HashMap<String, String>> run = null;
		
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			run = StorageMySQLRead.getAttacks(this.uuid, -1);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			run = StorageFlatRead.getAttacks(this.uuid, -1);
		}

		// Conto quante segnalazioni ci sono nel database
		int tot = run.size();
		
		double totalpage = Math.ceil(tot / perpagina) + 1;
		
		HashMap<String, HashMap<String, String>> run_s = null;
		
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			run_s = StorageMySQLRead.getAttacks(this.uuid, pagina);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			run_s = StorageFlatRead.getAttacks(this.uuid, pagina);
		}
		
		int num = 0;
		
		for (Entry<String, HashMap<String, String>> key : run_s.entrySet()) {
			if (num == 0) {
				p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.attacks-list-title")));
			}
			
			// Preparo la stringa
			String text = CraftOfClansM.getString("commands-syntax.attacks-list-layout");
			text = text.replace("[perc]", key.getValue().get("percentage"));
			text = text.replace("[gold_won]", key.getValue().get("gold_won"));
			text = text.replace("[gems_won]", key.getValue().get("gems_won"));
			text = text.replace("[elixir_won]", key.getValue().get("elixir_won"));
			text = text.replace("[lost_trophies]", key.getValue().get("trophies_lost"));
			text = text.replace("[won_trophies]", key.getValue().get("trophies_won"));
			text = text.replace("[time_ago]", _Number.getFrom(key.getValue().get("data_attack")));
			
			p.sendMessage(Color.translate(text));

			p.sendMessage("");
			
			num++;
		}

		// Se non è presente nessun report invio il messaggio di errore
		if (num == 0) {
			p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.attacks-list-not-found")));
			return;
		}

		String footer = CraftOfClansM.getString("commands-syntax.attacks-list-footer");
		footer = footer.replace("[current]", pagina + "");
		footer = footer.replace("[totpage]", (int) totalpage + "");
		footer = footer.replace("[totalreport]", tot + "");

		p.sendMessage(Color.translate(footer));
	}
}
