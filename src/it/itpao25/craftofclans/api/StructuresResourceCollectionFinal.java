package it.itpao25.craftofclans.api;

import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.village.VillageId;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class StructuresResourceCollectionFinal extends PlayerEvent implements Cancellable {
	private StructuresId tipo;
	private VillageId village;
	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();

	public StructuresResourceCollectionFinal(StructuresId tipo, VillageId village, Player p) {
		super(p);
		this.tipo = tipo;
		this.village = village;
	}

	@Override
	public void setCancelled(boolean paramBoolean) {
		this.cancelled = paramBoolean;
	}

	public StructuresId getStructure() {
		return tipo;
	}

	public VillageId getVillage() {
		return village;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
