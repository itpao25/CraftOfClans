package it.itpao25.craftofclans.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import com.google.common.collect.Lists;

import it.itpao25.craftofclans.api.ResourceChangeValue;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.map.ExpanderRegister;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.WorldEditUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

public class StructuresUpgrade {

	private StructuresId struttura;
	private PlayerStored sender;
	private Boolean response = false;
	private VillageCuboid new_cuboid;
	private Location old_x;

	public StructuresUpgrade(StructuresId struttura, PlayerStored sender) {

		this.struttura = struttura;
		this.sender = sender;

		update();
	}

	/**
	 * Controllo se è disponible un upgrade
	 * 
	 * @return
	 */
	private boolean checkLevel() {
		if (struttura.hasLevelUp()) {
			return true;
		}
		return false;
	}

	/**
	 * Controllo se la struttura può essere upgradata (se c'è spazio disponibile)
	 * 
	 * @return
	 */
	private boolean checkSpace() {

		// Controllo se è il municipio
		if (struttura.getType().equals("TOWNHALL")) {
			this.new_cuboid = struttura.getCuboid();
			this.old_x = new_cuboid.corners()[0].getLocation();
			this.old_x.setY(VillageSettings.getHeight(struttura.getVillage()));
			return true;
		}

		// Nuova posizione
		String schem = struttura.getDataCustom(struttura.getLevel() + 1, "schematics-name");
		SchematicsSize size = new SchematicsSize(schem);
		int x = size.getWidth();
		int z = size.getLenght();
		int y = size.getHeight();

		Location loc1 = struttura.getCuboid().corners()[0].getLocation();
		loc1.setY(VillageSettings.getHeight(struttura.getVillage()) - 1);

		Location loc2;
		if ((struttura.getType().toString().equals(StructuresEnum.VILLAGE_WALL.toString()) || struttura.getType().toString().equals(StructuresEnum.VILLAGE_GATE_WALL.toString()))) {
			loc2 = loc1.clone();
			loc2.setY(VillageSettings.getHeight(struttura.getVillage()));
		} else {
			loc2 = loc1.clone().add(+(x + 1), +(y + 1), +(z + 1));
		}

		// Vecchie posizioni
		List<Block> blocks = Lists.newArrayList(struttura.getCuboid().iterator());
		List<BlockState> backup = new ArrayList<>();
		List<BlockState> backup_droppable = new ArrayList<>();
		ArrayList<String> blocchi_droppabili = WorldEditUtil.listaOggettiDroppabili();

		// Prendo prima di blocchi che potrebbero buggarsi quindi dropparsi
		for (Block blocco : blocks) {
			if (blocco.getY() >= VillageSettings.getHeight(struttura.getVillage())) {
				if (blocchi_droppabili.contains(blocco.getType().toString())) {
					backup_droppable.add(blocco.getState());
					blocco.setType(Material.AIR);
				}
			}
		}

		for (Block blocco : blocks) {
			if (!blocchi_droppabili.contains(blocco.getType().toString())) {
				Block nuovo = blocco;
				backup.add(nuovo.getState());
				if (blocco.getY() >= VillageSettings.getHeight(struttura.getVillage())) {
					blocco.setType(Material.AIR);
				}
			}
		}

		VillageCuboid new_cuboid = new VillageCuboid(loc1, loc2);

		this.new_cuboid = new_cuboid;

		// Nuova posizione (blocchi)
		int index_nuovi = 0;
		List<Block> nuovo_blocchi = Lists.newArrayList(new_cuboid.iterator());
		Iterator<Block> nuovo_blocchi_it = nuovo_blocchi.iterator();

		if (new_cuboid != null) {
			while (nuovo_blocchi_it.hasNext()) {
				Block blocco = nuovo_blocchi_it.next();
				if (blocco.getY() < VillageSettings.getHeight(struttura.getVillage())) {
					nuovo_blocchi_it.remove();
					continue;
				}
				if (blocco.getType().equals(Material.AIR)) {
					// Controllo se lo spazio è claimato
					if (ExpanderRegister.isPresent(blocco.getChunk())) {
						if (MapInfo.getStructures(blocco.getLocation()) == null) {
							index_nuovi++;
						} else {
							if (MapInfo.getStructures(blocco.getLocation()).equals(this.struttura)) {
								index_nuovi++;
							}
						}
					}
				}
			}
		}
		
		// Ripristino il backup
		for (BlockState backup_block : backup) {
			backup_block.getLocation().getBlock().setBlockData(backup_block.getBlockData());
		}
		
		// Ripristino il backup sensibile
		for (BlockState backup_block : backup_droppable) {
			backup_block.getLocation().getBlock().setBlockData(backup_block.getBlockData());
		}

		// Conto i nuovi blocchi
		if (index_nuovi != nuovo_blocchi.size()) {
			return false;
		}
		
		return true;
	}

