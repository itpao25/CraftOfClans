package it.itpao25.craftofclans.api;

import it.itpao25.craftofclans.village.VillageId;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AttackComplete extends PlayerEvent implements Cancellable {
	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();
	private int get_tot_damage;
	private VillageId vittima;

	public AttackComplete(Player who, Integer getTotalDamage, VillageId vittima) {
		super(who);
		this.get_tot_damage = getTotalDamage;
		this.vittima = vittima;
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

	public int getDamageTotal() {
		return get_tot_damage;
	}

	public VillageId getVillageVittima() {
		return vittima;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
