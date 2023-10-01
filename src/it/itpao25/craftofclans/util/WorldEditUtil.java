package it.itpao25.craftofclans.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.EditSession.ReorderMode;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WorldEditUtil {

	@SuppressWarnings("deprecation")
	public static boolean setBlock(RandomPattern pat, Location max, Location min) {

		if (!CraftOfClans.isWorldEdit) {
			LogHandler.error("WorldEdit is not installed!");
			return false;
		}

		// Supporto ad AsyncWorldEdit
		if (CraftOfClans.isAsyncWorldEdit) {
			return AsyncWorldEditUtil.setBlock(pat, max, min);
		}

		if (!min.getWorld().equals(max.getWorld())) {
			return false;
		}

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(min.getWorld()), -1)) {
			editSession.setReorderMode(ReorderMode.MULTI_STAGE);

			BlockVector3 vmax = BlockVector3.at(max.getX(), max.getY(), max.getZ());
			BlockVector3 vmin = BlockVector3.at(min.getX(), min.getY(), min.getZ());
			CuboidRegion cSel = new CuboidRegion(vmax, vmin);

			editSession.setBlocks(cSel, pat);
			editSession.close();
			return true;

		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Incollo una schematics
	 * 
	 * @return
	 */
	public static boolean replacecopy(Location original, String schematicName, Integer rotate) {

		if (!CraftOfClans.isWorldEdit) {
			LogHandler.error("WorldEdit is not installed!");
			return false;
		}
		
		// Supporto ad AsyncWorldEdit
		if (CraftOfClans.isAsyncWorldEdit) {
			return AsyncWorldEditUtil.replacecopy(original, schematicName, rotate);
		}

		File dir = new File(SchematicsHandler.getFolder() + File.separator + schematicName);
		// Controllo se il file esiste
		if (!dir.exists()) {
			LogHandler.error("the schematic file '" + schematicName + "' does not exist!!");
			return false;
		}

		VillageId villo = MapInfo.getVillage(original);
		
		ClipboardFormat format = ClipboardFormats.findByFile(dir);
		try (ClipboardReader reader = format.getReader(new FileInputStream(dir))) {

			Clipboard clipboard = reader.read();
			clipboard.setOrigin(BlockVector3.at(clipboard.getMinimumPoint().getBlockX(), clipboard.getOrigin().getBlockY(), clipboard.getMinimumPoint().getBlockZ()));

			try (@SuppressWarnings("deprecation")
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(original.getWorld()), -1)) {

				editSession.setReorderMode(ReorderMode.MULTI_STAGE);
				ClipboardHolder holder = new ClipboardHolder(clipboard);

				holder.setTransform(new AffineTransform().rotateY(rotate));

				Operation operation = holder.createPaste(editSession).to(BlockVector3.at(original.getBlockX(), VillageSettings.getHeight(villo), original.getBlockZ())).ignoreAirBlocks(false).build();
				Operations.complete(operation);
				editSession.close();

				return true;

			} catch (WorldEditException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Incollo una schematics
	 * 
	 * @return
	 */
	public static boolean replacecopy(Location location, String schematicName) {

		// Supporto ad AsyncWorldEdit
		if (CraftOfClans.isAsyncWorldEdit) {
			return AsyncWorldEditUtil.replacecopy(location, schematicName);
		}

		File dir = new File(SchematicsHandler.getFolder() + File.separator + schematicName);
		// Controllo se il file esiste
		if (!dir.exists()) {
			LogHandler.error("the schematic file '" + schematicName + "' does not exist!!");
			return false;
		}

		VillageId villo = MapInfo.getVillage(location);
		
		BlockVector3 pasteLocation = BlockVector3.at(location.getBlockX(), VillageSettings.getHeight(villo), location.getBlockZ());

		ClipboardFormat format = ClipboardFormats.findByFile(dir);
		try (ClipboardReader reader = format.getReader(new FileInputStream(dir))) {

			Clipboard clipboard = reader.read();
			clipboard.setOrigin(BlockVector3.at(clipboard.getMinimumPoint().getBlockX(), clipboard.getOrigin().getBlockY(), clipboard.getMinimumPoint().getBlockZ()));

			try (@SuppressWarnings("deprecation")
			EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1)) {
				editSession.setReorderMode(ReorderMode.FAST);

				Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(pasteLocation).ignoreAirBlocks(true).build();
				Operations.complete(operation);

				editSession.close();
				return true;

			} catch (WorldEditException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Ritorno con il selection del giocatore
	 * 
	 * @param p
	 * @return
	 */
	public static ArrayList<Location> getSelectionPlayer(Player p) {

		WorldEditPlugin worldEdit = (WorldEditPlugin) CraftOfClans.getInstance().getServer().getPluginManager().getPlugin("WorldEdit");
		Region selection;

		try {
			selection = worldEdit.getSession(p).getSelection(BukkitAdapter.adapt(p.getWorld()));
		} catch (IncompleteRegionException e) {
			p.sendMessage(Color.message("&cYou must make a WorldEdit Selection first"));
			return null;
		}

		BlockVector3 vmin = selection.getMinimumPoint();
		BlockVector3 vmax = selection.getMaximumPoint();

		ArrayList<Location> loc = new ArrayList<>();
		loc.add(new Location(p.getWorld(), vmin.getBlockX(), vmin.getBlockY(), vmin.getBlockZ()));
		loc.add(new Location(p.getWorld(), vmax.getBlockX(), vmax.getBlockY(), vmax.getBlockZ()));

		return loc;
	}

	/**
	 * Lista degli oggetti droppabili nelle strutture
	 * 
	 * @return
	 */
	public static ArrayList<String> listaOggettiDroppabili() {
		
		ArrayList<String> blocchi_droppabili = new ArrayList<String>();
		
		if (CraftOfClans.config.getString("expand.fix-drop-items-on-upgrade") != null) {
			List<String> lista_config = CraftOfClans.config.get().getStringList("expand.fix-drop-items-on-upgrade");
			for(String materiale : lista_config) {
				blocchi_droppabili.add(materiale.toUpperCase());
			}
		} else {
			// Imposto i blocchi che possono essere droppati
			blocchi_droppabili.add("RAIL");
			blocchi_droppabili.add("LADDER");
			blocchi_droppabili.add("STONE_BUTTON");
			blocchi_droppabili.add("WOOD_BUTTON");
			blocchi_droppabili.add("WHITE_CARPET");
			blocchi_droppabili.add("WOOD_PLATE");
			blocchi_droppabili.add("STONE_PLATE");
			blocchi_droppabili.add("GOLD_PLATE");
			blocchi_droppabili.add("IRON_PLATE");
			blocchi_droppabili.add("LEVER");
			blocchi_droppabili.add("TORCH");
			blocchi_droppabili.add("WALL_TORCH");
			blocchi_droppabili.add("PAINTING");
			blocchi_droppabili.add("SIGN");
			blocchi_droppabili.add("SIGN_POST");
			blocchi_droppabili.add("WALL_SIGN");
			blocchi_droppabili.add("FLOWER_POT");
			blocchi_droppabili.add("REDSTONE_TORCH_OFF");
			blocchi_droppabili.add("REDSTONE_TORCH_ON");
		}
		
		return blocchi_droppabili;
	}
}