	/**
	 * Incollo la struttura aggiornandola
	 * 
	 * @return
	 */
	private boolean paste() {

		Location loc1 = new_cuboid.corners()[0].getLocation();
		loc1.setY(VillageSettings.getHeight(struttura.getVillage()));

		// Location loc2 = new_cuboid.corners()[5].getLocation();
		String schem = struttura.getDataCustom(struttura.getLevel() + 1, "schematics-name");

		// Solamente per il municipio
		HashMap<Integer, HashMap<ItemStack, Integer>> oggetti_chest = new HashMap<>();

		// Prima di incollare, elimino i blocchi precedenti
		List<Block> blocks = Lists.newArrayList(struttura.getCuboid().iterator());
		ArrayList<String> blocchi_droppabili = WorldEditUtil.listaOggettiDroppabili();

		// Prendo prima di blocchi che potrebbero buggarsi quindi dropparsi
		for (Block blocco : blocks) {
			if (blocco.getY() >= VillageSettings.getHeight(struttura.getVillage())) {
				if (blocchi_droppabili.contains(blocco.getType().toString())) {
					blocco.setType(Material.AIR);
				}
			}
		}

		int index_blocco = 0;
		for (Block blocco : blocks) {

			if (!blocchi_droppabili.contains(blocco.getType().toString())) {
				// Inoltre se è il municipio, prendo i valori delle chest
				if (struttura.getType().equals("TOWNHALL")) {
					if (blocco.getState() == null) {
						continue;
					}
					if (blocco.getState() instanceof Chest) {
						Chest chest = (Chest) blocco.getState();
						ItemStack[] itemsinchest = chest.getInventory().getContents();
						for (ItemStack oggetto_in_chest : itemsinchest) {
							if (oggetto_in_chest == null) {
								continue;
							}
							HashMap<ItemStack, Integer> local = new HashMap<>();
							local.put(oggetto_in_chest, oggetto_in_chest.getAmount());
							oggetti_chest.put(index_blocco, local);
							index_blocco++;
						}
					}
				}

				if (blocco.getY() >= VillageSettings.getHeight(struttura.getVillage())) {
					if (blocco.getState() == null) {
						continue;
					}
					if (blocco.getState() instanceof Chest) {
						Chest chest = (Chest) blocco.getState();
						chest.getInventory().clear();
					}
					blocco.setType(Material.AIR);
				} else {
					if (blocco.getY() == VillageSettings.getHeight(struttura.getVillage()) - 1) {
						// Imposto il materiale come l'expander
						Location loc1_terreno = blocco.getLocation();
						Location loc2_terreno = blocco.getLocation();
						WorldEditUtil.setBlock(VillageSettings.getMaterialExpanded(), loc1_terreno, loc2_terreno);
						continue;
					}
					if (blocco.getY() < VillageSettings.getHeight(struttura.getVillage()) - 20) {
						// Imposto la terra
						blocco.setType(Material.DIRT);
					} else {
						blocco.setType(VillageSettings.getMaterialGeneration());
					}
				}
			}
		}

		Location pasteLoc = this.new_cuboid.corners()[0].getLocation().clone();

		// Aggiungo un blocco interno solo se non è un municipio, un muro o una porta
		// difensiva
		if (struttura.getType().equals("TOWNHALL") == false 
				&& (!struttura.getType().toString().equals(StructuresEnum.VILLAGE_WALL.toString()))
				&& (!struttura.getType().toString().equals(StructuresEnum.VILLAGE_GATE_WALL.toString()))) {
			
			pasteLoc.add(1, 0, 1);
		}
		
		if (WorldEditUtil.replacecopy(pasteLoc, schem)) {

			// Nuova collocazione
			if (struttura.getType().equals("TOWNHALL") == false) {
				CraftOfClansData.get().set("villages." + struttura.getVillage().getIDescaped() + ".structures." + struttura.getId() + ".coord",
						this.new_cuboid.getLowerX() + "_" + this.new_cuboid.getLowerY() + "_" + this.new_cuboid.getLowerZ() + "_" + this.new_cuboid.getUpperX() + "_" + this.new_cuboid.getUpperY() + "_" + this.new_cuboid.getUpperZ());
				CraftOfClansData.save();

				// Rimuovo gli npc se ci sono
				struttura.despawnNPC();

				StructuresId nuova = struttura.setData();

				// Rispawno gli NPC
				nuova.spawnNPC();

				if (SchematicsHandler.structures_registred.containsKey(struttura)) {
					SchematicsHandler.structures_registred.remove(struttura);
				}
				SchematicsHandler.structures_registred.put(nuova, this.new_cuboid);
			}

			// Ripristino del contenuto delle chest
			if (struttura.getType().equals("TOWNHALL")) {
				checkTownhallChest(oggetti_chest);
			}

			ResourceChangeValue event = new ResourceChangeValue(sender.get());
			Bukkit.getServer().getPluginManager().callEvent(event);

			return true;
		}
		return false;
	}

