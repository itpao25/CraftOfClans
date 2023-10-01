package it.itpao25.craftofclans.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Location;
import org.primesoft.asyncworldedit.api.worldedit.IAsyncEditSessionFactory;
import org.primesoft.asyncworldedit.api.worldedit.IEditSession;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
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
import com.sk89q.worldedit.session.ClipboardHolder;

import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

@SuppressWarnings("deprecation")
public class AsyncWorldEditUtil {
	
	public static boolean setBlock(RandomPattern pat, Location max, Location min) {
		
		if (!min.getWorld().equals(max.getWorld())) {
			return false;
		}
		
		IAsyncEditSessionFactory factory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
		IEditSession editSession = factory.getThreadSafeEditSession(BukkitAdapter.adapt(min.getWorld()), -1);
		
		BlockVector3 vmax = BlockVector3.at(max.getX(), max.getY(), max.getZ());
		BlockVector3 vmin = BlockVector3.at(min.getX(), min.getY(), min.getZ());
		CuboidRegion cSel = new CuboidRegion(vmax, vmin);
		
		try {
			editSession.setBlocks(cSel, pat);
		} catch (MaxChangedBlocksException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		editSession.close();

		return true;
	}
	
	/**
	 * Faccio il replace di alcuni blocchi
	 */
	public static boolean replacecopy(Location original, String schematicName, Integer rotate) {
		
		VillageId villo = MapInfo.getVillage(original);
		
		File dir = new File(SchematicsHandler.getFolder() + File.separator + schematicName);
		// Controllo se il file esiste
		if (!dir.exists()) {
			LogHandler.error("the schematic file '" + schematicName + "' does not exist!!");
			return false;
		}

		ClipboardFormat format = ClipboardFormats.findByFile(dir);
		try (ClipboardReader reader = format.getReader(new FileInputStream(dir))) {

			Clipboard clipboard = reader.read();
			clipboard.setOrigin(BlockVector3.at(clipboard.getMinimumPoint().getBlockX(), clipboard.getOrigin().getBlockY(), clipboard.getMinimumPoint().getBlockZ()));
			
			IAsyncEditSessionFactory factory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
			IEditSession editSession = factory.getThreadSafeEditSession(BukkitAdapter.adapt(original.getWorld()), -1);
			
			ClipboardHolder holder = new ClipboardHolder(clipboard);
			
			holder.setTransform(new AffineTransform().rotateY(rotate));
			
			Operation operation = holder.createPaste(editSession)
					.to(BlockVector3.at(original.getBlockX(), VillageSettings.getHeight(villo), original.getBlockZ()))
					.ignoreAirBlocks(false)
					.build();
			Operations.complete(operation);
			
			editSession.close();
			
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Faccio il replace di alcuni blocchi
	 */
	public static boolean replacecopy(Location location, String schematicName) {
		
		VillageId villo = MapInfo.getVillage(location);
		
		File dir = new File(SchematicsHandler.getFolder() + File.separator + schematicName);
		// Controllo se il file esiste
		if (!dir.exists()) {
			LogHandler.error("the schematic file '" + schematicName + "' does not exist!!");
			return false;
		}
		
		BlockVector3 pasteLocation = BlockVector3.at(location.getBlockX(), VillageSettings.getHeight(villo), location.getBlockZ());
		
		ClipboardFormat format = ClipboardFormats.findByFile(dir);
		try (ClipboardReader reader = format.getReader(new FileInputStream(dir))) {

			Clipboard clipboard = reader.read();
			clipboard.setOrigin(BlockVector3.at(clipboard.getMinimumPoint().getBlockX(), clipboard.getOrigin().getBlockY(), clipboard.getMinimumPoint().getBlockZ()));
			
			IAsyncEditSessionFactory factory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
			IEditSession editSession = factory.getThreadSafeEditSession(BukkitAdapter.adapt(location.getWorld()), -1);
			
			
			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(pasteLocation)
					.ignoreAirBlocks(true)
					.build();
			Operations.complete(operation);
			
			editSession.close();
			
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
}
