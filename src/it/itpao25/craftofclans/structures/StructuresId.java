package it.itpao25.craftofclans.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.attack.AttackListener;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.NPCSkin;
import it.itpao25.craftofclans.holo.HolographicListner;
import it.itpao25.craftofclans.inventory.InventoryShopToPlayer;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.LookClose;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

public class StructuresId {

	/**
	 * TYPE: TOWNHALL -> MUNICIPIO GOLD_MINE -> Miniera COLLECTOR_ELIXIR ->
	 * Estrattore di Elisir ELIXIR_STORAGE -> Deposito di elisir GOLD_STORAGE ->
	 * Deposito d'oro ARCHER_TOWER -> Torre degli arceri CANNON -> Cannone MORTAR ->
	 * mortaio
	 * 
	 */

	private Location loc;
	private int id;
	private VillageId villo;
	private String type;
	private String coord;
	private Integer level;
	private boolean is_decoration = false;
	private Long cooldown_command;

	// Se disabilitata durante gli attacchi (per il muro)
	public boolean is_disabled = false;

	public StructuresId(Location loc) {
		this.loc = loc;
		ind_id();
		setData();
	}

	public StructuresId(VillageId villo, Integer id) {
		this.id = id;
		this.villo = villo;
		setData();
	}

	private boolean ind_id() {
		this.villo = MapInfo.getVillage(loc);
		for (Entry<StructuresId, VillageCuboid> item : SchematicsHandler.structures_registred.entrySet()) {
			if (item.getValue().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				this.id = item.getKey().getId();
				return true;
			}
		}
		return false;
	}

	public StructuresId setData() {
		
		this.type = CraftOfClansData.getString("villages." + villo.getIDescaped() + ".structures." + id + ".type");
		this.coord = CraftOfClansData.getString("villages." + villo.getIDescaped() + ".structures." + id + ".coord");
		this.level = CraftOfClansData.getInt("villages." + villo.getIDescaped() + ".structures." + id + ".liv");

		// Se è una decorazione
		if (CraftOfClansData.getString("villages." + villo.getIDescaped() + ".structures." + id + ".is_decoration") != null && CraftOfClansData.getString("villages." + villo.getIDescaped() + ".structures." + id + ".is_decoration").equals("1")) {
			this.is_decoration = true;
		}
		
		// Se è disabilitato
		if (CraftOfClansData.getString("villages." + villo.getIDescaped() + ".structures." + id + ".disabled") != null) {
			if (CraftOfClansData.getBoolean("villages." + villo.getIDescaped() + ".structures." + id + ".disabled")) {
				this.is_disabled = true;
			}
		}
		return this;
	}

	private void delete() {
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + id, null);
		CraftOfClansData.save();

		if (SchematicsHandler.structures_registred.containsKey(this)) {
			SchematicsHandler.structures_registred.remove(this);

			// Despawno gli NPC se presenti nella struttura
			if (AttackListener.npc_structures.containsKey(this)) {
				for (NPC npc : AttackListener.npc_structures.get(this)) {
					npc.destroy();
				}
				AttackListener.npc_structures.remove(this);
			}

			// Rimuovo le particelle della struttura se ci sono
			if (SchematicsHandler.structures_particle.containsKey(this)) {
				SchematicsHandler.structures_particle.remove(this);
			}
		}

