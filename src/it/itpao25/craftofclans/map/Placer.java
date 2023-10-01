package it.itpao25.craftofclans.map;

import java.util.ArrayList;
import java.util.List;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.guardian.GuardianEnum;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.SchematicsSize;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageCuboid;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.collect.Lists;

public class Placer {

	public boolean runTask() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(CraftOfClans.getInstance(), new PlacerListener(), 0L, 20L);
		return false;
	}

	/**
	 * Registro l'item che viene tenuto in mano
	 */
	public static boolean placeListener(ItemStack item, Player p, Location loc) {
		
		String schem = null;
		String key_schem = null;
		
		if (item == null) {
			return false;
		}

		String itemname = item.getItemMeta().getDisplayName();
		int level = setLevelFromLore(item);

		for (String nome : CraftOfClans.config.get().getConfigurationSection("shop.structures-gui").getKeys(false)) {
			if (CraftOfClans.config.getString("shop.structures-gui." + nome + ".title") != null) {
				String corrent = Color.translate(CraftOfClans.config.getString("shop.structures-gui." + nome + ".title"));
				if (itemname.equals(corrent)) {
					key_schem = nome;
					
					// Se è una struttura
					for (StructuresEnum struem : StructuresEnum.values()) {
						if (struem.name().equalsIgnoreCase(nome)) {
							schem = "shop.structures-core." + nome + ".levels." + level + ".schematics-name";
						}
					}

					// Se è un Guardiano
					for (GuardianEnum guard_enum : GuardianEnum.values()) {
						if (guard_enum.name().equalsIgnoreCase(nome)) {
							schem = "GUARDIAN";
						}
					}

					// Come ultimo caso prendo le decorazioni
					if (schem == null) {
						schem = "shop.structures-core." + nome + ".levels." + level + ".schematics-name";
					}
				}
			}
		}
		if (schem == null || schem == "") {
			return false;
		}
		
		int x = 0, z = 0;
		
		if (!schem.equals(StructuresEnum.GUARDIAN.toString()) && 
				(key_schem != null && !key_schem.equals(StructuresEnum.VILLAGE_WALL.toString()) && !key_schem.equals(StructuresEnum.VILLAGE_GATE_WALL.toString()))) {
			
			String config = CraftOfClans.config.getString(schem);
			SchematicsSize size = new SchematicsSize(config);
			x = size.getWidth() + 1;
			z = size.getLenght() + 1;

		} else {
			x = 1;
			z = 1;
		}

		Location nuova1 = loc.clone().add(0, +1, 0);
		
		Location nuova2;
		if (!schem.equals(StructuresEnum.GUARDIAN.toString()) && 
				(key_schem != null && !key_schem.equals(StructuresEnum.VILLAGE_WALL.toString()) && !key_schem.equals(StructuresEnum.VILLAGE_GATE_WALL.toString()))) {
			nuova2 = loc.clone().add(+x, 1, +z);
		} else {
			nuova2 = loc.clone().add(0, 1, 0);
		}

		VillageCuboid cuboide = new VillageCuboid(nuova1, nuova2);
		List<Block> lista = Lists.newArrayList(cuboide.iterator());

		int liberi = 0;
		for (Block blocco : lista) {
			if (nuova1.getChunk().equals(nuova2.getChunk()) && ExpanderRegister.isPresent(nuova1.getChunk())) {
				if (SchematicsHandler.hasStructures(blocco.getLocation())) {
					continue;
				}
				if (blocco.getLocation().getBlock().getType().equals(Material.AIR)) {
					liberi++;
				}
			}
		}
		
		ArrayList<Location> locations = new ArrayList<>();
		if(PlacerListener.blocks.containsKey(p)) {
			PlacerListener.blocks.remove(p);
		}
		
		for (Block blockko : lista) {
			if (liberi == cuboide.volume()) {
				locations.add(blockko.getLocation());
				p.sendBlockChange(blockko.getLocation(), Material.LIME_CARPET.createBlockData());
			} else {
				if (blockko.getLocation().getBlock().getType().equals(Material.AIR)) {
					locations.add(blockko.getLocation());
					p.sendBlockChange(blockko.getLocation(), Material.RED_CARPET.createBlockData());
				}
			}
		}
		PlacerListener.blocks.put(p, locations);
		
		return true;
	}

	/**
	 * Prendo il livello dal lore dall'item
	 */
	private static int setLevelFromLore(ItemStack item) {
		int level = 1;
		List<String> lores = item.getItemMeta().getLore();
		if (lores == null)
			return level;
		String code = lores.get(0);
		code = ChatColor.stripColor(code).replace("Level: ", "");
		if (_Number.isNumero(code)) {
			level = Integer.parseInt(code);
		}
		return level;
	}
}
