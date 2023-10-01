package it.itpao25.craftofclans.holo;

import java.util.ArrayList;
import java.util.List;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.worldmanager.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.PickupHandler;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

public class HolographicUtil {

	public static List<Hologram> holo_death_attack = new ArrayList<>();

	public static boolean creaCassaMorto(Player player, Location loc, final ItemStack[] drops, VillageId villo) {

		loc = loc.add(0.0, 2.0, 0.0);
		ItemStack itemStack = new ItemStack(Material.CHEST);

		final Hologram hologram = HologramsAPI.createHologram(CraftOfClans.getInstance(), loc);

		List<String> list_lore = CraftOfClans.config.get().getStringList("holographic-display.attack-chest-death-player-lore");
		for (int c = 0; c <= list_lore.size() - 1; c++) {
			String current = list_lore.get(c);
			current = current.replace("%1%", player.getName());
			current = current.replace("%2%", "");
			hologram.appendTextLine(Color.translate(current));
		}

		final String villo_own = villo.getOwnerName();

		ItemLine linea = hologram.appendItemLine(itemStack);
		linea.setPickupHandler(new PickupHandler() {
			@Override
			public void onPickup(Player player) {
				// Controllo se il player è il proprietario del villaggio
				if (!player.getName().equals(villo_own))
					return;

				player.getInventory().addItem(drops);
				player.playEffect(hologram.getLocation(), Effect.MOBSPAWNER_FLAMES, null);
				player.playSound(player.getLocation(), Sounds.LEVEL_UP.bukkitSound(), 1F, 2F);
				hologram.delete();
				player.sendMessage(Color.message(CraftOfClansM.getString("messages.attack-player-death-pickinventary").replace("%1%", player.getName())));
			}
		});

		holo_death_attack.add(hologram);
		return true;
	}
}