		// Controllo gli hologrammi (Se attivato HolographicDisplays)
		if (CraftOfClans.isHolographicDisplay) {
			HolographicListner.removeOnStructure(this);
		}
	}

	public String getDataCustom(int level, String index) {
		if (CraftOfClans.config.getString("shop.structures-core." + getType() + ".levels." + level + "." + index) != null) {
			return CraftOfClans.config.getString("shop.structures-core." + getType() + ".levels." + level + "." + index);
		} else {
			return null;
		}
	}

	public String getDataCustomBase(String index) {
		if (CraftOfClans.config.getString("shop.structures-core." + getType() + "." + index) != null && !CraftOfClans.config.getString("shop.structures-core." + getType() + "." + index).equals("")) {
			return CraftOfClans.config.getString("shop.structures-core." + getType() + "." + index);
		} else {
			return null;
		}
	}

	public List<String> getListDataCustom(int level, String index) {
		if (CraftOfClans.config.getString("shop.structures-core." + getType() + ".levels." + level + "." + index) != null) {
			return CraftOfClans.config.get().getStringList("shop.structures-core." + getType() + ".levels." + level + "." + index);
		} else {
			return null;
		}
	}

	public int getId() {
		return this.id;
	}

	public VillageId getVillage() {
		return villo;
	}

	public String getType() {
		return type;
	}

	public String getCoord() {
		return this.coord;
	}

	public double getResources() {
		if (CraftOfClansData.getString("villages." + villo.getIDescaped() + ".structures." + id + ".resources") != null) {
			return CraftOfClansData.getDouble("villages." + villo.getIDescaped() + ".structures." + id + ".resources");
		}
		return -1;
	}

	/**
	 * Imposto il cuboid della struttura
	 * 
	 * @return
	 */
	public VillageCuboid getCuboid() {

		String[] finale = this.coord.split("_");

		// Min
		int min_x = Integer.parseInt(finale[0]);
		int min_y = Integer.parseInt(finale[1]);
		int min_z = Integer.parseInt(finale[2]);
		// Max
		int max_x = Integer.parseInt(finale[3]);
		int max_y = Integer.parseInt(finale[4]);
		int max_z = Integer.parseInt(finale[5]);

		World world = Bukkit.getServer().getWorld("clansworld");
		// Struttura
		Location loc1 = new Location(world, min_x, min_y, min_z);
		Location loc2 = new Location(world, max_x, max_y, max_z);

		return new VillageCuboid(loc1, loc2);
	}

	public Integer getLevel() {
		return this.level;
	}

	public boolean hasLevelUp() {
		int level_up = this.level + 1;
		if (getDataCustom(level_up, "schematics-name") != null) {
			return true;
		}
		return false;
	}

	/**
	 * Ritorno con la capacità della struttura presa dal config.yml
	 * 
	 * @return
	 */
	public double getCapacity() {
		if (CraftOfClans.config.getString("shop.structures-core." + getType() + ".levels." + level + ".capacity") != null) {
			return CraftOfClans.config.getDouble("shop.structures-core." + getType() + ".levels." + level + ".capacity");
		} else {
			return 0;
		}

	}

	/**
	 * Incremento delle risorse per minuto
	 * 
	 * @return
	 */
	public double getIncrementResource() {
		if (getType().equals("GOLD_MINE") || getType().equals("COLLECTOR_ELIXIR") || getType().equals("DARK_ELIXIR_DRILL")) {
			if (CraftOfClans.config.getString("shop.structures-core." + getType() + ".levels." + level + ".increment-forminute") != null) {
				return CraftOfClans.config.getDouble("shop.structures-core." + getType() + ".levels." + level + ".increment-forminute");
			} else {
				return 0;
			}
		} else if (getType().equals("GEMS_COLLECTOR")) {
			if (CraftOfClans.config.getString("shop.structures-core." + getType() + ".levels." + level + ".increment-fordelay") != null) {
				return CraftOfClans.config.getDouble("shop.structures-core." + getType() + ".levels." + level + ".increment-fordelay");
			} else {
				return 0;
			}
		}
		return -1;
	}

	/**
	 * Imposto le risorse della struttura
	 * 
	 * @param resources
	 * @return
	 */
	public boolean setResources(double resources) {
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + id + ".resources", resources);
		if (CraftOfClansData.save()) {
			return true;
		}
		return false;
	}

	public boolean setLevel(int level) {
		this.level = level;
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + id + ".liv", level);
		if (CraftOfClansData.save()) {
			return true;
		}
		return false;
	}

	public String getName() {
		// Struttura normale
		if (CraftOfClansM.getString("structures." + getType()) != null) {
			return CraftOfClansM.getString("structures." + getType());
		}
		// Struttura decorazione
		if (CraftOfClans.config.getString("shop.structures-core." + getType()) != null && CraftOfClans.config.getString("shop.structures-core." + getType() + ".display-name") != null) {
			return CraftOfClans.config.getString("shop.structures-core." + getType() + ".display-name");
		}
		return getType();
	}

	public String getNameNoColor() {
		String strip = getName().toLowerCase();
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', strip));
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasResource() {
		return getResources() >= 0 ? true : false;
	}

	/**
	 * Ritorno con il numero di risorse che devono essere rimosse al giocatore dopo
	 * aver Distrutto la struttura
	 * 
	 * @return
	 */
	public double getResourceAttack() {

		double totale_risorse = 0;
		double numero_strutture = 0;
		double numero_rimuovere = 0;

		if (getType().equals("GOLD_MINE")) {

			totale_risorse = getResources();
			for (Entry<StructuresId, String> id : villo.getStructuresList().entrySet()) {
				if (id.getKey().getType().equals("GOLD_MINE")) {
					numero_strutture++;
				}
			}

		} else if (getType().equals("COLLECTOR_ELIXIR")) {

			totale_risorse = getResources();
			for (Entry<StructuresId, String> id : villo.getStructuresList().entrySet()) {
				if (id.getKey().getType().equals("COLLECTOR_ELIXIR")) {
					numero_strutture++;
				}
			}

		} else if (getType().equals("ELIXIR_STORAGE")) {

			totale_risorse = new PlayerStored(villo.getOwnerID()).getElixir();
			for (Entry<StructuresId, String> id : villo.getStructuresList().entrySet()) {
				if (id.getKey().getType().equals("ELIXIR_STORAGE")) {
					numero_strutture++;
				}
			}

		} else if (getType().equals("GOLD_STORAGE")) {

			totale_risorse = new PlayerStored(villo.getOwnerID()).getGold();
			for (Entry<StructuresId, String> id : villo.getStructuresList().entrySet()) {
				if (id.getKey().getType().equals("GOLD_STORAGE")) {
					numero_strutture++;
				}
			}

		}

		// Calcolo le risorse da rimuovere
		double sommo;
		// Risolvo il problema del java.lang.ArithmeticException
		// @since 0.4
		try {
			sommo = totale_risorse / numero_strutture;
		} catch (java.lang.ArithmeticException exc) {
			sommo = 0;
		}

		if (sommo <= 500) {
			numero_rimuovere = sommo;
		} else if (sommo <= 1000) {
			numero_rimuovere = 500;
		} else {
			numero_rimuovere = sommo / 3;
		}

		return numero_rimuovere;
	}

	@Override
	public int hashCode() {
		return this.id + this.getVillage().getX() + this.getVillage().getZ();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof StructuresId) {
			if (((StructuresId) o).getVillage().getID().equals(this.getVillage().getID()) && ((StructuresId) o).getId() == this.getId()) {
				return true;
			}
		}
		return false;
	}

	private boolean resetBlocks() {
		ArrayList<Block> blocchi = Lists.newArrayList(this.getCuboid().iterator());

		// Rimuovo prima gli item che possono essere droppati
		ArrayList<String> blocchi_droppabili = WorldEditUtil.listaOggettiDroppabili();
		for (Block block : blocchi) {
			if (blocchi_droppabili.contains(block.getType().toString())) {
				block.setType(Material.AIR);
			}
		}

		for (Block block : blocchi) {
			if (!blocchi_droppabili.contains(block.getType().toString())) {
				if (block.getY() == VillageSettings.getHeight(getVillage()) - 1) {
					// Imposto i blocchi dell'espansione
					WorldEditUtil.setBlock(VillageSettings.getMaterialExpanded(), block.getLocation(), block.getLocation());
					continue;
				} else if (block.getY() < VillageSettings.getHeight(getVillage()) - 1 && block.getY() > (VillageSettings.getHeight(getVillage()) - 20)) {
					block.setType(VillageSettings.getMaterialGeneration());
					continue;
				} else if (block.getY() < VillageSettings.getHeight(getVillage()) - 20) {
					continue;
				}
				block.setType(Material.AIR);
			}
		}
		return true;
	}

	/**
	 * Faccio la richiesta per il move della struttura
	 * 
	 * @return
	 */
	public boolean requestToMove(Player p) {

		// Controllo se il giocatore ha spazio nell'inventario per dare il nuovo item
		if (p.getInventory().firstEmpty() == -1) {
			p.sendMessage(Color.message("&cYour inventory is full!"));
			return false;
		}
		this.delete();

		if (this.resetBlocks()) {
			new InventoryShopToPlayer(p, this.getType(), getLevel());
		}

		return false;
	}

	/**
	 * Elimino la struttura
	 * 
	 * @return
	 */
	public boolean destroy() {
		this.delete();
		this.resetBlocks();
		return true;
	}

	/**
	 * Nella struttura, controllo se è contenuto il blocco TAPPETO BIANCO
	 * 
	 * @return Location
	 */
	public Location getSpawnPointFromBlock() {
		List<Block> blocchi = Lists.newArrayList(getCuboid().iterator());
		for (Block blocco : blocchi) {
			if (blocco.getType().equals(Material.WHITE_CARPET)) {
				Location loc = blocco.getLocation();
				loc = loc.add(0.5, 0.5, 0.5);
				return loc;
			}
		}
		return null;
	}

	/**
	 * Conto le risorse nella struttura
	 * 
	 * @return
	 */
	public double getResourcesInside() {
		PlayerStored pstored = new PlayerStored(getVillage().getOwnerID());

		if (getType().equals("ELIXIR_STORAGE")) {
			int conto = 0;
			for (Entry<StructuresId, String> entry : getVillage().getStructuresList().entrySet()) {
				if (entry.getValue().equals("ELIXIR_STORAGE")) {
					conto++;
				}
			}
			if (conto == 1) {
				return pstored.getElixir();
			}
			return pstored.getElixir() / conto;

		} else if (getType().equals("GOLD_STORAGE")) {

			int conto = 0;
			for (Entry<StructuresId, String> entry : getVillage().getStructuresList().entrySet()) {
				if (entry.getValue().equals("GOLD_STORAGE")) {
					conto++;
				}
			}
			if (conto == 1) {
				return pstored.getGold();
			}
			return pstored.getGold() / conto;

		} else if (getType().equals("DARK_ELIXIR_STORAGE")) {

			int conto = 0;
			for (Entry<StructuresId, String> entry : getVillage().getStructuresList().entrySet()) {
				if (entry.getValue().equals("DARK_ELIXIR_STORAGE")) {
					conto++;
				}
			}
			if (conto == 1) {
				return pstored.getElixirNero();
			}
			return pstored.getElixirNero() / conto;
		}

		return -1;
	}

	/**
	 * Se è una struttura di decorazione
	 * 
	 * @return
	 */
	public boolean isDecoration() {
		return this.is_decoration;
	}

	/**
	 * Se ha un comando personalizzato
	 */
	public boolean hasCommandCustom() {
		if (getDataCustomBase("onclick-command") != null) {
			return true;
		}
		return false;
	}

	/**
	 * Eseguo il comando personalizzato della struttura
	 * 
	 * @param p
	 * @return
	 */
	public boolean executeCommandCustom(Player p) {
		if (cooldown_command != null) {
			long coolDownRimasto = (cooldown_command + 3000) - (System.currentTimeMillis());
			if (coolDownRimasto >= 0) {
				return false;
			}
		}
		cooldown_command = System.currentTimeMillis();
		return Bukkit.getServer().dispatchCommand(p, getDataCustomBase("onclick-command"));
	}

	/**
	 * Numero massimo delle strutture dentro un villaggio
	 * 
	 * @return
	 */
	public Integer maxNumber() {

		// Numero massimo generale
		int max_structures = CraftOfClans.config.getString("shop.structures-core." + getType() + ".max-for-eachvillage") != null ? CraftOfClans.config.getInt("shop.structures-core." + getType() + ".max-for-eachvillage") : 0;
		if (max_structures != 0) {
			return max_structures;
		}

		// Se esistono limiti in base al livello del municipio
		if (CraftOfClans.config.getString("shop.structures-core." + getType() + ".limit-based-townhall-level") != null) {
			int villo_lvl = villo.getLevelTownHall();
			for (String key : CraftOfClans.config.get().getConfigurationSection("shop.structures-core." + getType() + ".limit-based-townhall-level").getKeys(false)) {
				if (key.equals(villo_lvl + "")) {
					int limit_townontownhall = Integer.parseInt(CraftOfClans.config.getString("shop.structures-core." + getType() + ".limit-based-townhall-level." + key));
					return limit_townontownhall;
				}
			}
		}
		return 0;
	}

	/**
	 * Se è una struttura che ha gli NPC di difesa
	 * 
	 * @return
	 */
	public boolean spawnNPC() {

		String tipo = getType();

		// Controllo se sono tipologie che hanno gli NPC
		if (!tipo.equals(StructuresEnum.ARCHER_TOWER.toString()) && !tipo.equals(StructuresEnum.WIZARD_TOWER.toString())) {
			return false;
		}

		Location loc = null;
		if (getSpawnPointFromBlock() != null) {
			loc = getSpawnPointFromBlock();
		} else {
			loc = getCuboid().getFace(CuboidDirection.Up).getCenter().getBlock().getLocation().add(+0.5, +0.5, +0.5);
		}

		String nome_display = Color.translate(CraftOfClansM.getString("structures." + tipo.toString()));

		if (tipo.equals(StructuresEnum.ARCHER_TOWER.toString())) {

			NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, nome_display);

			npc.getOrAddTrait(LookClose.class).lookClose(true);
			npc.getOrAddTrait(LookClose.class).setRealisticLooking(true);
			npc.getOrAddTrait(LookClose.class).setRange(Integer.parseInt(getDataCustom(getLevel(), "damage-range")));

			// Imposto la skin se c'è nelle impostazioni
			NPCSkin.getSkinFromConfig(npc, tipo);

			npc.spawn(loc);
			npc.setProtected(true);
			npc.data().set("originale", loc);

			ItemStack hat = new ItemStack(Material.IRON_HELMET);
			ItemStack bow = new ItemStack(Material.BOW);
			Equipment equipTrait = npc.getOrAddTrait(Equipment.class);
			equipTrait.set(EquipmentSlot.HELMET, hat);
			equipTrait.set(EquipmentSlot.HAND, bow);

			ArrayList<NPC> npc_s = new ArrayList<>();
			npc_s.add(npc);

			AttackListener.npc_structures.put(this, npc_s);

		} else if (tipo.equals(StructuresEnum.WIZARD_TOWER.toString())) {

			NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, nome_display);

			npc.getOrAddTrait(LookClose.class).lookClose(true);
			npc.getOrAddTrait(LookClose.class).setRealisticLooking(true);
			npc.getOrAddTrait(LookClose.class).setRange(Integer.parseInt(getDataCustom(getLevel(), "damage-range")));

			// Imposto la skin se c'è nelle impostazioni
			NPCSkin.getSkinFromConfig(npc, tipo);

			npc.spawn(loc);
			npc.setProtected(true);
			npc.data().set("originale", loc);

			ItemStack fuoco = new ItemStack(Material.BLAZE_POWDER);

			npc.addTrait(Equipment.class);
			Equipment equipTrait = npc.getOrAddTrait(Equipment.class);
			equipTrait.set(EquipmentSlot.HAND, fuoco);

			ArrayList<NPC> npc_s = new ArrayList<>();
			npc_s.add(npc);

			AttackListener.npc_structures.put(this, npc_s);
		}

		return true;
	}

	/**
	 * Rimuovo gli NPC della struttura
	 * 
	 * @return
	 */
	public boolean despawnNPC() {

		Iterator<Entry<StructuresId, ArrayList<NPC>>> it = AttackListener.npc_structures.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<StructuresId, ArrayList<NPC>> id = (Map.Entry<StructuresId, ArrayList<NPC>>) it.next();
			if (id.getKey().equals(this)) {
				for (NPC npc : id.getValue()) {
					npc.destroy();
				}
				it.remove();
			}
		}

		return true;
	}

	/**
	 * Spawno le particelle vicino alle strutture
	 * 
	 * @return
	 */
	public boolean spawnParticle() {

		String tipo = getType();

		if (tipo.equals(StructuresEnum.BOMB.toString())) {
			
			Location loc = getCuboid().getFace(CuboidDirection.Up).getCenter().getBlock().getLocation().add(+0.5, 0, +0.5);
			loc.setY(VillageSettings.getHeight(getVillage()) + 0.3);

			HashMap<String, Object> options_particle = new HashMap<String, Object>();
			options_particle.put("location", loc);
			options_particle.put("particle", Particle.REDSTONE);
			options_particle.put("particle_data", new Particle.DustOptions(org.bukkit.Color.RED, 1));

			SchematicsHandler.structures_particle.put(this, options_particle);

		} else if (tipo.equals(StructuresEnum.SKELETON_TRAP.toString())) {

			Location loc = getCuboid().getFace(CuboidDirection.Up).getCenter().getBlock().getLocation().add(+0.5, 0, -0.5);
			loc.setY(VillageSettings.getHeight(getVillage()) + 0.4);

			HashMap<String, Object> options_particle = new HashMap<String, Object>();
			options_particle.put("location", loc);
			options_particle.put("particle", Particle.SMOKE_LARGE);

			SchematicsHandler.structures_particle.put(this, options_particle);
		}

		return false;
	}
	
	public String getSchematic() {
		return getDataCustom(this.level, "schematics-name");
	}
	
	/**
	 * Imposto la struttura se disabilitata o meno
	 * 
	 * @param toggle
	 * @return
	 */
	public boolean setDisabled(boolean toggle) {
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + id + ".disabled", toggle);
		CraftOfClansData.save();
		
		this.is_disabled = toggle;
		return true;
	}
	
	/**
	 * Disabilito la struttura durante gli attacchi
	 */
	public void disableOnAttack() {
		setDisabled(true);
		resetBlocks();
	}
	
	public void regenSchematic() {
		Location loc = getCuboid().corners()[0].getLocation();
		loc.setY(VillageSettings.getHeight(getVillage()));
		
		WorldEditUtil.replacecopy(loc, getSchematic());
	}
}