	/**
	 * Gestisco i costi dell'aggiornamento
	 * 
	 * @return
	 */
	private boolean resource() {

		double cost_gems = struttura.getDataCustom(struttura.getLevel() + 1, "cost_gems") != null ? Double.parseDouble(struttura.getDataCustom(struttura.getLevel() + 1, "cost_gems")) : 0;
		double cost_elixir = struttura.getDataCustom(struttura.getLevel() + 1, "cost_elixir") != null ? Double.parseDouble(struttura.getDataCustom(struttura.getLevel() + 1, "cost_elixir")) : 0;
		double cost_gold = struttura.getDataCustom(struttura.getLevel() + 1, "cost_gold") != null ? Double.parseDouble(struttura.getDataCustom(struttura.getLevel() + 1, "cost_gold")) : 0;
		double cost_dark_elixir = struttura.getDataCustom(struttura.getLevel() + 1, "cost_dark_elixir") != null ? Double.parseDouble(struttura.getDataCustom(struttura.getLevel() + 1, "cost_dark_elixir")) : 0;

		if (cost_gems > 0) {
			if (sender.hasGems(cost_gems) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", cost_gems + ""));
				return false;
			}
		}
		if (cost_elixir > 0) {
			if (sender.hasElixir(cost_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", cost_elixir + ""));
				return false;
			}
		}
		if (cost_gold > 0) {
			if (sender.hasGold(cost_gold) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", cost_gold + ""));
				return false;
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.hasElixirNero(cost_dark_elixir) == false) {
				sender.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", cost_dark_elixir + ""));
				return false;
			}
		}
		// Rimuovo le risorse
		if (cost_gems > 0) {
			if (sender.removeGems(cost_gems)) {
				cost_gems = 0;
			}
		}
		if (cost_elixir > 0) {
			if (sender.removeElixir(cost_elixir)) {
				cost_elixir = 0;
			}
		}
		if (cost_gold > 0) {
			if (sender.removeGold(cost_gold)) {
				cost_gold = 0;
			}
		}
		if (cost_dark_elixir > 0) {
			if (sender.removeElixirNero(cost_dark_elixir)) {
				cost_dark_elixir = 0;
			}
		}
		return true;
	}

	/**
	 * Controllo se l'aggiornamento può essere effettuato controllo il livello del
	 * municipio è ok
	 * 
	 * @return
	 */
	public boolean hasLevelTownhall() {
		VillageId id = struttura.getVillage();
		if (struttura.getDataCustom(struttura.getLevel() + 1, "townhall-required") != null) {
			if (_Number.isNumero(struttura.getDataCustom(struttura.getLevel() + 1, "townhall-required"))) {
				int level = Integer.parseInt(struttura.getDataCustom(struttura.getLevel() + 1, "townhall-required"));
				if (id.getLevelTownHall() < level) {
					sender.sendMessage(Color.message(CraftOfClansM.getString("messages.townhall-required-upgrade")).replace("%1%", level + ""));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Imposto l'aggiornamento della struttura
	 * 
	 * @return
	 */
	public boolean update() {
		int next_level = struttura.getLevel() + 1;
		if (checkLevel() == false) {
			return false;
		}

		// Controllo se il livello del municipio soddisfa la richiesta
		if (hasLevelTownhall() == false) {
			return false;
		}

		if (checkSpace() == false) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("messages.upgrade-error-nospace")));
			return false;
		}

		// Controllo se il giocatore ha le risorse necessarie
		// I messaggi vengono gestiti dalla funzione in modo diretto
		if (resource() == false) {
			return false;
		}

		if (paste() == false) {
			sender.sendMessage(Color.message(CraftOfClansM.getString("messages.upgrade-error")));
			return false;
		}
		sender.sendMessage(Color.message(CraftOfClansM.getString("messages.upgrade-success")));

		this.response = true;
		struttura.setLevel(next_level);
		return true;
	}

	/**
	 * Rimetto gli oggetti nelle chest del municipio
	 * 
	 * @param oggetti_chest
	 * @return
	 */
	private boolean checkTownhallChest(HashMap<Integer, HashMap<ItemStack, Integer>> oggetti_chest) {

		// Dopo aver rincollato la struttura, inserisco nuovamente gli item nella chest
		List<Block> nuovi_blocchi = Lists.newArrayList(this.new_cuboid.iterator());
		LogHandler.log(oggetti_chest.size() + " items to put in chest of the new structure (" + struttura.getType() + ")");
		for (Block blocco : nuovi_blocchi) {

			if (blocco.getState() == null) {
				continue;

			}
			if (blocco.getState() instanceof Chest) {

				Chest chest = (Chest) blocco.getState();
				Iterator<Entry<Integer, HashMap<ItemStack, Integer>>> oggetti_da_inserire = oggetti_chest.entrySet().iterator();
				while (oggetti_da_inserire.hasNext()) {

					Entry<Integer, HashMap<ItemStack, Integer>> entry = oggetti_da_inserire.next();
					if ((chest.getInventory().firstEmpty() == -1)) {
						break;
					}

					Iterator<Entry<ItemStack, Integer>> local_hash = entry.getValue().entrySet().iterator();
					ItemStack oggetto = null;

					while (local_hash.hasNext()) {
						Entry<ItemStack, Integer> thisEntry = (Entry<ItemStack, Integer>) local_hash.next();
						oggetto = (thisEntry.getKey());
						oggetto.setAmount(thisEntry.getValue());
					}

					chest.getInventory().addItem(oggetto);
					oggetti_da_inserire.remove();
				}
			}
		}
		return false;
	}

	/**
	 * Ritorno con la risposta
	 * 
	 * @return
	 */
	public boolean getResponse() {
		return this.response;
	}
}
