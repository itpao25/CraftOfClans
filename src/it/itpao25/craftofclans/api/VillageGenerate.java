package it.itpao25.craftofclans.api;

import it.itpao25.craftofclans.village.VillageId;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VillageGenerate extends Event implements Cancellable {

	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();
	private VillageId village;
	private Player p;

	public VillageGenerate(VillageId village, Player p) {
		this.village = village;
		this.p = p;
	}

	public boolean isCancelled() {
		return cancelled;
	}

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

	public VillageId getVillage() {
		return village;
	}

	public Player getPlayer() {
		return p;
	}
}
