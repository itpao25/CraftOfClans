package it.itpao25.craftofclans.map;

import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.bukkit.Chunk;
import org.bukkit.Location;
import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

public class Expander {
	
	private Chunk chunk_daclaim;
	private VillageId villo;
	private PlayerStored player;
	private boolean isprima = false;
	private int payed = -1;
	public boolean has_not_resources = false;
	
	/**
	 * Classe principale che mi permette l'espansione del villaggio tramite il chunk
	 * 
	 * @param villo         Villaggio del giocatore
	 * @param chunk_claimed Chunk del giocatore
	 */
	public Expander(VillageId villo, Chunk chunk_claimed) {
		this.chunk_daclaim = chunk_claimed;
		this.villo = villo;
	}
	
	public boolean register() {
		if (ExpanderRegister.isPresent(chunk_daclaim)) {
			return false;
		}
		if (isBorder()) {
			return false;
		}
		// Inserico il chunk claimato
		if (villo.isExistInData() == false) {
			villo.setInData();
		}
		if (setInData()) {
			if (setTerrain(false)) {
				return true;
			}
		}
		return false;
	}

	public boolean register(PlayerStored p) {
		if (ExpanderRegister.isPresent(chunk_daclaim)) {
			return false;
		}
		if (isBorder()) {
			return false;
		}
		this.player = p;
		// Inserico il chunk claimato
		if (villo.isExistInData() == false) {
			villo.setInData();
		}
		if (setInData()) {
			if (setTerrain(false)) {
				return true;
			}
		}
		return false;
	}

	public boolean register(PlayerStored p, boolean isPrimo) {
		this.isprima = isPrimo;
		if (ExpanderRegister.isPresent(chunk_daclaim)) {
			return false;
		}
		if (isBorder()) {
			return false;
		}
		this.player = p;
		// Inserico il chunk claimato
		if (villo.isExistInData() == false) {
			villo.setInData();
		}
		if (setInData()) {
			if (setTerrain(isPrimo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Imposto il terreno dove sta per venire registrato nel chunk corrente
	 * 
	 * @return
	 */
	private boolean setTerrain(boolean isPrimo) {
		if (this.chunk_daclaim == null) {
			return false;
		}
		
		Location loc1 = this.chunk_daclaim.getBlock(0, VillageSettings.getHeight(villo) - 1, 0).getLocation();
		Location loc2 = this.chunk_daclaim.getBlock(15, VillageSettings.getHeight(villo) - 1, 15).getLocation();
		WorldEditUtil.setBlock(VillageSettings.getMaterialExpanded(), loc1, loc2);
		
		// Se devo rimuovere automaticamente l'erba
		boolean erba_rimuovere = CraftOfClans.config.getBoolean("generator.surface-grass");
		if(erba_rimuovere && !CraftOfClans.config.getBoolean("expand.grass-removal-automatically-onexpand")) {
			erba_rimuovere = false;
		}
		
		// Nel primo expand devo togliere l'erba sotto
		if (erba_rimuovere || isPrimo) {
			Location loc1_1 = this.chunk_daclaim.getBlock(0, VillageSettings.getHeight(villo), 0).getLocation();
			Location loc2_2 = this.chunk_daclaim.getBlock(15, VillageSettings.getHeight(villo), 15).getLocation();
			WorldEditUtil.setBlock(VillageSettings.getAirMaterial(), loc1_1, loc2_2);
		}
		return true;
	}

	/**
	 * Aggiungo il file nel file config.yml
	 * 
	 * @return
	 */
	private boolean setInData() {
		// Controllo le gemme e rimuovo
		// E controllo se il numero massimo non è stato superato
		if (!checkMax() || !checkGems()) {
			return false;
		}
		if (this.villo.isExistInData()) {
			// Prima
			List<String> prima = CraftOfClansData.get().getStringList("villages." + this.villo.getIDescaped() + ".expanded");
			int x = chunk_daclaim.getX();
			int z = chunk_daclaim.getZ();
			prima.add(x + "_" + z);

			CraftOfClansData.get().set("villages." + this.villo.getIDescaped() + ".expanded", prima);
			CraftOfClansData.save();

			if (isprima != true) {
				villo.updateQueryExpanded();
			}
			LogHandler.log("Adding new expansion for the village " + this.villo.getID() + " - chunk " + x + " " + z);
			if (this.player != null && isprima != true) {
				if (payed != -1) {
					this.player.sendMessage(Color.message(CraftOfClansM.getString("messages.expander-success")).replace("%1%", payed + ""));
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Controllo e rimuovo le gemme necessarie
	 * 
	 * @return
	 */
	public boolean checkGems() {
		if (isprima) {
			return true;
		}
		
		// Gemme finali da rimuovere
		int da_rimuovere = 0;
		
		int required = CraftOfClans.config.getInt("expand.cost-gems");
		int total = villo.getNumberExpandeded();
		String increment = CraftOfClans.config.getString("expand.incremet").replace("cost", required + "").replace("total_expand", total + "");
		
		if (total == 0) {
			da_rimuovere = required;
		} else {
			
			boolean calcolo_increment_riuscito = false;
			try {
				
				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine engine = mgr.getEngineByName("JavaScript");
				
				if(engine != null) {
					String calcolo_string = String.valueOf(engine.eval(increment));
					calcolo_string = calcolo_string.replaceAll("(?<=^\\d+)\\.0*$", "");
					
					int calcolo = Integer.parseInt(calcolo_string);
					da_rimuovere = calcolo + required;
					
					calcolo_increment_riuscito = true;
				}
			} catch (ScriptException e) {
				calcolo_increment_riuscito = false;
			}
			if(!calcolo_increment_riuscito) {
				int calcolo = required *  total / 2;
				da_rimuovere = calcolo + required;
			}
		}
		
		LogHandler.log("Mathematical calculation from javascript " + increment + " = " + da_rimuovere);
		if (player.removeGems(da_rimuovere)) {
			this.payed = da_rimuovere;
			return true;
		} else {
			this.has_not_resources = true;
			this.player.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", da_rimuovere + ""));
		}
		return false;
	}

	/**
	 * Controllo se è possibile effettuare l'espansione oppure si è arrivato al
	 * limite
	 * 
	 * @return
	 */
	public boolean checkMax() {
		int total = villo.getNumberExpandeded();
		int max = CraftOfClans.config.getInt("expand.max-for-village");
		if (max <= total) {
			if (this.player != null) {
				player.sendMessage(Color.message(CraftOfClansM.getString("messages.expander-limit-reached")));
			}
			return false;
		}
		return true;
	}

	/**
	 * Se è un bordo villaggio non può claimare
	 * 
	 * @return
	 */
	public boolean isBorder() {
		// Prima
		List<String> bordi = CraftOfClansData.get().getStringList("villages." + this.villo.getIDescaped() + ".border");
		int x = chunk_daclaim.getX();
		int z = chunk_daclaim.getZ();
		String chunk = x + "_" + z;
		if (bordi.contains(chunk)) {
			return true;
		}
		return false;
	}
}
