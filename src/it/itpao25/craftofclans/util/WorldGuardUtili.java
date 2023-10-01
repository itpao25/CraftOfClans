package it.itpao25.craftofclans.util;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardUtili {

	/**
	 * Se è una zona safezone
	 * 
	 * @param loc
	 * @return
	 */
	public static boolean isSafeZone(Location loc) {
		
		RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(loc.getWorld()));
		if (regionManager == null) {
			return false;
		}

		ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
		if (set.size() <= 0) {
			return false;
		}

		return true;
	}
}
