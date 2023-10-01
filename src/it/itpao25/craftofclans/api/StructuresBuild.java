package it.itpao25.craftofclans.api;

import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.village.VillageId;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class StructuresBuild extends PlayerEvent implements Cancellable {

	private StructuresEnum tipo;
	private VillageId village;
	private Integer level;
	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();
	private Location loca;

	public StructuresBuild(StructuresEnum tipo, VillageId village, Integer level, Player p, Location loc) {
		super(p);
		this.tipo = tipo;
		this.village = village;
		this.level = level;
		this.loca = loc;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public StructuresEnum getStructure() {
		return tipo;
	}

	public VillageId getVillage() {
		return village;
	}

	public Integer getLevel() {
		return level;
	}

	public Location getLocation() {
		return this.loca;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public StructuresEnum getType() {
		return tipo;
	}
}
