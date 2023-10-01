package it.itpao25.craftofclans.structures;

import java.util.ArrayList;
import java.util.Date;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.guardian.GuardianVillage;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.ExpandBorder;
import it.itpao25.craftofclans.map.Expander;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.world.block.BlockTypes;

@SuppressWarnings("deprecation")
public class Municipio implements Runnable {

	private VillageCuboid cuboid;
	private PlayerStored p;
	public VillageId id;
	private StructuresId structure;

	// Se è la prima volta che viene generato il villaggio
	private boolean isPrima = false;
	private long time_start;

	public Municipio(VillageCuboid cuboid) {
		this.cuboid = cuboid;
	}

	public Municipio(VillageCuboid cuboid, PlayerStored p, boolean isPrima) {
		this.isPrima = isPrima;
		this.cuboid = cuboid;
		this.p = p;
	}

	public Municipio(VillageCuboid cuboid, PlayerStored p, VillageId id, boolean isPrima, long time_start) {
		this.isPrima = isPrima;
		this.cuboid = cuboid;
		this.p = p;
		this.id = id;
		this.time_start = time_start;
	}

	public boolean create() {

		Location center = cuboid.getCenter();
		Chunk in_mezzo = center.getChunk();

		// Se è abiliata la modalità freemode
		Location x = in_mezzo.getBlock(0, VillageSettings.getHeight(id) - 1, 0).getLocation();

		registerChunk(center);
		registerStructures(center.getChunk());

		String config = CraftOfClans.config.getString("shop.structures-core.TOWNHALL.levels.1.schematics-name");
		WorldEditUtil.replacecopy(x, config);

		long end_time = new Date().getTime();
		long difference = end_time - this.time_start;
		p.sendMessage(Color.message(CraftOfClansM.getString("messages.village-created-success").replace("%1%", difference + " ms")));

		if (p != null && id != null) {
			id.tp(p.get());
		}
		LogHandler.log("New village successfully registered!");
		return true;
	}
	
	/**
	 * Registro il primo chunk, in modo da permettere di non expandere questo
	 * 
	 * @param loc
	 * @return
	 */
	public boolean registerChunk(Location loc) {
		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		Expander expander = new Expander(this.id, chunk);
		return expander.register(p, this.isPrima);
	}
	
	/**
	 * Registro la struttura
	 */
	public boolean registerStructures(Chunk chunk) {
		
		Location min = chunk.getBlock(0, VillageSettings.getHeight(id) - 5, 0).getLocation();
		Location max = chunk.getBlock(15, VillageSettings.getHeight(id) + 15, 15).getLocation();
		int conto = SchematicsHandler.getIndexStructures(id);
		VillageCuboid cuboid = new VillageCuboid(min, max);
		
		CraftOfClansData.get().createSection("villages." + id.getIDescaped() + ".structures." + conto);
		CraftOfClansData.get().set("villages." + id.getIDescaped() + ".structures." + conto + ".type", "TOWNHALL");
		CraftOfClansData.get().set("villages." + id.getIDescaped() + ".structures." + conto + ".coord", min.getBlockX() + "_" + min.getBlockY() + "_" + min.getBlockZ() + "_" + max.getBlockX() + "_" + max.getBlockY() + "_" + max.getBlockZ());
		CraftOfClansData.get().set("villages." + id.getIDescaped() + ".structures." + conto + ".liv", 1);

		StructuresId structures = new StructuresId(id, conto);
		this.structure = structures;

		if (CraftOfClansData.save()) {
			SchematicsHandler.structures_registred.put(structures, cuboid);
			return true;
		}
		return false;
	}

