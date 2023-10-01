package it.itpao25.craftofclans.guardian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.inventory.InventoryEnum;
import it.itpao25.craftofclans.inventory.InventoryHandler;
import it.itpao25.craftofclans.inventory.InventoryShopToPlayer;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.structures.StructuresInfoSetting;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.LookClose;

public class GuardianVillage {

	private String coord;
	private Location loc;
	private int id;
	private int level;
	private VillageId villo;
	private NPC npc;
	public boolean is_first = false;

	public GuardianVillage(Location loc, Integer id) {
		this.loc = loc;
		this.id = id;
		ind_npc();
		setData();
	}

	public GuardianVillage(VillageId villo, Integer id) {
		this.id = id;
		this.villo = villo;
		setData();
		init_location();
	}
	
	private boolean ind_npc() {

		this.villo = MapInfo.getVillage(loc);
		Location tofind = loc.clone();
		tofind.setY(VillageSettings.getHeight(villo) + 1);
		
		for (Entry<NPC, VillageId> item : GuardianNPC.npc_guardian.entrySet()) {
			if (item.getKey().isSpawned()) {
				Location locnpc = item.getKey().getStoredLocation();
				if (locnpc.getX() == tofind.getX() && locnpc.getZ() == tofind.getZ() && locnpc.getY() == tofind.getY()) {
					this.npc = item.getKey();
					this.id = item.getKey().getOrAddTrait(GuardianNPCTrait.class).stored_guardian.id;
					return true;
				}
			}
		}
		return false;
	}

	private boolean init_location() {

		String[] finale = this.coord.split("_");

		int x = Integer.parseInt(finale[0]);
		int y = Integer.parseInt(finale[1]);
		int z = Integer.parseInt(finale[2]);

		this.loc = new Location(Bukkit.getWorld("clansworld"), x, y, z);

		return true;
	}

	private boolean setData() {
		this.coord = CraftOfClansData.getString("villages." + villo.getIDescaped() + ".guardian." + id + ".coord");
		this.level = CraftOfClansData.getInt("villages." + villo.getIDescaped() + ".guardian." + id + ".liv");

		// Solo se � il primo NPC cliccando devo aprire la GUI
		if (CraftOfClansData.getString("villages." + villo.getIDescaped() + ".guardian." + id + ".is_first") != null) {
			this.is_first = true;
		}
		return true;
	}

