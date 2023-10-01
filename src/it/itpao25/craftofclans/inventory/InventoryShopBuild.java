package it.itpao25.craftofclans.inventory;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.structures.StructuresEnum;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;

import org.bukkit.inventory.ItemStack;

public class InventoryShopBuild {

	private String title = null;
	private String string_name = null;
	
	private StructuresEnum tipo;
	private PlayerStored p;

	public InventoryShopBuild(ItemStack item, PlayerStored p) {
		this.title = item.getItemMeta().getDisplayName();
		this.p = p;
		setAction();
	}

	public InventoryShopBuild(String item, PlayerStored p) {
		this.string_name = item.toUpperCase();
		this.p = p;
		setAction();
	}
	
	/**
	 * Imposto l'azione della GUI
	 * 
	 * @return
	 */
	private boolean setAction() {
		
		// Se viene dall'item
		if (this.title != null) {
			for (String string : CraftOfClans.config.get().getConfigurationSection("shop.structures-gui").getKeys(false)) {
				if (CraftOfClans.config.getString("shop.structures-gui." + string + ".title") != null) {
					String current = Color.translate(CraftOfClans.config.getString("shop.structures-gui." + string + ".title"));
					if (current.equals(title)) {
						for (StructuresEnum senum : StructuresEnum.values()) {
							if (senum.toString().equals(string.toString())) {
								this.tipo = senum;
							}
						}
	
						String decoration = CraftOfClans.config.getString("shop.structures-gui." + string + ".decoration") != null ? CraftOfClans.config.getString("shop.structures-gui." + string + ".decoration") : null;
						if (decoration != null) {
							// E' una decorazione
							this.tipo = StructuresEnum.DECORATION.setType(string);
						}
					}
				}
			}
		}
		
		// Se viene direttamente dal nome della struttura /coc shop buy <nome struttura>
		if(this.string_name != null) {
			if (CraftOfClans.config.getString("shop.structures-gui." + this.string_name + ".title") != null) {
				for (StructuresEnum senum : StructuresEnum.values()) {
					if (senum.toString().equals(this.string_name)) {
						this.tipo = senum;
					}
				}
				
				String decoration = CraftOfClans.config.getString("shop.structures-gui." + this.string_name + ".decoration") != null ? CraftOfClans.config.getString("shop.structures-gui." + this.string_name + ".decoration") : null;
				if (decoration != null) {
					// E' una decorazione
					this.tipo = StructuresEnum.DECORATION.setType(this.string_name);
				}
			}
		}
		
		return false;
	}

	/**
	 * Preparo l'item che deve essere dato al giocatore
	 * 
	 * @return
	 */
	public boolean getItemResponse() {
		if (this.tipo == null) {
			return false;
		}

		// Se è una struttura normale o è una decorazione
		String tipo;
		if (!this.tipo.toString().equals(StructuresEnum.DECORATION.toString())) {
			tipo = this.tipo.toString();
		} else {
			tipo = this.tipo.type_struttura;
		}

		// Controllo se può comprare questa struttura
		if (p.hasVillage()) {
			
			// Se ha il municipio a livello giusto
			VillageId id = p.getVillage();
			String config = CraftOfClans.config.getString("shop.structures-core." + tipo + ".levels.1.townhall-required");
			if (config != null) {
				int require_townhall = CraftOfClans.config.getInt("shop.structures-core." + tipo + ".levels.1.townhall-required");
				if (id.getLevelTownHall() < require_townhall) {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.townhall-required-upgrade")).replace("%1%", require_townhall + ""));
					return false;
				}
			}
			
			// Se può costruire altre strutture
			int max_structures = CraftOfClans.config.getString("shop.structures-core." + tipo + ".max-for-eachvillage") != null ? CraftOfClans.config.getInt("shop.structures-core." + tipo + ".max-for-eachvillage") : 0;
			if (max_structures != 0) {
				int numero1 = MapInfo.getStructuresAtVillageByType(tipo, id);
				if (numero1 >= max_structures) {
					p.sendMessage(Color.message(CraftOfClansM.getString("messages.shop-max-structures-reach").replace("%1%", max_structures + "")));
					return false;
				}
			}
			
			// Se esistono limiti in base al livello del municipio
			if(CraftOfClans.config.getString("shop.structures-core." + tipo + ".limit-based-townhall-level") != null) {
				int villo_lvl = p.getVillage().getLevelTownHall();
				int numero1 = MapInfo.getStructuresAtVillageByType(tipo, id);
				
				for(String key : CraftOfClans.config.get().getConfigurationSection("shop.structures-core." + tipo + ".limit-based-townhall-level").getKeys(false)) {
					if(key.equals(villo_lvl + "")) {
						int limit_townontownhall = Integer.parseInt(CraftOfClans.config.getString("shop.structures-core." + tipo + ".limit-based-townhall-level."+ key));
						if (numero1 >= limit_townontownhall) {
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.townhall-required-upgrade-toupdate")));
							return false;
						}
					}
				}
			}
		}

		String costo = checkResource(tipo);

		if (costo == null) {
			return false;
		}

		// Do al giocatore l'oggetto
		new InventoryShopToPlayer(p.get(), tipo, 1);
		
		// Invio il messaggio dell'acquisto con successo
		p.sendMessage(Color.message(CraftOfClansM.getString("messages.bought-structure-successfully").replace("%1%", costo)));
		return true;
	}