	/**
	 * Genero i bordi del villaggio
	 * 
	 * @return
	 */
	public boolean generateBordi() {

		String wall_schem = CraftOfClans.config.getString("village.wall-schematic");

		int height_map = VillageSettings.getHeight(id);

		ArrayList<Chunk> angoli = new ArrayList<>();

		Chunk chunk_corner1 = cuboid.corners()[2].getChunk();
		angoli.add(chunk_corner1);

		Chunk chunk_corner2 = cuboid.corners()[1].getChunk();
		angoli.add(chunk_corner2);

		Chunk chunk_corner3 = cuboid.corners()[4].getChunk();
		angoli.add(chunk_corner3);

		Chunk chunk_corner4 = cuboid.corners()[5].getChunk();
		angoli.add(chunk_corner4);

		VillageCuboid latoSud = cuboid.getFace(CuboidDirection.South);

		for (Chunk chunk : latoSud.getChunks()) {

			// Espando il chunk
			ExpandBorder expander = new ExpandBorder(id, chunk);
			expander.register();

			// Se è un angolo
			if (angoli.contains(chunk)) {
				continue;
			}

			Location loc1 = chunk.getBlock(15, height_map, 0).getLocation();
			WorldEditUtil.replacecopy(loc1, wall_schem, 270);
		}

		VillageCuboid latoNord = cuboid.getFace(CuboidDirection.North);
		for (Chunk chunk : latoNord.getChunks()) {

			// Espando il chunk
			ExpandBorder expander = new ExpandBorder(id, chunk);
			expander.register();

			if (angoli.contains(chunk)) {
				continue;
			}

			Location loc1 = chunk.getBlock(0, height_map, 15).getLocation();
			WorldEditUtil.replacecopy(loc1, wall_schem, 90);
		}

		// Lato con Porta nella mura
		VillageCuboid latoEst = cuboid.getFace(CuboidDirection.East);
		int metaEst = 4;
		int IntEst = 0;

		for (Chunk chunk : latoEst.getChunks()) {

			IntEst++;

			// Espando il chunk
			ExpandBorder expander = new ExpandBorder(id, chunk);
			expander.register();

			Location loc1 = chunk.getBlock(0, height_map, 0).getLocation();

			if (IntEst == metaEst) {
				generateDoors(loc1, 0);
				continue;
			}

			if (angoli.contains(chunk)) {
				continue;
			}

			WorldEditUtil.replacecopy(loc1, wall_schem);
		}

		// Lato con le porta davanti
		VillageCuboid latoOvest = cuboid.getFace(CuboidDirection.West);
		int metaOvest = 4;
		int IntOvest = 0;

		for (Chunk chunk : latoOvest.getChunks()) {

			IntOvest++;

			// Espando il chunk
			ExpandBorder expander = new ExpandBorder(id, chunk);
			expander.register();

			Location loc1 = chunk.getBlock(15, height_map, 15).getLocation();

			if (IntOvest == metaOvest) {
				generateDoors(loc1, 180);
				continue;
			}

			if (angoli.contains(chunk)) {
				continue;
			}

			WorldEditUtil.replacecopy(loc1, wall_schem, 180);
		}

		// Prendo gli angoli del villaggio
		Location corner1 = chunk_corner1.getBlock(15, height_map, 15).getLocation();
		setCornerSchem(corner1.getBlock().getLocation(), 2);

		Location corner2 = chunk_corner2.getBlock(15, height_map, 0).getLocation();
		setCornerSchem(corner2.getBlock().getLocation(), 1);

		Location corner3 = chunk_corner3.getBlock(0, height_map, 15).getLocation();
		setCornerSchem(corner3.getBlock().getLocation(), 4);

		Location corner4 = chunk_corner4.getBlock(0, height_map, 0).getLocation();
		setCornerSchem(corner4.getBlock().getLocation(), 5);

		return true;
	}

	/**
	 * Genero le porte del villaggio
	 * 
	 * @return
	 */
	public boolean generateDoors(Location loc, int rotate) {

		String door_schem = CraftOfClans.config.getString("village.wall-door-schematic");

		if (rotate != 0) {
			WorldEditUtil.replacecopy(loc, door_schem, rotate);
		} else {
			WorldEditUtil.replacecopy(loc, door_schem);
		}
		return true;
	}

