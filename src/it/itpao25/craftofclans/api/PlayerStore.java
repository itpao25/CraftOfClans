package it.itpao25.craftofclans.api;

import it.itpao25.craftofclans.attack.AttackerManager;
import it.itpao25.craftofclans.player.PlayerStored;
import java.util.UUID;

public class PlayerStore {

	private UUID uuid;
	private PlayerStored stored;

	public PlayerStore(UUID id) {
		this.uuid = id;
		this.stored = new PlayerStored(id);
	}

	/**
	 * Check if the player exits in database
	 * 
	 * @return
	 */
	public boolean isExist() {
		return stored.isExist();
	}

	/**
	 * Check if the player has one village registred
	 * 
	 * @return
	 */
	public boolean hasVillage() {
		return stored.hasVillage();
	}

	/**
	 * Get number of gems of the player
	 * 
	 * @return
	 */
	public double getGems() {
		return stored.getGems();
	}

	/**
	 * Remove gems to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean removeGems(double number) {
		return stored.removeGems(number);
	}

	/**
	 * Check if the player has gems
	 * 
	 * @param number
	 * @return
	 */
	public boolean hasGems(double number) {
		return stored.hasGems(number);
	}

	/**
	 * Add gems to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean addGems(double number) {
		return stored.addGems(number);
	}

	/**
	 * Returns int of max elixir for the user (Based on structures placed)
	 * 
	 * @return
	 */
	public double getMaxElixir() {
		return stored.getMaxElixir();
	}

	/**
	 * Get int of elixir of the player
	 * 
	 * @return
	 */
	public double getElixir() {
		return stored.getElixir();
	}

	/**
	 * Remove gems to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean removeElixir(double number) {
		return stored.removeElixir(number);
	}

	/**
	 * Check if the player has Elixir
	 * 
	 * @param number
	 * @return
	 */
	public boolean hasElixir(double number) {
		return stored.hasElixir(number);
	}

	/**
	 * Add elixir to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean addElixir(double number) {
		return stored.addElixir(number);
	}

	/**
	 * Returns int of max gold for the user (Based on structures placed)
	 * 
	 * @return
	 */
	public double getMaxGold() {
		return stored.getMaxGold();
	}

	/**
	 * Get int of gold to the player
	 * 
	 * @return
	 */
	public double getGold() {
		return stored.getGold();
	}

	/**
	 * Remove gold to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean removeGold(double number) {
		return stored.removeGold(number);
	}

	/**
	 * Check if the player has Gold
	 * 
	 * @param number
	 * @return
	 */
	public boolean hasGold(double number) {
		return stored.hasGold(number);
	}

	/**
	 * Add gold to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean addGold(double number) {
		return stored.addGold(number);
	}

	/**
	 * Get trophies of the player
	 * 
	 * @return
	 */
	public int getTrophies() {
		return stored.getTrofei();
	}

	/**
	 * Remove trophies to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean removeTrophies(int number) {
		return stored.removeTrofei(number);
	}

	/**
	 * Add trophies to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean addTrophies(int number) {
		return stored.addTrofei(number);
	}

	/**
	 * Check player has trophies
	 * 
	 * @param number
	 * @return
	 */
	public boolean hasTrophies(int number) {
		return stored.hasTrofei(number);
	}

	/**
	 * Returns int of max dark elixir for the user (Based on structures placed)
	 * 
	 * @return
	 */
	public double getMaxDarkElixir() {
		return stored.getMaxElixirNero();
	}

	/**
	 * Get int of dark elixir of the player
	 * 
	 * @return
	 */
	public double getDarkElixir() {
		return stored.getElixirNero();
	}
	
	/**
	 * Remove dark elixir to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean removeDarkElixir(double number) {
		return stored.removeElixirNero(number);
	}

	/**
	 * Check if the player has Dark Elixir
	 * 
	 * @param number
	 * @return
	 */
	public boolean hasDarkElixir(double number) {
		return stored.hasElixirNero(number);
	}

	/**
	 * Add dark elixir to the player
	 * 
	 * @param number
	 * @return
	 */
	public boolean addDarkElixir(double number) {
		return stored.addElixirNero(number);
	}

	/**
	 * Check if the player has in attack mode
	 * 
	 * @return
	 */
	public boolean hasInAttack() {
		if (AttackerManager.hasPlayer(uuid)) {
			return true;
		}
		return false;
	}

	/**
	 * Get UUID of the player
	 * 
	 * @return
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Aggiorno la scoreboard
	 * 
	 * @return
	 */
	public void refreshScoreboard() {
		/*
 		ResourceChangeValue event = new ResourceChangeValue(stored.get());
		Bukkit.getServer().getPluginManager().callEvent(event);
		*/
	}
}
