package it.itpao25.craftofclans.worldmanager;

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.village.VillageId;

public class WorldListener implements Listener {

	/**
	 * Il player posiziona lava/acqua
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerPlaceWater(PlayerBucketEmptyEvent event) {
		if (!event.getPlayer().getWorld().getName().equals("clansworld")) {
			return;
		}

		VillageId villo = MapInfo.getVillage(event.getBlock().getLocation());
		if (villo != null) {
			event.setCancelled(true);
		}
	}

	/**
	 * Acqua/lava si espande
	 * 
	 * @param event
	 */
	@EventHandler
	public void onLavaFlow(BlockFromToEvent event) {
		if (!event.getBlock().getLocation().getWorld().getName().equals("clansworld")) {
			return;
		}

		if (event.getBlock().isLiquid()) {
			VillageId villo = MapInfo.getVillage(event.getToBlock().getLocation());
			if (villo != null) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Il villaggio prende fuoco
	 * 
	 * @param event
	 */
	@EventHandler
	public void grassSpread(BlockSpreadEvent event) {
		if (event.getBlock().getWorld().getName().equals("clansworld") == false) {
			return;
		}

		VillageId villo = MapInfo.getVillage(event.getBlock().getLocation());
		if (villo != null) {
			event.setCancelled(true);
		}
	}

	/**
	 * Il villaggio prende fuoco 2
	 * 
	 * @param event
	 */
	@EventHandler
	public void fuocoBlocco(BlockBurnEvent event) {
		if (event.getBlock().getWorld().getName().equals("clansworld") == false) {
			return;
		}

		VillageId villo = MapInfo.getVillage(event.getBlock().getLocation());
		if (villo != null) {
			event.setCancelled(true);
		}
	}

	/**
	 * Spawn dei mob in un villaggio
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getEntity().getWorld().getName().equals("clansworld") == false) {
			return;
		}

		VillageId villo = MapInfo.getVillage(event.getEntity().getLocation());
		if (villo != null) {
			event.setCancelled(true);
		}
	}

	/**
	 * TNT o varie explodono in un villaggio
	 * 
	 * @param event
	 */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity ent = event.getEntity();
		if (ent.getWorld().getName().equals("clansworld")) {

			VillageId villo1 = MapInfo.getVillage(ent.getLocation());
			if (villo1 != null) {
				event.setCancelled(true);
			}

			Iterator<Block> iter = event.blockList().iterator();
			while (iter.hasNext()) {
				Block b = iter.next();

				VillageId villo = MapInfo.getVillage(b.getLocation());
				if (villo != null) {
					iter.remove();
				}
			}
		}
	}
	
	/**
	 * I mob modificano il mondo
	 * 
	 * @param event
	 */
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity ent = event.getEntity();
		if (ent.getWorld().getName().equals("clansworld")) {

			VillageId villo1 = MapInfo.getVillage(event.getBlock().getLocation());
			if (villo1 != null) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Disabilito il pistone nel mondo
	 */
	@EventHandler
	public void disablePiston(BlockPistonExtendEvent event) {
		if (!event.getBlock().getWorld().getName().equals("clansworld")) {
			return;
		}

		Iterator<Block> iter = event.getBlocks().iterator();
		while (iter.hasNext()) {
			Block b = iter.next();

			VillageId villo = MapInfo.getVillage(b.getRelative(event.getDirection()).getLocation());
			if (villo != null) {
				event.setCancelled(true);
				return;
			}

			VillageId villo1 = MapInfo.getVillage(b.getLocation());
			if (villo1 != null) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Disabilito il pistone in ritirata nel mondo
	 */
	@EventHandler
	public void onPistonRitornoEvent(BlockPistonRetractEvent event) {
		if (!event.getBlock().getWorld().getName().equals("clansworld")) {
			return;
		}

		Iterator<Block> iter = event.getBlocks().iterator();
		while (iter.hasNext()) {
			Block b = iter.next();

			VillageId villo = MapInfo.getVillage(b.getLocation());
			if (villo != null) {
				event.setCancelled(true);
				return;
			}
		}
	}
}