	private void setCornerSchem(Location loc, Integer director) {

		String wall_corner_schem = CraftOfClans.config.getString("village.wall-corner-schematic");

		int height_map = VillageSettings.getHeight(id);

		Location finale = loc.clone();
		// Location test = loc.clone();

		Integer rotate = 0;
		switch (director) {
		case 1:
			// test.add(0, 25, 0).getBlock().setType(Material.IRON_BLOCK);
			finale = loc.getChunk().getBlock(0, height_map, 15).getLocation();
			rotate = 90;
			break;
		case 2:
			// test.add(0, 25, 0).getBlock().setType(Material.GOLD_BLOCK);
			finale = loc.getChunk().getBlock(0, height_map, 0).getLocation();
			break;
		case 4:
			// test.add(0, 25, 0).getBlock().setType(Material.COBBLESTONE);
			finale = loc.getChunk().getBlock(15, height_map, 0).getLocation();
			rotate = -90;
			break;
		case 5:
			// test.add(0, 25, 0).getBlock().setType(Material.DIAMOND_BLOCK);
			finale = loc.getChunk().getBlock(15, height_map, 15).getLocation();
			rotate = 180;
			break;
		}
		// finale.clone().add(0, 25, 0).getBlock().setType(Material.DIRT);
		WorldEditUtil.replacecopy(finale, wall_corner_schem, rotate);
	}

	/**
	 * Preparo l'area dei bordi protezione
	 */
	public void impostoBordiProtezione() {

		// Tetto
		VillageCuboid tetto = cuboid.getFace(CuboidDirection.Up);
		ArrayList<Block> blocchi_tetto = Lists.newArrayList(tetto.iterator());

		int height_tetto = VillageSettings.getHeight(id) + 17 > Bukkit.getWorld("clansworld").getMaxHeight() - 1 ? Bukkit.getWorld("clansworld").getMaxHeight() - 1 : VillageSettings.getHeight(id) + 17;
		int height_lato = height_tetto + 3 > Bukkit.getWorld("clansworld").getMaxHeight() - 1 ? Bukkit.getWorld("clansworld").getMaxHeight() - 1 : height_tetto + 3;

		for (Block blocco : blocchi_tetto) {
			Location bloccodeciso = blocco.getLocation().clone();
			bloccodeciso.setY(height_tetto);
			bloccodeciso.getBlock().setType(Material.BARRIER);
		}

		RandomPattern pat = new RandomPattern();
		pat.add(new BlockPattern(BlockTypes.BARRIER.getDefaultState()), 1);

		ArrayList<Chunk> chunks = new ArrayList<Chunk>();

		VillageCuboid latoSud = cuboid.getFace(CuboidDirection.South);
		chunks.addAll(latoSud.getChunks());

		VillageCuboid latoNord = cuboid.getFace(CuboidDirection.North);
		chunks.addAll(latoNord.getChunks());

		VillageCuboid latoEst = cuboid.getFace(CuboidDirection.East);
		chunks.addAll(latoEst.getChunks());

		VillageCuboid latoOvest = cuboid.getFace(CuboidDirection.West);
		chunks.addAll(latoOvest.getChunks());

		for (Chunk chunk : chunks) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = VillageSettings.getHeight(id); y < height_lato + 1; y++) {

						Block blocco = chunk.getBlock(x, y, z);
						if (blocco.getType().equals(Material.WHITE_CARPET)) {
							Location pos1 = blocco.getLocation();
							Location pos2 = pos1.clone();
							pos2.setY(height_lato);

							WorldEditUtil.setBlock(pat, pos1, pos2);
						}

					}
				}
			}
		}
	}

	@Override
	public void run() {

		// Creo il municipio
		create();

		// Genero i bordi
		generateBordi();

		// Imposto i blocchi invisibili di protezione
		Bukkit.getScheduler().runTaskLater(CraftOfClans.getInstance(), new Runnable() {
			public void run() {
				impostoBordiProtezione();
			}
		}, 60L);

		// Genero il primo Guardiano
		GuardianVillage gvillage = GuardianNPC.generateNPC(this.structure, 1);
		gvillage.spawn();
	}
}
