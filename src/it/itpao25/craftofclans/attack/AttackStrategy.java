package it.itpao25.craftofclans.attack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mcmonkey.sentinel.SentinelTrait;

import com.google.common.collect.Lists;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.ExplodeEffect;
import de.slikey.effectlib.effect.LineEffect;
import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.EffettoMortaio;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.village.VillageCuboid.CuboidDirection;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.Age;

public class AttackStrategy implements Runnable {

	private static int level;
	private static Attack attacco;

	public AttackStrategy(VillageId attacco) {
		AttackStrategy.attacco = attacco.getAttack();
	}

	@Override
	public void run() {

		// Giocatore che ha avviato l'attacco
		Player player_own_attack = attacco.p.get();

		// Gestione strutture
		Iterator<Entry<NPC, StructuresId>> it = AttackListener.difese.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry<NPC, StructuresId> id = (Map.Entry<NPC, StructuresId>) it.next();

			if (attacco.hasdisableDStructure(id.getValue())) {
				continue;
			}

			level = Integer.parseInt(id.getValue().getDataCustom(id.getValue().getLevel(), "damage-range"));
			Entity npc = id.getKey().getEntity();

			// Lista dell'entità più vicina
			Entity entita = PlayerNear(id.getValue(), npc);
			
			if (entita != null) {
				
				if (CitizensAPI.getNPCRegistry().isNPC(entita)) {
					NPC npc_target = CitizensAPI.getNPCRegistry().getNPC(entita);
					// Solo le truppe spawnate dall'avversario possono essere attaccate
					if (!AttackListener.truppe_sentinel.containsKey(npc_target)) {
						continue;
					}
				}
				
				// Torre degli arceri
				if (id.getValue().getType().equals("ARCHER_TOWER") && npc instanceof Player) {
					attackPlayerArrow(npc, entita, id.getValue());
				}

				// Torre dello stregone
				if (id.getValue().getType().equals("WIZARD_TOWER") && npc instanceof Player) {
					attackPlayerBomb(npc, entita, id.getValue());
				}

				// Cannone
				if (id.getValue().getType().equals("CANNON")) {
					attackPlayerBomb(npc, entita, id.getValue());
				}

				// Mortaio
				if (id.getValue().getType().equals("MORTAR")) {
					attackPlayerMortar(npc, entita, id.getValue());
				}

				// TESLA
				if (id.getValue().getType().equals("TESLA")) {
					TeslaAppare(npc, entita, id.getValue(), player_own_attack);
				}

				// BOMBA
				if (id.getValue().getType().equals("BOMB")) {
					BombaEsplode(npc, entita, id.getValue(), player_own_attack);
				}

				// Trappola degli scheletri
				if (id.getValue().getType().equals("SKELETON_TRAP")) {
					SkeletonTrap(npc, entita, id.getValue(), player_own_attack);
				}
			}
		}
	}

	/**
	 * Prendo l'entità più vicina
	 * 
	 * @param id
	 * @param ent
	 * @return
	 */
	public static Entity PlayerNear(StructuresId id, Entity ent) {
		if (ent == null)
			return null;
		
		Entity risultato = null;
		double lastDistance = Double.MAX_VALUE;
		
		List<Entity> vicini = ent.getNearbyEntities(level, level, level);
		
		for (Entity questo : vicini) {
			if ((questo instanceof Player)) {
			    double distance = ent.getLocation().distance(questo.getLocation());
			    if(distance < lastDistance) {
			    	lastDistance = distance;
			    	risultato = questo;
			    }
			}
		}
		return risultato;
	}

	/**
	 * Lancia le frecce
	 */
	private static boolean attackPlayerArrow(Entity npc, Entity entity_target, StructuresId id) {

		final Location location_player = entity_target.getLocation();
		final float danno = Float.parseFloat((id.getDataCustom(id.getLevel(), "damage-arrow")));

		EffectManager em = new EffectManager(CraftOfClans.getInstance());
		LineEffect effect = new LineEffect(em);
		effect.setEntity(npc);
		effect.setTargetLocation(location_player);
		effect.particle = Particle.CLOUD;
		effect.callback = new Runnable() {
			@Override
			public void run() {

				if (entity_target.getLocation().distance(location_player) < 1) {
					
					if (CitizensAPI.getNPCRegistry().isNPC(entity_target)) {
						// Attacco l'npc truppa
						NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity_target);
						if (AttackListener.truppe_sentinel.containsKey(npc)) {
							if (npc.isSpawned()) {
								npc.getOrAddTrait(SentinelTrait.class).getLivingEntity().damage(danno);
							}
						}

					} else if (entity_target instanceof Player) {
						// Attacco il player
						((Player) entity_target).damage(danno);

						// Eseguo degli effetti dalla struttura
						attackPlayerEffect(((Player) entity_target), id);
					}
				}
			}
		};
		effect.start();
		return true;
	}

	/**
	 * Spawna bomba
	 */
	private static boolean attackPlayerBomb(Entity npc, Entity entity_target, StructuresId id) {

		final Location location_player = entity_target.getLocation();
		location_player.add(0, 1, 0); // Punto in testa

		final float danno = Float.parseFloat((id.getDataCustom(id.getLevel(), "damage-bomb")));

		EffectManager em = new EffectManager(CraftOfClans.getInstance());
		LineEffect effect = new LineEffect(em);
		effect.setEntity(npc);
		effect.setTargetLocation(location_player);
		effect.particle = Particle.FLAME;
		effect.callback = new Runnable() {
			@Override
			public void run() {
				if (entity_target.getLocation().distance(location_player) < 1) {

					if (CitizensAPI.getNPCRegistry().isNPC(entity_target)) {
						// Attacco l'npc truppa
						NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity_target);
						if (AttackListener.truppe_sentinel.containsKey(npc)) {
							if (npc.isSpawned()) {
								npc.getOrAddTrait(SentinelTrait.class).getLivingEntity().damage(danno);
							}
						}

					} else if (entity_target instanceof Player) {
						// Attacco il player
						((Player) entity_target).damage(danno);

						// Eseguo degli effetti dalla struttura
						attackPlayerEffect(((Player) entity_target), id);
					}
				}
			}
		};
		effect.start();
		return true;
	}

	/**
	 * Attacco con il mortaio
	 */
	private static boolean attackPlayerMortar(Entity npc, final Entity entity_target, StructuresId id) {

		// Il giocatore si può spostare e quindi devo segnarmi la posizione dov'è
		// attualmente
		final Location location_player = entity_target.getLocation();

		EffectManager em = new EffectManager(CraftOfClans.getInstance());
		EffettoMortaio effect = new EffettoMortaio(em, npc.getLocation(), entity_target.getLocation());
		effect.setEntity(npc);
		effect.setTargetLocation(location_player);

		final float danno = Float.parseFloat((id.getDataCustom(id.getLevel(), "damage-bomb")));
		effect.callback = new Runnable() {
			@Override
			public void run() {
				entity_target.getWorld().createExplosion(location_player.getX(), location_player.getY(), location_player.getZ(), danno, false, false);
			}
		};
		effect.start();

		return true;
	}

	/**
	 * Effetti per la difesa
	 * 
	 * @param p
	 * @param id
	 * @return
	 */
	private static boolean attackPlayerEffect(Player p, StructuresId id) {
		if (id.getDataCustom(id.getLevel(), "effect") != null) {
			List<String> list = id.getListDataCustom(id.getLevel(), "effect");
			for (String current : list) {
				// FORMATO <NOME EFFECT> <DURATA> <AMPLIAMENTO>
				String[] current_format = current.split("\\s+");
				String nome = current_format[0] != null ? current_format[0] : null;
				int durata = current_format[1] != null && _Number.isNumero(current_format[1]) ? Integer.parseInt(current_format[1]) : -1;
				int ampliamento = current_format[2] != null && _Number.isNumero(current_format[2]) ? Integer.parseInt(current_format[2]) : -1;
				PotionEffectType effect = null;
				if (nome != null) {
					try {
						effect = PotionEffectType.getByName(nome);
					} catch (Exception ex) {
						ex.printStackTrace();
						break;
					}
					p.addPotionEffect(new PotionEffect(effect, durata, ampliamento));
				}
			}
		}
		return true;
	}

	/**
	 * Spawno una tesla e appare
	 * 
	 * @return
	 */
	private static boolean TeslaAppare(Entity npc, final Entity entity_target, StructuresId id, Player player_own_attack) {

		// Prendo i blocchi e le rendo visibili per il giocatore
		ArrayList<Block> blocchi = Lists.newArrayList(id.getCuboid().iterator());
		for (Block block : blocchi) {
			if (block.getY() > VillageSettings.getHeight(id.getVillage()) - 1) {
				BlockData materiale = block.getWorld().getBlockAt(block.getLocation()).getBlockData();
				player_own_attack.sendBlockChange(block.getLocation(), materiale);
			}
		}

		// Imposto visibili i blocchi di disattivazione
		if (AttackerManager.battles_blocks.containsKey(id)) {
			HashMap<ArrayList<Block>, Integer> result = AttackerManager.battles_blocks.get(id);
			for (Entry<ArrayList<Block>, Integer> id1 : result.entrySet()) {
				ArrayList<Block> blocks = id1.getKey();
				for (Block object : blocks) {
					BlockData materiale = object.getWorld().getBlockAt(object.getLocation()).getBlockData();
					player_own_attack.sendBlockChange(object.getLocation(), materiale);
				}
			}
		}

		// Il giocatore si può spostare e quindi devo segnarmi la posizione dov'è
		// attualmente
		final Location location_player = entity_target.getLocation();
		final float danno = Float.parseFloat((id.getDataCustom(id.getLevel(), "damage-bomb")));

		EffectManager em = new EffectManager(CraftOfClans.getInstance());
		LineEffect effect = new LineEffect(em);
		effect.setEntity(npc);
		effect.setTargetLocation(location_player);
		effect.particle = Particle.SPELL;
		effect.callback = new Runnable() {
			@Override
			public void run() {
				
				if (entity_target.getLocation().distance(location_player) < 1) {
					
					if (CitizensAPI.getNPCRegistry().isNPC(entity_target)) {
						// Attacco l'npc truppa
						NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity_target);
						if (AttackListener.truppe_sentinel.containsKey(npc)) {
							if (npc.isSpawned()) {
								npc.getOrAddTrait(SentinelTrait.class).getLivingEntity().damage(danno);
							}
						}

					} else if (entity_target instanceof Player) {
						// Attacco il player
						((Player) entity_target).damage(danno);

						// Eseguo degli effetti dalla struttura
						attackPlayerEffect(((Player) entity_target), id);
					}
				}
			}
		};
		effect.start();

		return false;
	}

	/**
	 * Esplode la bomba
	 * 
	 * @return
	 */
	private static boolean BombaEsplode(Entity npc, final Entity entity_target, StructuresId id, Player player_own_attack) {

		// Prendo i blocchi e le rendo invisibili per il giocatore
		ArrayList<Block> blocchi = Lists.newArrayList(id.getCuboid().iterator());
		for (Block block : blocchi) {
			if (block.getY() > VillageSettings.getHeight(id.getVillage()) - 1) {
				BlockData materiale = block.getWorld().getBlockAt(block.getLocation()).getBlockData();
				player_own_attack.sendBlockChange(block.getLocation(), materiale);
			}
		}

		// Il giocatore si può spostare e quindi devo segnarmi la posizione dov'è
		// attualmente
		final Location location_player = entity_target.getLocation();
		final float danno = Float.parseFloat((id.getDataCustom(id.getLevel(), "damage-bomb")));

		location_player.getWorld().playSound(location_player, Sound.ENTITY_CREEPER_PRIMED, 1, 0);

		EffectManager em = new EffectManager(CraftOfClans.getInstance());
		ExplodeEffect effect = new ExplodeEffect(em);
		effect.setEntity(npc);
		effect.setTargetLocation(location_player);
		effect.callback = new Runnable() {
			@Override
			public void run() {
				
				if (entity_target.getLocation().distance(location_player) < 1.75) {
					
					if (CitizensAPI.getNPCRegistry().isNPC(entity_target)) {
						// Attacco l'npc truppa
						NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity_target);
						if (AttackListener.truppe_sentinel.containsKey(npc)) {
							if (npc.isSpawned()) {
								npc.getOrAddTrait(SentinelTrait.class).getLivingEntity().damage(danno);
							}
						}

					} else if (entity_target instanceof Player) {
						// Attacco il player
						((Player) entity_target).damage(danno);

						// Eseguo degli effetti dalla struttura
						attackPlayerEffect(((Player) entity_target), id);
					}
				}

				// Rinascondo la bomba
				ArrayList<Block> blocchi = Lists.newArrayList(id.getCuboid().iterator());
				for (Block block : blocchi) {
					if (block.getY() > VillageSettings.getHeight(id.getVillage()) - 1) {
						player_own_attack.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
					}
				}
			}
		};
		effect.start();

		attacco.disableStructure(id);

		return false;
	}

	/**
	 * Trappola degli scheletri
	 * 
	 * @return
	 */
	private static boolean SkeletonTrap(Entity npc, final Entity entity_target, StructuresId id, Player player_own_attack) {

		Location loc = id.getCuboid().getFace(CuboidDirection.Up).getCenter().getBlock().getLocation().add(+0.5, 0, -0.5);

		int numero_spawn = Integer.parseInt((id.getDataCustom(id.getLevel(), "defense-spawn")));

		String nome_type = id.getDataCustom(id.getLevel(), "spawn-type");
		String age = id.getDataCustom(id.getLevel(), "spawn-type-age");
		int ageVal = age.equalsIgnoreCase("baby") ? -24000 : 0;

		EffectManager em = new EffectManager(CraftOfClans.getInstance());
		ExplodeEffect effect = new ExplodeEffect(em);
		effect.setEntity(npc);
		effect.setTargetLocation(loc);
		effect.start();

		for (int i = 0; i < numero_spawn; i++) {

			NPC npc_defense = CitizensAPI.getNPCRegistry().createNPC(EntityType.valueOf(nome_type), id.getName());

			npc_defense.getOrAddTrait(Age.class).setAge(ageVal);
			npc_defense.getOrAddTrait(SentinelTrait.class).addTarget("uuid:" + player_own_attack.getUniqueId());
			npc_defense.getOrAddTrait(SentinelTrait.class).squad = "difese";

			npc_defense.getOrAddTrait(SentinelTrait.class).respawnTime = 0;

			// Imposto l'armatura
			String boots = id.getDataCustom(id.getLevel(), "spawn-equipment.boots");
			String chestplate = id.getDataCustom(id.getLevel(), "spawn-equipment.chestplate");
			String helmet = id.getDataCustom(id.getLevel(), "spawn-equipment.helmet");
			String leggings = id.getDataCustom(id.getLevel(), "spawn-equipment.leggings");
			String hand = id.getDataCustom(id.getLevel(), "spawn-equipment.hand");

			npc_defense.getOrAddTrait(Equipment.class).set(EquipmentSlot.BOOTS, new ItemStack(Material.getMaterial(boots), 1));
			npc_defense.getOrAddTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, new ItemStack(Material.getMaterial(chestplate), 1));
			npc_defense.getOrAddTrait(Equipment.class).set(EquipmentSlot.HELMET, new ItemStack(Material.getMaterial(helmet), 1));
			npc_defense.getOrAddTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, new ItemStack(Material.getMaterial(leggings), 1));
			npc_defense.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND, new ItemStack(Material.getMaterial(hand), 1));

			npc_defense.setProtected(false);
			npc_defense.spawn(loc);

			AttackListener.difese_sentinel.put(npc_defense, id);
		}

		attacco.disableStructure(id);

		return true;
	}
}