	/**
	 * Controllo le risorse
	 * 
	 * @return
	 */
	public String checkResource(String item) {
		double cost_gems = 0;
		double cost_elixir = 0;
		double cost_gold = 0;
		double cost_dark_elixir = 0;

		for (String current : CraftOfClans.config.get().getConfigurationSection("shop.structures-core").getKeys(false)) {
			if (item.toString().equals(current)) {
				cost_gems = CraftOfClans.config.getString("shop.structures-core." + current + ".levels.1.cost_gems") != null ? CraftOfClans.config.getDouble("shop.structures-core." + current + ".levels.1.cost_gems") : 0;
				cost_elixir = CraftOfClans.config.getString("shop.structures-core." + current + ".levels.1.cost_elixir") != null ? CraftOfClans.config.getDouble("shop.structures-core." + current + ".levels.1.cost_elixir") : 0;
				cost_gold = CraftOfClans.config.getString("shop.structures-core." + current + ".levels.1.cost_gold") != null ? CraftOfClans.config.getDouble("shop.structures-core." + current + ".levels.1.cost_gold") : 0;
				cost_dark_elixir = CraftOfClans.config.getString("shop.structures-core." + current + ".levels.1.cost_dark_elixir") != null ? CraftOfClans.config.getDouble("shop.structures-core." + current + ".levels.1.cost_dark_elixir") : 0;
			}
		}

		if (cost_gems > 0) {
			if (p.hasGems(cost_gems) == false) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.nogems")).replace("%1%", _Number.showNumero(cost_gems)));
				return null;
			}
		}
		if (cost_elixir > 0) {
			if (p.hasElixir(cost_elixir) == false) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.noelixir")).replace("%1%", _Number.showNumero(cost_elixir)));
				return null;
			}
		}

		if (cost_gold > 0) {
			if (p.hasGold(cost_gold) == false) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.nogold")).replace("%1%", _Number.showNumero(cost_gold)));
				return null;
			}
		}

		if (cost_dark_elixir > 0) {
			if (p.hasElixirNero(cost_dark_elixir) == false) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.nodark-elixir")).replace("%1%", _Number.showNumero(cost_dark_elixir)));
				return null;
			}
		}

		String costo = "";
		// Rimuovo le risorse
		if (cost_gems > 0) {
			if (p.removeGems(cost_gems)) {
				costo += _Number.showNumero(cost_gems) + " gems";
			}
		}
		if (cost_elixir > 0) {
			if (p.removeElixir(cost_elixir)) {
				costo += _Number.showNumero(cost_elixir) + " elixir";
			}
		}
		if (cost_gold > 0) {
			if (p.removeGold(cost_gold)) {
				costo += _Number.showNumero(cost_gold) + " gold";
			}
		}
		if (cost_dark_elixir > 0) {
			if (p.removeElixirNero(cost_dark_elixir)) {
				costo += _Number.showNumero(cost_dark_elixir) + " dark elixir";
			}
		}
		return costo;
	}
}