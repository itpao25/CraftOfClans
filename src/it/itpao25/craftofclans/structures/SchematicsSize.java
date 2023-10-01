package it.itpao25.craftofclans.structures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.handler.LogHandler;

public class SchematicsSize {
	private File file;
	private int length;
	private int width;
	private int height;

	public SchematicsSize(String name) {
		if (!CraftOfClans.isWorldEdit) {
			LogHandler.error("WorldEdit is not installed!");
			return;
		}
		File dir = new File(SchematicsHandler.getFolder() + File.separator + name);
		if (!dir.exists()) {
			LogHandler.error("the schematic file '" + name + "' does not exist!");
			return;
		}
		this.file = dir;
		load();
	}

	public boolean load() {

		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			Clipboard clipboard = reader.read();
			
			this.length = clipboard.getDimensions().getBlockZ();
			this.width = clipboard.getDimensions().getBlockX();
			this.height = clipboard.getDimensions().getBlockY();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public int getLenght() {
		return this.length;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
