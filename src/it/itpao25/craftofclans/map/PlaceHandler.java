package it.itpao25.craftofclans.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.StructuresBuild;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.GuardianEnum;
import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.guardian.GuardianVillage;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.SchematicsSize;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.worldmanager.Sounds;

public class PlaceHandler {

	private ItemStack item;
	private PlayerStored p;
	private boolean response;
	private Location loc;
	private StructuresEnum struttura;
	private Location min;
	private Location max;
	private Integer level;
	private boolean is_decoration = false;
	public boolean is_cancelled = false;

	public PlaceHandler(ItemStack item, PlayerStored p, Location location) {
		this.item = item;
		this.p = p;
		this.loc = location;
		this.response = getType();
	}

	/**
	 * Controllo se è presente nel config il titolo dell'item
	 * 
	 * @return
	 */
	public boolean isPresent() {
		List<String> list_name = new ArrayList<String>();

		for (String string : CraftOfClans.config.get().getConfigurationSection("shop.structures-gui").getKeys(false)) {
			if (CraftOfClans.config.getString("shop.structures-gui." + string + ".title") != null) {
				String name = Color.translate(CraftOfClans.config.getString("shop.structures-gui." + string + ".title"));
				list_name.add(name);
			}
		}
		if (this.item != null) {
			if (this.item.getItemMeta() != null && this.item.getItemMeta().getDisplayName() != null) {
				if (list_name.contains(Color.translate(this.item.getItemMeta().getDisplayName()))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Prendo il livello dal lore dall'item
	 * 
	 */
	private void setLevelFromLore() {
		List<String> lores = item.getItemMeta().getLore();
		String code = lores.get(0);
		code = ChatColor.stripColor(code).replace("Level: ", "");
		if (_Number.isNumero(code)) {
			this.level = Integer.parseInt(code);
		} else {
			LogHandler.error("setLevelFromLore() is not number!");
		}
	}

	/**
	 * Prendo l'owner dal lore dall'item
	 */
	public static String setOwnerFromLore(ItemStack item) {
		String owner = null;
		List<String> lores = item.getItemMeta().getLore();
		if (lores == null)
			return owner;
		String code = lores.get(1);
		if (!code.contains("@Village: ")) {
			return null;
		}
		code = ChatColor.stripColor(code).replace("@Village: ", "");
		return code;
	}

	private boolean getType() {

		if (isPresent() == false) {
			return false;
		}
		setLevelFromLore();

		if (!loc.getWorld().getName().equals("clansworld")) {
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.build-worldnotcorrect")));
			return false;
		}

		// Controllo se l'owner è quello giusto
		String playername_village = setOwnerFromLore(item);
		if (playername_village != null) {
			String pstore = p.getName();
			if (!pstore.equals(playername_village)) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.place-not-allowed-own")));
				return false;
			}
		}

		Location nuova = new Location(loc.getWorld(), loc.getX(), VillageSettings.getHeight(p.getVillage()), loc.getZ());

		VillageId map = MapInfo.getVillage(loc);
		if (!map.isOwner(p)) {
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.build-nothis")));
			return false;
		}

		// Controllo se il chunk è claimato o meno
		if (!ExpanderRegister.isPresent(loc.getChunk())) {
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.build-notexpanded")));
			return false;
		}

		Player player = p.get();

		String config = null;
		String key_schem = null;

		for (String nome : CraftOfClans.config.get().getConfigurationSection("shop.structures-gui").getKeys(false)) {
			if (CraftOfClans.config.getString("shop.structures-gui." + nome + ".title") != null) {
				String corrent = Color.translate(CraftOfClans.config.getString("shop.structures-gui." + nome + ".title"));

				if (this.item.getItemMeta().getDisplayName().equals(corrent)) {
					// Se è una struttura
					for (StructuresEnum struem : StructuresEnum.values()) {
						if (struem.name().equalsIgnoreCase(nome)) {
							this.struttura = StructuresEnum.valueOf(nome);
							key_schem = struem.toString();
						}
					}

					// Se è un Guardiano
					for (GuardianEnum guard_enum : GuardianEnum.values()) {
						if (guard_enum.name().equalsIgnoreCase(nome)) {

							// Se può piazzare il guardiano in base ai limiti
							StructuresBuild event = new StructuresBuild(struttura, map, 1, player, nuova);
							Bukkit.getServer().getPluginManager().callEvent(event);
							if (event.isCancelled()) {
								return false;
							}

							Location loc = this.loc.clone();
							loc.setY(VillageSettings.getHeight(p.getVillage()) + 1);
							loc.add(+0.5, 0, +0.5);

							if (!SchematicsHandler.hasStructures(loc)) {
								GuardianVillage guardiano = GuardianNPC.generateNPC(loc, this.level);
								guardiano.spawn();
								return true;
							} else {
								this.is_cancelled = true;
								p.sendMessage(Color.message(CraftOfClansM.getString("messages.guardian-not-allowd-here")));
							}
							return false;
						}
					}

					// Come ultimo caso prendo le decorazioni
					if (this.struttura == null) {
						this.struttura = StructuresEnum.DECORATION.setType(nome);
						this.is_decoration = true;
					}

					// Imposto il nome della struttura
					config = CraftOfClans.config.getString("shop.structures-core." + nome + ".levels." + this.level + ".schematics-name");
				}
			}
		}

		// Limiti sul chunk/sul numero di strutture
		StructuresBuild event = new StructuresBuild(struttura, map, this.level, player, nuova);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}

		int x = 0, y = 0, z = 0;
		
		SchematicsSize size = new SchematicsSize(config);
		x = size.getWidth();
		y = size.getHeight();
		z = size.getLenght();

		Location loc1 = loc.clone();
		loc1.setY(VillageSettings.getHeight(p.getVillage()) - 1);
		
		Location loc2;
		
		// Se è il muro da costruire
		if(key_schem != null && (key_schem.equals(StructuresEnum.VILLAGE_WALL.toString()) || 
				key_schem.equals(StructuresEnum.VILLAGE_GATE_WALL.toString()))) {
			loc2 = loc.clone();
			loc2.setY(VillageSettings.getHeight(p.getVillage()));
		} else {
			loc2 = loc.clone().add(+(x + 1), +y, +(z + 1));
		}
		
		this.min = loc1;
		this.max = loc2;

		if (!loc1.getChunk().equals(loc2.getChunk())) {
			return false;
		}

		// Controllo se si può posizionare la struttura
		VillageCuboid cuboid = new VillageCuboid(loc1, loc2);

		Iterator<Block> blocks = cuboid.iterator();
		List<Block> list_block = Lists.newArrayList(blocks);

		for (Block blocco : list_block) {
			// Controllo se ci sono delle strutture in quella zona
			if (SchematicsHandler.hasStructures(blocco.getLocation())) {
				return false;
			}

			// Controllo se è tutta aria
			if (blocco.getY() >= VillageSettings.getHeight(p.getVillage())) {
				if (!blocco.getLocation().getBlock().getType().equals(Material.AIR)) {
					return false;
				}
			}
		}
		
		Location pasteLoc;
		if(key_schem != null && (key_schem.equals(StructuresEnum.VILLAGE_WALL.toString()) || 
				key_schem.equals(StructuresEnum.VILLAGE_GATE_WALL.toString()))) {
			pasteLoc = nuova;
		} else {
			pasteLoc = nuova.add(1, 0, 1);
		}
		
		if (WorldEditUtil.replacecopy(pasteLoc, config)) {
			setInData();
			nuova.getWorld().playSound(pasteLoc, Sounds.ANVIL_USE.bukkitSound(), 1, 1);
		}
		return true;
	}

	/**
	 * Ritorno con la risposta
	 * 
	 * @return
	 */
	public boolean getResponse() {
		return this.response;
	}

	public boolean setInData() {
		
		VillageId villo = MapInfo.getVillage(loc);
		if (!villo.isExistInData()) {
			return false;
		}
		
		if (CraftOfClansData.get().getConfigurationSection("villages." + villo.getIDescaped() + ".structures") == null) {
			CraftOfClansData.get().createSection("villages." + villo.getIDescaped() + ".structures");
		}

		int conto = SchematicsHandler.getIndexStructures(villo);

		String tipo;
		if (!struttura.toString().equals(StructuresEnum.DECORATION.toString())) {
			tipo = struttura.toString();
		} else {
			// Decorazione
			tipo = struttura.type_struttura;
		}

		if (tipo == null) {
			return false;
		}

		World world = Bukkit.getServer().getWorld("clansworld");
		Location loc1 = new Location(world, min.getBlockX(), min.getBlockY(), min.getBlockZ());
		Location loc2 = new Location(world, max.getBlockX(), max.getBlockY(), max.getBlockZ());
		VillageCuboid finale = new VillageCuboid(loc1, loc2);

		CraftOfClansData.get().createSection("villages." + villo.getIDescaped() + ".structures." + conto);
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + conto + ".type", tipo);
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + conto + ".coord", finale.getLowerX() + "_" + finale.getLowerY() + "_" + finale.getLowerZ() + "_" + finale.getUpperX() + "_" + finale.getUpperY() + "_" + finale.getUpperZ());
		CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + conto + ".liv", this.level);

		if (struttura == StructuresEnum.COLLECTOR_ELIXIR || struttura == StructuresEnum.GOLD_MINE || struttura == StructuresEnum.GEMS_COLLECTOR || struttura == StructuresEnum.DARK_ELIXIR_DRILL) {
			CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + conto + ".resources", 0);
		}
		if (this.is_decoration) {
			CraftOfClansData.get().set("villages." + villo.getIDescaped() + ".structures." + conto + ".is_decoration", 1);
		}
		
		if (CraftOfClansData.save()) {
			StructuresId structures = new StructuresId(villo, Integer.valueOf(conto));

			// Se devo spawnare gli NPC
			structures.spawnNPC();
			// Se la struttura ha delle particelle decorative
			structures.spawnParticle();

			SchematicsHandler.structures_registred.put(structures, finale);
			return true;
		}

		return true;
	}
}
