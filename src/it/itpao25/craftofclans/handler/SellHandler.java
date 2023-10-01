package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.ResourceChangeValue;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * Gestione della vendita degli item
 * 
 * @author itpao25
 *
 */
public class SellHandler {

	private ArrayList<ItemStack> items;
	private PlayerStored p;

	public SellHandler(ArrayList<ItemStack> items) {
		this.items = items;
	}

	public SellHandler(ArrayList<ItemStack> item_final2, PlayerStored p) {
		this.items = item_final2;
		this.p = p;
	}

	public boolean registerPremi() {
		double final_cost = 0;

		List<String> list = CraftOfClans.config.get().getStringList("sell.price");
		for (String price : list) {
			if (price.contains(":") == false) {
				LogHandler.error("The price of sell " + price + " is not valid!");
				return false;
			}
			String[] result_price = price.split(":");
			int amount = Integer.parseInt(result_price[0]);
			String name = result_price[1];
			double gems = Double.parseDouble(result_price[2]);

			for (ItemStack entry : items) {
				if (entry.getType().name().equals(name)) {
					int total = entry.getAmount();
					// Non è uguale al numero indicato sul config
					if (total != amount) {
						
						// Controllo se il totale è divisibile per l'amount indicato
						if (total % amount == 0) {
							
							int divisi = 0;
							int da_dividere = total / amount;
							for(int i = 0; i < da_dividere; i++) {
								final_cost = final_cost + gems;
								divisi++;
							}
							
							if(divisi != total) {
								// Ridò al player parte che non è stata divisa
								ItemStack item_dadare = new ItemStack(entry.clone());
								item_dadare.setAmount(total - divisi);
								p.get().getInventory().addItem(item_dadare);
							}
							
						} else if (total > amount) {
							
							// Non è divisibile allora provo se è minore del valore del config
							int da_ridare = total - amount;
							
							// Aggiungo il prezzo 
							final_cost = final_cost + gems;
							
							// Ridò al player parte che non è stata venduta
							ItemStack item_dadare = new ItemStack(entry.clone());
							item_dadare.setAmount(da_ridare);
							p.get().getInventory().addItem(item_dadare);
							
						} else {
							// Ridò al player gli item
							p.get().getInventory().addItem(entry);
							p.get().sendMessage(Color.message(CraftOfClansM.getString("messages.sell-returned-item-amount").replace("%2%", amount + "").replace("%1%", entry.getType().name())));
						}
						continue;
					}
					// Se mette più stack insieme devo calcolarlo
					final_cost = final_cost + gems;
				}
			}
		}
		if (p != null) {
			p.addGems(final_cost);

			ResourceChangeValue event = new ResourceChangeValue(p.get());
			Bukkit.getServer().getPluginManager().callEvent(event);

			p.sendMessage(Color.message(CraftOfClansM.getString("messages.sell-success")).replace("%1%", final_cost + ""));
		}
		return false;
	}
}
