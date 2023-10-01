package it.itpao25.craftofclans.api;

import it.itpao25.craftofclans.structures.StructuresId;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResourceCollected extends Event implements Cancellable {

	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();
	private StructuresId struttura;

	public ResourceCollected(StructuresId structuresId) {
		this.struttura = structuresId;
	}

	public StructuresId getStructure() {
		return this.struttura;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean paramBoolean) {
		this.cancelled = paramBoolean;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
