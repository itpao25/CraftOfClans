package it.itpao25.craftofclans.village;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import com.google.common.collect.Lists;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.attack.Attack;
import it.itpao25.craftofclans.attack.AttackerManager;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.guardian.GuardianNPCTrait;
import it.itpao25.craftofclans.guardian.GuardianVillage;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.Expander;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlatRead;
import it.itpao25.craftofclans.storage.StorageMySQLRead;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.Structures;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;
import net.citizensnpcs.api.npc.NPC;

public class VillageId {

	private final int x;
	private final int z;
	private Date data_scudo;
	public OfflinePlayer owner;
	public PlayerStored powner;

	public int freemode_height;

	public VillageId(int x, int z) {
		this.x = x;
		this.z = z;

		setData();
	}

	public VillageId(int x, int z, boolean is_only_check) {
		this.x = x;
		this.z = z;

		if (!is_only_check) {
			setData();
		}
	}

	/**
	 * Genero un nuovo villaggio specificando l'altezza
	 * 
	 * @param x
	 * @param z
	 * @param y
	 */
	public VillageId(int x, int z, int y) {
		this.x = x;
		this.z = z;
		this.freemode_height = y;
	}

	public VillageId(String id) throws NumberFormatException {
		this.x = Integer.parseInt(id.substring(0, id.indexOf(';')));
		this.z = Integer.parseInt(id.substring(id.indexOf(';') + 1));

		setData();
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public String getID() {
		return x + ";" + z;
	}

	public String getIDescaped() {
		return x + "_" + z;
	}

	/**
	 * Imposto le informazioni del villaggio
	 */
	public void setData() {
		int id = 0;

		// Imposto l'owner del villaggio
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			id = StorageMySQLRead.getOwnerVillo(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			id = StorageFlatRead.getOwnerVillo(this.x, this.z);
		}
		
		powner = new PlayerStored(id);
		owner = powner.getOfflinePlayer();

		HashMap<String, String> data = new HashMap<>();
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			data = StorageMySQLRead.getInfoVillage(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			data = StorageFlatRead.getInfoVillage(this.x, this.z);
		}

		if (data == null || data.size() == 0)
			return;

		freemode_height = _Number.isNumero(data.get("height")) ? Integer.parseInt(data.get("height")) : VillageSettings.getHeight(null);
	}

	/**
	 * Controllo se il villaggio è disponibile
	 * 
	 * @return
	 */
	public boolean isAvailable() {
		for (VillageId c : VillagesHandler.villages) {
			if (c.getCuboid().contains(this.x, VillageSettings.getHeight(this), this.z)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Controllo se un giocatore è l'owner del villaggio tramite l'UUID
	 * 
	 * @param p
	 * @return
	 */
	public boolean isOwner(PlayerStored p) {
		if (owner.getUniqueId().equals(p.getOfflinePlayer().getUniqueId())) {
			return true;
		}
		return false;
	}

	/**
	 * Controllo se un giocatore è l'owner del villaggio è online
	 */
	public boolean isOwnerOnline() {
		if (owner.isOnline()) {
			return true;
		}
		return false;
	}

	public int getOwnerID() {
		return powner.getId();
	}

	/**
	 * Ritorno con il nome del giocatore proprietario del villo
	 * 
	 * @return
	 */
	public String getOwnerName() {
		return owner.getName();
	}

	/**
	 * Converto la località in stringa
	 * 
	 * @param loc
	 * @return
	 */
	public static String loc2str(Location loc) {
		return loc.getWorld().getName() + " : " + loc.getBlockX() + "x : " + loc.getBlockY() + "y : " + loc.getBlockZ() + "z";
	}

	/**
	 * Teletrasporto un giocatore in questo villaggio
	 * 
	 * @param p
	 * @return
	 */
	public boolean tp(Player p) {
		return p.teleport(locBase()) ? true : false;
	}

	/**
	 * Ritorno con la località base del giocatore
	 * 
	 * @return
	 */
	public Location locBase() {

		Chunk center = getCuboid().getCenter().getChunk();
		Location newloc = center.getBlock(8, VillageSettings.getHeight(this), 8).getLocation();

		double look_directory = CraftOfClans.config.get().getDouble("player-settings.tp-onjoin-look-yaw");
		newloc.setYaw((float) look_directory);

		return newloc;
	}

	/**
	 * Location dello spectator mode
	 * 
	 * @return
	 */
	public Location spectatorLocation() {
		
		Location finale = locBase().clone();

		int height_tetto = VillageSettings.getHeight(this) + 17 > Bukkit.getWorld("clansworld").getMaxHeight() - 1 ? Bukkit.getWorld("clansworld").getMaxHeight() - 1 : VillageSettings.getHeight(this) + 17;
		finale.setY(height_tetto + 1);

		Location municipio = finale.clone();
		municipio.setY(VillageSettings.getHeight(this));

		// Imposto lo sguardo verso il municipio
		Location lookat = MapInfo.lookAt(finale, municipio);
		finale.setYaw(lookat.getYaw());
		finale.setPitch(lookat.getPitch());

		return finale;
	}

	/**
	 * Registro il villaggio nel database
	 * 
	 * @param p
	 * @return
	 */
	public boolean register(PlayerStored p) {
		if (isAvailable() == false)
			return false;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.villoRegister(this.x, this.z, p.getId(), freemode_height);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			StorageFlatRead.villoRegister(this.x, this.z, p.getId(), freemode_height);
		}
		VillagesHandler.villages.add(new VillageId(this.x, this.z));

		setData();

		return true;
	}

	/**
	 * Ritorno con l'area cuboide
	 * 
	 * @return
	 */
	public VillageCuboid getCuboid() {

		Chunk chunk = new Location(Bukkit.getWorld("clansworld"), this.x, 0, this.z).getChunk();
		Location pos1 = chunk.getBlock(0, 0, 15).getLocation();
		Location pos2 = chunk.getBlock(15, Bukkit.getWorld("clansworld").getMaxHeight() - 1, 0).getLocation();

		VillageCuboid first = new VillageCuboid(pos1, pos2);
		VillageCuboid finale = first.outset(CuboidDirection.Horizontal, VillageSettings.sizeX() / 2);

		return finale;
	}

	/**
	 * 
	 */
	public Structures getStructures() {
		return new Structures(getCuboid());
	}

	/**
	 * Gestisco l'espander
	 * 
	 * @param chunk
	 * @return
	 */
	public Expander getExpander(Chunk chunk) {
		return new Expander(new VillageId(this.x, this.z), chunk);
	}

	/**
	 * Aggiungo un espansione (int) nel database
	 * 
	 * @return
	 */
	public boolean updateQueryExpanded() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.updateExpandedVillo(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.updateExpandedVillo(this.x, this.z);
		}
		return false;
	}

	/**
	 * Numero delle volte che è stata fatta un espansione
	 */
	public int getNumberExpandeded() {
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			return StorageMySQLRead.getExpandedVillo(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			return StorageFlatRead.getExpandedVillo(this.x, this.z);
		}
		return -1;
	}

	/**
	 * Controllo se il villaggio esiste nel file data.yml
	 * 
	 * @return
	 */
	public boolean isExistInData() {
		if (CraftOfClansData.getString("villages." + getIDescaped()) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Creo il villo in data.yml
	 * 
	 * @return
	 */
	public boolean setInData() {
		if (isExistInData()) {
			return false;
		}
		List<String> list = new ArrayList<String>();
		CraftOfClansData.get().set("villages." + getIDescaped() + ".expanded", list);
		CraftOfClansData.get().set("villages." + getIDescaped() + ".structures", list);
		CraftOfClansData.save();
		return true;
	}

	/**
	 * Elimino il villaggio
	 * 
	 * @return
	 */
	public boolean deleteData() {
		if (!isExistInData()) {
			return false;
		}

		CraftOfClansData.get().set("villages." + getIDescaped(), null);
		CraftOfClansData.save();

		// Elimino dal database
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.deleteVillage(this.x, this.z, getOwnerID());
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			StorageFlatRead.deleteVillage(this.x, this.z, getOwnerID());
		}

		VillagesHandler.villages.remove(this);
		return true;
	}

	/**
	 * Ritorno con la lista delle strutture
	 * 
	 * @return
	 */
	public HashMap<StructuresId, String> getStructuresList() {
		
		HashMap<StructuresId, String> hash = new HashMap<>();
		
		for (Entry<StructuresId, VillageCuboid> item : SchematicsHandler.structures_registred.entrySet()) {
			if(item.getKey().getVillage().equals(this)) {
				hash.put(item.getKey(), item.getKey().getType());
			}
		}
		
		return hash;
		
		/*
		if (!isExistInData()) {
			setInData();
		}
		String map = "villages." + getIDescaped() + ".structures";
		if (CraftOfClansData.getString(map) != null && CraftOfClansData.get().getConfigurationSection(map) != null) {
			if (CraftOfClansData.get().getConfigurationSection(map).getKeys(false) != null) {
				for (String key : CraftOfClansData.get().getConfigurationSection(map).getKeys(false)) {
					if (CraftOfClansData.getString(map + "." + key + ".coord") != null) {
						String coordinate = CraftOfClansData.getString(map + "." + key + ".coord");
						String[] finale = coordinate.split("_");
						// Min
						int min_x = Integer.parseInt(finale[0]);
						int min_y = Integer.parseInt(finale[1]);
						int min_z = Integer.parseInt(finale[2]);
						// Max
						int max_x = Integer.parseInt(finale[3]);
						int max_y = Integer.parseInt(finale[4]);
						int max_z = Integer.parseInt(finale[5]);
						// World
						World world = Bukkit.getServer().getWorld("clansworld");
						// Struttura
						Location loc1 = new Location(world, min_x, min_y, min_z);
						Location loc2 = new Location(world, max_x, max_y, max_z);
						if (loc1 != null && loc2 != null) {
							StructuresId finale_s = new StructuresId(new VillageId(x, z), Integer.parseInt(key));
							hash.put(finale_s, finale_s.getType());
						}
					}
				}
			}
		}
		return hash;
		*/
	}

	/**
	 * Lista delle espansioni del villaggio
	 * 
	 * @return
	 */
	public ArrayList<Chunk> getExpanses() {
		ArrayList<Chunk> list = new ArrayList<>();
		World mondo = Bukkit.getWorld("clansworld");
		if (getNumberExpandeded() > 0) {
			List<String> prima = CraftOfClansData.get().getStringList("villages." + getIDescaped() + ".expanded");
			for (String key : prima) {
				String[] coordinate = key.split("_");
				list.add(mondo.getChunkAt(Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1])));
			}
		}
		return list;
	}

	/**
	 * Lista dei guardiani dentro quel villaggio
	 * 
	 * @return
	 */
	public ArrayList<GuardianVillage> getGuardians() {
		ArrayList<GuardianVillage> lista = new ArrayList<>();
		String map = "villages." + getIDescaped() + ".guardian";
		if (CraftOfClansData.getString(map) != null) {
			if (CraftOfClansData.get().getConfigurationSection(map).getKeys(false) != null) {
				for (String key : CraftOfClansData.get().getConfigurationSection(map).getKeys(false)) {
					if (CraftOfClansData.getString(map + "." + key + ".coord") != null) {
						String coordinate = CraftOfClansData.getString(map + "." + key + ".coord");
						String[] finale = coordinate.split("_");

						int x = Integer.parseInt(finale[0]);
						int y = Integer.parseInt(finale[1]);
						int z = Integer.parseInt(finale[2]);

						// World
						World world = Bukkit.getServer().getWorld("clansworld");

						// Struttura
						Location loc1 = new Location(world, x, y, z);
						if (loc1 != null) {
							GuardianVillage guardian = new GuardianVillage(loc1, Integer.parseInt(key));
							lista.add(guardian);
						}
					}
				}
			}
		}
		return lista;
	}

	/**
	 * Teletrasporto un giocatore in questo villaggio
	 * 
	 * @param p
	 * @return
	 */
	public boolean tpAttack(Player p) {
		p.teleport(getRandomTp());
		return true;
	}

	/**
	 * Controllo se il villaggio è sotto attacco
	 * 
	 * @return
	 */
	public boolean isAttacked() {
		for (Entry<UUID, VillageId> id : AttackerManager.attackers.entrySet()) {
			if (id.getValue().getID().equals(new VillageId(x, z).getID())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Ritorno con l'attacco in corso
	 * 
	 * @return
	 */
	public Attack getAttack() {
		for (Entry<UUID, Attack> id : AttackerManager.battles.entrySet()) {
			if (id.getValue().getVillage().getID().equals(new VillageId(x, z).getID())) {
				return id.getValue();
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return getX() + getZ();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof VillageId) {
			if (((VillageId) o).getX() == getX() && ((VillageId) o).getZ() == getZ()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * LVL del minicipio
	 * 
	 * @return
	 */
	public int getLevelTownHall() {
		if (!isExistInData()) {
			setInData();
			return 0;
		}
		String map = "villages." + getIDescaped() + ".structures.1.type";
		if (map != null) {
			if (CraftOfClansData.getString(map).equals("TOWNHALL")) {
				return CraftOfClansData.getInt("villages." + getIDescaped() + ".structures.1.liv");
			}
		}
		return 0;
	}

	/**
	 * Imposto lo scudo al giocatore
	 * 
	 * @param string
	 */
	public void setScudo(String string) {
		int da_aggiungere = 0;
		if (string == "50") {
			da_aggiungere = CraftOfClans.config.getString("attack.shield-duration-if-attack-50") != null ? CraftOfClans.config.getInt("attack.shield-duration-if-attack-50") : 360;
		} else if (string == "85") {
			da_aggiungere = CraftOfClans.config.getString("attack.shield-duration-if-attack-85") != null ? CraftOfClans.config.getInt("attack.shield-duration-if-attack-85") : 720;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, da_aggiungere);

		SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		String formatted = format1.format(calendar.getTime());

		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQLRead.VillosetScudo(this.x, this.z, formatted);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			StorageFlatRead.VillosetScudo(this.x, this.z, formatted);
		}

	}

	/**
	 * Controllo se attualmente c'è uno scudo attivo
	 * 
	 * @return
	 */
	public boolean isActiveScudo() {

		String data = "";

		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			data = StorageMySQLRead.getDataShield(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			data = StorageFlatRead.getDataShield(this.x, this.z);
		}

		if (data == "" || data == null || data == "0000-00-00 00:00:00") {
			return false;
		}

		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {

			Date date = parser.parse(data);
			this.data_scudo = date;

			if (new Date().before(date)) {
				return true;
			} else {
				return false;
			}

		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Imposto l'ultima data di attacco
	 * 
	 * @return
	 */
	public boolean setLastAttack() {
		boolean data = false;
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			data = StorageMySQLRead.VillosetDataAttacco(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			data = StorageFlatRead.VillosetDataAttacco(this.x, this.z);
		}
		return data;
	}

	/**
	 * Prendo l'ultima data di attacco (quante ore sono passate)
	 * 
	 * @return
	 */
	public double getLastAttack() {

		String data = "";
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			data = StorageMySQLRead.getLastVillageAttack(this.x, this.z);
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			data = StorageFlatRead.getLastVillageAttack(this.x, this.z);
		}
		if (data == "" || data == null || data == "0000-00-00 00:00:00") {
			return -1;
		}

		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date;
		try {
			date = parser.parse(data);
		} catch (ParseException e) {
			return -1;
		}

		Date now = new Date();
		long diffInMilliseconds = now.getTime() - date.getTime();
		if (diffInMilliseconds < 0) {
			return -1;
		}

		double diffInHours = diffInMilliseconds / 3600000D;
		return diffInHours;
	}

	public ArrayList<String> info() {

		ArrayList<String> listInfo = new ArrayList<String>();

		listInfo.add(Color.translate(CraftOfClansM.getString("messages.village-info-title")));
		listInfo.add("");

		// ON / OFF
		String info2_on = Color.translate(CraftOfClansM.getString("messages.village-info-shield-on"));
		String info2_off = Color.translate(CraftOfClansM.getString("messages.village-info-shield-off"));
		String info2 = Color.translate(CraftOfClansM.getString("messages.village-info-shield"));

		if (isActiveScudo()) {

			info2 = info2.replace("%1%", info2_on);
			listInfo.add(info2);

			// Durata scudo
			Date now = new Date();
			long diffInMilliseconds = now.getTime() - this.data_scudo.getTime();

			if (diffInMilliseconds < 0) {
				double diffInHours = Math.abs(diffInMilliseconds) / 3600000D;

				// Tempo di durata dello scudo
				String info3 = Color.translate(CraftOfClansM.getString("messages.village-info-shield-date"));

				info3 = info3.replace("%1%", String.format("%.2f", diffInHours));
				listInfo.add(info3);
			}

		} else {
			info2 = info2.replace("%1%", info2_off);
			listInfo.add(info2);
		}

		double last_attack = getLastAttack();

		if (last_attack != -1) {
			String info4 = Color.translate(CraftOfClansM.getString("messages.village-info-last-attack"));
			info4 = info4.replace("%1%", String.format("%.2f", last_attack));
			listInfo.add(info4);
		}
		listInfo.add("");

		return listInfo;
	}

	/**
	 * Distruzione villaggio
	 */
	public void destroy(CommandSender sender) {

		// Resetto le risorse del giocatore
		PlayerStored pstored_village = new PlayerStored(getOwnerID());

		double gems = CraftOfClans.config.getString("newbie-player.gems") != null ? CraftOfClans.config.getDouble("newbie-player.gems") : 0;
		double elixir = CraftOfClans.config.getString("newbie-player.elixir") != null ? CraftOfClans.config.getDouble("newbie-player.elixir") : 0;
		double gold = CraftOfClans.config.getString("newbie-player.gold") != null ? CraftOfClans.config.getDouble("newbie-player.gold") : 0;
		int trophies = CraftOfClans.config.getString("newbie-player.trophies") != null ? CraftOfClans.config.getInt("newbie-player.trophies") : 0;

		pstored_village.setGems(gems);
		pstored_village.setElixir(elixir);
		pstored_village.setGold(gold);
		pstored_village.setTrofei(trophies);
		pstored_village.setElixirNero(0);

		sender.sendMessage(Color.message(pstored_village.getName() + "'s resources resetted"));

		// Strutture
		Iterator<Entry<StructuresId, String>> strutture = getStructuresList().entrySet().iterator();
		while (strutture.hasNext()) {
			Entry<StructuresId, String> id = (Map.Entry<StructuresId, String>) strutture.next();
			id.getKey().destroy();
			strutture.remove();
		}

		// Guardiani
		Iterator<Entry<NPC, VillageId>> guardiani = GuardianNPC.npc_guardian.entrySet().iterator();
		while (guardiani.hasNext()) {
			Entry<NPC, VillageId> id = (Map.Entry<NPC, VillageId>) guardiani.next();
			try {
				if (id.getKey().getOrAddTrait(GuardianNPCTrait.class).stored_guardian.getVillage().equals(this)) {
					id.getKey().destroy();
					guardiani.remove();
				}
			} catch(NullPointerException error) {
				LogHandler.error("Error deleting guardian: " + error.getMessage());
			}
		}

		for (GuardianVillage npc : getGuardians()) {
			npc.delete();
		}

		int indech = 1;
		int numerochunk = getCuboid().getChunks().size();
		// Imposto il pavimento con il pattern
		for (Chunk chunk : getCuboid().getChunks()) {
			ChunkData data = new VillageGeneratorBasic().generateChunkData(chunk.getWorld(), null, chunk.getX(), chunk.getZ(), null);

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < 128; y++) {
						Block block = chunk.getBlock(x, y, z);
						block.setBlockData(data.getBlockData(x, y, z));
					}
				}
			}
			sender.sendMessage(Color.message(indech + " of " + numerochunk + " chunk processing"));
			indech++;
		}

		// Elimino il villaggio
		deleteData();
	}

	/**
	 * Faccio il tp random nel villaggio per gli attacchi
	 * 
	 * @return
	 */
	public Location getRandomTp() {

		Chunk chunk_centrale = getCuboid().getCenter().getChunk();

		Location central_block = chunk_centrale.getBlock(8, 0, 8).getLocation();
		Location loc1 = chunk_centrale.getBlock(0, 0, 0).getLocation();
		Location loc2 = chunk_centrale.getBlock(15, 0, 15).getLocation();
		loc1.setY(VillageSettings.getHeight(this) + 1);
		loc2.setY(VillageSettings.getHeight(this) + 1);
		central_block.setY(VillageSettings.getHeight(this) + 1);

		VillageCuboid cQuboide1 = new VillageCuboid(loc1, loc2);
		VillageCuboid cQuboide = cQuboide1.outset(CuboidDirection.Horizontal, 32);

		List<Block> blocchi_per_tp = Lists.newArrayList(cQuboide.iterator());

		// Cerco un blocco libero nel villaggio
		ArrayList<Block> lista_blocchi_tp = new ArrayList<>();
		for (Block blocco : blocchi_per_tp) {
			if (blocco.getType().equals(Material.AIR)) {
				lista_blocchi_tp.add(blocco);
			}
		}

		int idx = _Number.random_int(0, lista_blocchi_tp.size());
		Location loc = lista_blocchi_tp.get(idx).getLocation();

		// Imposto lo sguardo verso il municipio
		Location lookat = MapInfo.lookAt(loc, central_block);
		loc.setYaw(lookat.getYaw());
		loc.setPitch(lookat.getPitch());

		return loc;
	}

	public int getHeight() {
		if (freemode_height != 0) {
			return freemode_height;
		}
		return -1;
	}
}
