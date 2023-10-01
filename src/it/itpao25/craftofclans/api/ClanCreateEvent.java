package it.itpao25.craftofclans.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ClanCreateEvent extends PlayerEvent implements Cancellable {

	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();

	public ClanCreateEvent(Player who) {
		super(who);
		// TODO Auto-generated constructor stub
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
