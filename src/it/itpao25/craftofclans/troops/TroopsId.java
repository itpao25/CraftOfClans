package it.itpao25.craftofclans.troops;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.guardian.NPCSkin;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.SkullSkin;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.worldmanager.Sounds;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class TroopsId {

	private int level;
	public String type;
	private PlayerStored pstored;

	private Date last_recharge;
	private Date last_withdrawal;

	public String display_name;

	// Livello della caserma del villaggio
	public int level_barracks;

	// Livello del laboratorio del villaggio
	public int level_laboratory;

	public TroopsId(PlayerStored pstored, String type, int level) {
		this.type = type;
		this.level = level;
		this.pstored = pstored;

		setData();
	}

	/**
	 * Inizializzo la truppa creata su un villaggio
	 * 
	 * @param pstored
	 * @param type
	 * @param level
	 */
	public TroopsId(PlayerStored pstored, String type, int level, String path_data) {
		this.type = type;
		this.level = level;
		this.pstored = pstored;

		try {
			// Imposto i dati della truppa
			String data_recharge = CraftOfClansData.getString(path_data + ".last_recharge");
			if (!data_recharge.equals("0000-00-00 00:00:00")) {
				last_recharge = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data_recharge);
			} else {
				// è necessario addestrare la truppa
				last_recharge = null;
			}

			String data_withdrawal = CraftOfClansData.getString(path_data + ".last_withdrawal");
			last_withdrawal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data_withdrawal);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		setData();
	}

	private void setData() {
		this.display_name = Color.translate(CraftOfClans.troops.getString("troops." + type + ".display_name"));

		for (Entry<StructuresId, String> id : pstored.getVillage().getStructuresList().entrySet()) {

			// Imposto il livello della caserma
			if (id.getKey().getType().equals(StructuresEnum.BARRACKS.toString())) {
				this.level_barracks = id.getKey().getLevel();
			}

			// Imposto il livello del laboratorio
			if (id.getKey().getType().equals(StructuresEnum.LABORATORY.toString())) {
				this.level_laboratory = id.getKey().getLevel();
			}
		}

	}

	public boolean hasNextLevel() {
		int next = level + 1;
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + next) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Lista dei costi
	 * 
	 * @return
	 */
	public List<String> getCostStringList() {

		List<String> lista_lore = new ArrayList<>();

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gems") != null) {
			double cost_gems = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gems")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gems")) : 0;
			if (cost_gems != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-gems") + ": " + _Number.showNumero(cost_gems)));
			}
		}

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_elixir") != null) {
			double cost_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_elixir")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_elixir")) : 0;
			if (cost_elixir != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-elixir") + ": " + _Number.showNumero(cost_elixir)));
			}
		}

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_dark_elixir") != null) {
			double cost_dark_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_dark_elixir")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_dark_elixir")) : 0;
			if (cost_dark_elixir != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-dark-elixir") + ": " + _Number.showNumero(cost_dark_elixir)));
			}
		}

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gold") != null) {
			double cost_gold = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gold")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gold")) : 0;
			if (cost_gold != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-gold") + ": " + _Number.showNumero(cost_gold)));
			}
		}
		return lista_lore;
	}

	public int getLevel() {
		return this.level;
	}

	/**
	 * Se il player ha la truppa comprata
	 * 
	 * @return
	 */
	public boolean hasPlayer() {
		if (pstored.hasTroop(type, 0)) {
			return true;
		}
		return false;
	}

	/**
	 * Lista dei costi
	 * 
	 * @return
	 */
	public boolean buy() {

		// Controllo se il laboratorio è del livello giusto
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".lab_level_requirement") != null) {

			int livello_lab_req = CraftOfClans.troops.getInt("troops." + type + ".levels." + level + ".lab_level_requirement");
			if (level_laboratory < livello_lab_req) {

				// Non ha il livello del laboratorio necessario
				pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.laboratory-required-upgrade").replace("%1%", livello_lab_req + "")));
				return false;
			}
		}

		double cost_gems = 0;
		double cost_elixir = 0;
		double cost_dark_elixir = 0;
		double cost_gold = 0;

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gems") != null) {
			cost_gems = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gems")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gems")) : 0;
			if (cost_gems != 0) {
				if (pstored.hasGems(cost_gems) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", _Number.showNumero(cost_gems)));
					return false;
				}
			}
		}
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_elixir") != null) {
			cost_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_elixir")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_elixir")) : 0;
			if (cost_elixir != 0) {
				if (pstored.hasElixir(cost_elixir) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", _Number.showNumero(cost_elixir)));
					return false;
				}
			}
		}
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_dark_elixir") != null) {
			cost_dark_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_dark_elixir")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_dark_elixir")) : 0;
			if (cost_dark_elixir != 0) {
				if (pstored.hasElixirNero(cost_dark_elixir) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", _Number.showNumero(cost_dark_elixir)));
					return false;
				}
			}
		}
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gold") != null) {
			cost_gold = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gold")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".cost_gold")) : 0;
			if (cost_gold != 0) {
				if (pstored.hasGold(cost_gold) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", _Number.showNumero(cost_gold)));
					return false;
				}
			}
		}

		// Rimuovo le risorse
		if (cost_gems > 0) {
			if (pstored.removeGems(cost_gems)) {
				cost_gems = 0;
			}
		}
		if (cost_elixir > 0) {
			if (pstored.removeElixir(cost_elixir)) {
				cost_elixir = 0;
			}
		}
		if (cost_gold > 0) {
			if (pstored.removeGold(cost_gold)) {
				cost_gold = 0;
			}
		}
		if (cost_dark_elixir > 0) {
			if (pstored.removeElixirNero(cost_dark_elixir)) {
				cost_dark_elixir = 0;
			}
		}

		if (cost_gems == 0 && cost_elixir == 0 && cost_gold == 0 && cost_dark_elixir == 0) {
			pstored.addTroop(type, level);

			pstored.get().playSound(pstored.get().getLocation(), Sounds.ORB_PICKUP.bukkitSound(), 1000, 1);

			return true;
		}

		return false;
	}

	/**
	 * Ultima ricarica fatta sulla truppa
	 * 
	 * @return
	 */
	public Date get_last_recharge() {
		return last_recharge;
	}

	/**
	 * Ultimo prelevo della truppa
	 * 
	 * @return
	 */
	public Date get_last_withdrawal() {
		return last_withdrawal;
	}

	public int getTimeTraining() {
		int tempo = CraftOfClans.troops.getInt("troops." + type + ".time-traning-based-level-barracks." + this.level_barracks);
		return tempo;
	}

	/**
	 * Tempo rimasto per fare la ricarica
	 * 
	 * @return
	 */
	public long getTime_remaining() {

		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(last_recharge);

		cal.add(Calendar.SECOND, getTimeTraining());
		Date later = cal.getTime();

		long seconds = (date.getTime() - later.getTime()) / 1000;
		seconds = Math.abs(seconds);

		return seconds;
	}

	/**
	 * Se bisogna riaddestrare
	 * 
	 * @return
	 */
	public boolean isToTraning() {
		if (last_recharge == null) {
			return true;
		}
		return false;
	}

	/**
	 * Se la truppa è pronta per essere ritirata
	 * 
	 * @return
	 */
	public boolean isReady() {
		if (last_recharge == null) {
			return false;
		}

		Date date = new Date();
		long seconds = (date.getTime() - last_recharge.getTime()) / 1000;

		if (seconds > getTimeTraining()) {
			return true;
		}
		return false;
	}

	/**
	 * Imposto la data di prelevo e rimuovo quella di ricarica
	 * 
	 * @return
	 */
	public boolean pickUp() {

		CraftOfClansData.get().set("villages." + pstored.getVillage().getIDescaped() + ".troops." + type + ".last_recharge", "0000-00-00 00:00:00");

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		CraftOfClansData.get().set("villages." + pstored.getVillage().getIDescaped() + ".troops." + type + ".last_withdrawal", dateFormat.format(date));
		CraftOfClansData.save();

		getPickItem();
		return true;
	}

	/**
	 * Creo la truppa che verrà usata negli attacchi
	 * 
	 * @return
	 */
	public boolean getPickItem() {

		if (pstored.get().getInventory().firstEmpty() == -1) {
			pstored.sendMessage(Color.message("&cYour inventory is full!"));
			return false;
		}

		ItemStack itemstack = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta item_meta = itemstack.getItemMeta();

		List<String> lore = new ArrayList<>();
		lore.add(Color.translate("&7Level: " + level));

		String villaggioid = pstored.getVillage().getOwnerName();
		lore.add(Color.translate("&7&o@Village: " + villaggioid));

		item_meta.setLore(lore);
		item_meta.setDisplayName(this.display_name);

		item_meta = SkullSkin.setSkullSkin(item_meta, type);
		itemstack.setItemMeta(item_meta);

		pstored.get().getInventory().addItem(itemstack);

		// Eseguo un suono
		pstored.get().playSound(pstored.get().getLocation(), Sounds.ITEM_PICKUP.bukkitSound(), 1000, 1);

		return true;
	}

	/**
	 * Imposto la data di addestramento
	 * 
	 * @return
	 */
	public boolean traning() {

		// Compro l'addestramento
		if (!traningBuy()) {
			return false;
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		CraftOfClansData.get().set("villages." + pstored.getVillage().getIDescaped() + ".troops." + type + ".last_recharge", dateFormat.format(date));
		CraftOfClansData.save();

		// Eseguo un suono
		pstored.get().playSound(pstored.get().getLocation(), Sounds.FIZZ.bukkitSound(), 1000, 1);

		return true;
	}

	/**
	 * Compro il training nuovo
	 * 
	 * @return
	 */
	public boolean traningBuy() {

		double cost_gems = 0;
		double cost_elixir = 0;
		double cost_dark_elixir = 0;
		double cost_gold = 0;

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gems") != null) {
			cost_gems = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gems")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gems")) : 0;
			if (cost_gems != 0) {
				if (pstored.hasGems(cost_gems) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", _Number.showNumero(cost_gems)));
					return false;
				}
			}
		}
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_elixir") != null) {
			cost_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_elixir")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_elixir")) : 0;
			if (cost_elixir != 0) {
				if (pstored.hasElixir(cost_elixir) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", _Number.showNumero(cost_elixir)));
					return false;
				}
			}
		}
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_dark_elixir") != null) {
			cost_dark_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_dark_elixir")) ? Double
					.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_dark_elixir")) : 0;
			if (cost_dark_elixir != 0) {
				if (pstored.hasElixirNero(cost_dark_elixir) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", _Number.showNumero(cost_dark_elixir)));
					return false;
				}
			}
		}
		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gold") != null) {
			cost_gold = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gold")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gold")) : 0;
			if (cost_gold != 0) {
				if (pstored.hasGold(cost_gold) == false) {
					pstored.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", _Number.showNumero(cost_gold)));
					return false;
				}
			}
		}

		// Rimuovo le risorse
		if (cost_gems > 0) {
			if (pstored.removeGems(cost_gems)) {
				cost_gems = 0;
			}
		}
		if (cost_elixir > 0) {
			if (pstored.removeElixir(cost_elixir)) {
				cost_elixir = 0;
			}
		}
		if (cost_gold > 0) {
			if (pstored.removeGold(cost_gold)) {
				cost_gold = 0;
			}
		}
		if (cost_dark_elixir > 0) {
			if (pstored.removeElixirNero(cost_dark_elixir)) {
				cost_dark_elixir = 0;
			}
		}

		if (cost_gems == 0 && cost_elixir == 0 && cost_gold == 0 && cost_dark_elixir == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Costi di addestramento
	 * 
	 * @return
	 */
	public ArrayList<String> getCostTraningList() {
		ArrayList<String> lista_lore = new ArrayList<>();

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gems") != null) {
			double cost_gems = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gems")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gems")) : 0;
			if (cost_gems != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-gems") + ": " + _Number.showNumero(cost_gems)));
			}
		}

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_elixir") != null) {
			double cost_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_elixir")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_elixir")) : 0;
			if (cost_elixir != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-elixir") + ": " + _Number.showNumero(cost_elixir)));
			}
		}

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_dark_elixir") != null) {
			double cost_dark_elixir = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_dark_elixir")) ? Double
					.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_dark_elixir")) : 0;
			if (cost_dark_elixir != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-dark-elixir") + ": " + _Number.showNumero(cost_dark_elixir)));
			}
		}

		if (CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gold") != null) {
			double cost_gold = _Number.isNumero(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gold")) ? Double.parseDouble(CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".training.cost_gold")) : 0;
			if (cost_gold != 0) {
				lista_lore.add(Color.translate(CraftOfClansM.getString("tiers.cost-gold") + ": " + _Number.showNumero(cost_gold)));
			}
		}
		return lista_lore;
	}

	/**
	 * Prendo gli NPC da spwnare sulla mappa durante l'attacco
	 * 
	 * @return
	 */
	public ArrayList<NPC> getNPCs() {
		ArrayList<NPC> lista = new ArrayList<>();

		boolean active = CraftOfClans.troops.getBoolean("troops." + type + ".enable");
		if (!active) {
			return lista;
		}

		int number = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".spawn-number") != null ? CraftOfClans.troops.getInt("troops." + type + ".levels." + level + ".spawn-number") : 1;
		String mob_type = CraftOfClans.troops.getString("troops." + type + ".type_mob"); // es. PLAYER

		for (int i = 0; i < number; i++) {
			
			NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.valueOf(mob_type), display_name);
			
			// Imposto l'armatura
			String boots = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".equipment.boots");
			String chestplate = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".equipment.chestplate");
			String helmet = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".equipment.helmet");
			String leggings = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".equipment.leggings");
			String hand = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".equipment.hand");
			
			if (boots != null) {
				ItemStack boots_item = new ItemStack(Material.getMaterial(boots));
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.BOOTS, boots_item);
			}
			if (chestplate != null) {
				ItemStack chestplate_item = new ItemStack(Material.getMaterial(chestplate));
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, chestplate_item);
			}
			if (helmet != null) {
				ItemStack helmet_item = new ItemStack(Material.getMaterial(helmet));
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HELMET, helmet_item);
			}
			if (leggings != null) {
				ItemStack leggings_item = new ItemStack(Material.getMaterial(leggings));
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, leggings_item);
			}
			if (hand != null) {
				ItemStack hand_item = new ItemStack(Material.getMaterial(hand));
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND, hand_item);
			}
			
			// Imposto la skin se esiste
			NPCSkin.getSkinFromConfig(npc, type);

			lista.add(npc);
		}

		return lista;
	}
	
	/**
	 * Danno personalizzato della truppa spawnata
	 * 
	 * @return
	 */
	public double getDamageValue() {
		String damage = CraftOfClans.troops.getString("troops." + type + ".levels." + level + ".damage");
		if(damage != null) {
			// Se ha impostato 'auto'
			if(!_Number.isNumero(damage)) {
				return -1;
			}
			return Double.parseDouble(damage);
		}
		return -1;
	}
}