	public boolean setLevel(int level) {
		this.level = level;
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".guardian." + id + ".liv", level);
		if (CraftOfClansData.save()) {
			return true;
		}
		return false;
	}

	/**
	 * Spawno l'NPC
	 * 
	 * @return
	 */
	public boolean spawn() {

		String nomenpc = Color.translate(CraftOfClansM.getString("structures.GUARDIAN"));

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, nomenpc);
		
		npc.getOrAddTrait(GuardianNPCTrait.class).setGuardianVillage(this);
		
		npc.getOrAddTrait(LookClose.class).lookClose(true);
		npc.getOrAddTrait(LookClose.class).setRealisticLooking(true);
		npc.getOrAddTrait(LookClose.class).setRange((double) 5.0);
		
		this.npc = npc;
		setEquip();
		
		// Imposto la skin se c'� nelle impostazioni
		NPCSkin.getSkinFromConfig(npc, "GUARDIAN");

		this.npc.spawn(this.loc);
		this.npc.setProtected(true);

		GuardianNPC.npc_guardian.put(this.npc, villo);
		return true;
	}

	/**
	 * Imposto l'equipaggiamento
	 * 
	 * @return
	 */
	public boolean setEquip() {

		// Piedi
		String boots = getDataCustom(this.level, "equipment.boots");
		
		if (boots != null) {
			ItemStack stick = new ItemStack(Material.getMaterial(boots));
			this.npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.BOOTS, stick);
		}

		// Petto
		String chestplate = getDataCustom(this.level, "equipment.chestplate");
		if (chestplate != null) {
			ItemStack stick = new ItemStack(Material.getMaterial(chestplate));
			this.npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, stick);
		}

		String helmet = getDataCustom(this.level, "equipment.helmet");
		if (helmet != null) {
			ItemStack stick = new ItemStack(Material.getMaterial(helmet));
			this.npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HELMET, stick);
		}
		
		String leggings = getDataCustom(this.level, "equipment.leggings");
		if (leggings != null) {
			ItemStack stick = new ItemStack(Material.getMaterial(leggings));
			this.npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, stick);
		}
		
		String hand = getDataCustom(this.level, "equipment.hand");
		if (hand != null) {
			ItemStack stick = new ItemStack(Material.getMaterial(hand));
			this.npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND, stick);
		}
		return true;
	}

	public VillageId getVillage() {
		return this.villo;
	}

	public int getId() {
		return this.id;
	}

	public String getCoord() {
		return this.coord;
	}

	public Integer getLevel() {
		return this.level;
	}

	public NPC getNPC() {
		return this.npc;
	}
	
	public NPC getEntryNPC() {
		return this.npc;
	}

	public NPC setNPC(NPC npc) {
		this.npc = npc;
		return this.getNPC();
	}

	@Override
	public int hashCode() {
		return this.id + this.villo.getX() + this.villo.getZ();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o == GuardianVillage.class) {
			if (((GuardianVillage) o).getVillage().getID().equals(this.villo.getID()) && ((GuardianVillage) o).getId() == this.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Quando clicchi sopra un'NPC
	 * 
	 * @param p
	 * @return
	 */
	public boolean openGUI(Player p) {
		
		int size = CraftOfClans.config.get().getInt("guardian-gui.size");
		InventoryHandler inv = new InventoryHandler(p, InventoryEnum.Guardian_GUI, size);
		HashMap<ItemStack, Integer> items = new HashMap<>();

		int slot_fall = 0;
		for (String string : CraftOfClans.config.get().getConfigurationSection("guardian-gui").getKeys(false)) {
			if (CraftOfClans.config.getString("guardian-gui." + string + ".title") != null) {

				// Controllo se � attivo
				if (CraftOfClans.config.getString("guardian-gui." + string + ".active") != null) {
					if (CraftOfClans.config.getBoolean("guardian-gui." + string + ".active") == false) {
						continue;
					}
				}

				String tipo = CraftOfClans.config.getString("guardian-gui." + string + ".type");
				if (tipo.equals("INFO_TOWNHALL") && !is_first) {
					continue;
				}

				// Il primo guardiano non pu� essere spostato
				if (tipo.equals("MOVE") && is_first) {
					continue;
				}

				int slot = CraftOfClans.config.getString("guardian-gui." + string + ".slot") != null ? CraftOfClans.config.getInt("guardian-gui." + string + ".slot") : slot_fall;
				String material = CraftOfClans.config.getString("guardian-gui." + string + ".material");
				List<String> lore = CraftOfClans.config.get().getStringList("guardian-gui." + string + ".lore");

				ItemStack item_corrent = new ItemStack(Material.getMaterial(material));
				ItemMeta item_corrent_meta = item_corrent.getItemMeta();
				item_corrent_meta.setDisplayName(Color.translate(CraftOfClans.config.getString("guardian-gui." + string + ".title")));
				
				for (int c = 0; c <= lore.size() - 1; c++) {
					String current = lore.get(c);

					current = current.replace("[level]", this.getLevel() + "");
					current = current.replace("[next_level]", this.getLevel() + 1 + "");

					if (current.contains("[cost_next]")) {
						current = current.replace("[cost_next]", "");
						if (new StructuresInfoSetting("GUARDIAN", this.getLevel() + 1).costo() != null) {
							ArrayList<String> costo = new StructuresInfoSetting("GUARDIAN", this.getLevel() + 1).costo();
							for (String costo_str : costo) {
								lore.add(Color.translate(costo_str));
							}
						} else {
							lore.remove(c);
						}
					}
					if (current.contains("[level_townhall]")) {
						current = current.replace("[level_townhall]", this.villo.getLevelTownHall() + "");
					}
					lore.set(c, Color.translate(current));
				}

				item_corrent_meta.setLore(lore);

				item_corrent.setItemMeta(item_corrent_meta);
				items.put(item_corrent, slot);
				slot_fall++;
			}
		}

		for (Entry<ItemStack, Integer> item : items.entrySet()) {
			inv.setItem(item.getValue(), item.getKey());
		}

		// Apro l'inventario
		inv.openInventory();
		return inv.openInventory();
	}

	/**
	 * Elimino il guardiano dal villaggio
	 */
	public void delete() {
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".guardian." + id, null);
		CraftOfClansData.save();
		if (GuardianNPC.npc_guardian.containsKey(this.getNPC())) {
			GuardianNPC.npc_guardian.remove(this.getNPC());
		}
	}

	/**
	 * Faccio la richiesta per il move del guardiano
	 * 
	 * @return
	 */
	public boolean requestToMove(Player p) {

		// Controllo se � il primo guardiano
		if (is_first) {
			p.sendMessage(Color.message("&cYou can't move a first Guardian!"));
			return false;
		}

		// Controllo se il giocatore ha spazio nell'inventario per dare il nuovo item
		if (p.getInventory().firstEmpty() == -1) {
			p.sendMessage(Color.message("&cYour inventory is full!"));
			return false;
		}
		this.getNPC().destroy();
		this.delete();

		new InventoryShopToPlayer(p, "GUARDIAN", getLevel());

		return true;
	}

	public String getDataCustom(int level, String index) {
		index = index == "" ? "" : "." + index;
		if (CraftOfClans.config.getString("shop.structures-core.GUARDIAN.levels." + level + "" + index) != null) {
			return CraftOfClans.config.getString("shop.structures-core.GUARDIAN.levels." + level + "" + index);
		} else {
			return null;
		}
	}

	/**
	 * Controllo se ha un livello di aggiornamento
	 * 
	 * @return
	 */
	public boolean hasLevelUp() {
		int level_up = this.level + 1;
		if (getDataCustom(level_up, "") != null) {
			return true;
		}
		return false;
	}
}
