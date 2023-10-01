package it.itpao25.craftofclans.holo;

import java.util.HashMap;
import java.util.Map.Entry;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.ResourceCollected;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class HolographicListner implements Listener {

	public static HashMap<StructuresId, Hologram> lista_holo = new HashMap<>();

	public static String str_lvl;

	public HolographicListner() {
		str_lvl = CraftOfClans.config.getString("holographic-display.level-text") != null ? CraftOfClans.config.getString("holographic-display.level-text") : "&6LVL &6&l";
	}

	@EventHandler
	public void onUpdateResource(ResourceCollected event) {
		// Controilo l'impostazione nel config è settata su true
		if (!CraftOfClans.isHolographicDisplay) {
			return;
		}

		// Se è già presente nella lista
		if (lista_holo.containsKey(event.getStructure())) {
			Hologram holo = lista_holo.get(event.getStructure());
			holo.delete();
		}

		StructuresId struttura = event.getStructure();
		Location loc = struttura.getCuboid().corners()[0].getLocation();
		loc.setY(VillageSettings.getHeight(event.getStructure().getVillage()) + 1.80);
		loc.setZ(loc.getZ() + 0.50);
		loc.setX(loc.getX() + 1.50);
		
		double valore = struttura.getResources();
		Hologram holo_nuovo = HologramsAPI.createHologram(CraftOfClans.getInstance(), loc);
		holo_nuovo.appendTextLine(Color.translate(struttura.getName()));
		holo_nuovo.appendTextLine(Color.translate(str_lvl + struttura.getLevel()));

		// Rosso se è pieno
		String colore_holo = "&6";
		if (valore >= struttura.getCapacity()) {
			colore_holo = "&4";
		}

		if (struttura.getCapacity() != 0) {
			holo_nuovo.appendTextLine(Color.translate(colore_holo + "" + _Number.showNumero(valore) + " &c/ " + colore_holo + _Number.showNumero(struttura.getCapacity())));
		} else {
			holo_nuovo.appendTextLine(Color.translate("&6" + _Number.showNumero(valore)));
		}
		lista_holo.put(event.getStructure(), holo_nuovo);

		// Cerco gli storage di quel villaggio
		storageHolo(struttura.getVillage());
	}

	public static void removeOnStructure(StructuresId id) {
		if (lista_holo.containsKey(id)) {
			Hologram holo = lista_holo.get(id);
			holo.delete();
			lista_holo.remove(id);
		}
	}

	/**
	 * Cerco le strutture per quel villaggio
	 * 
	 * @param village
	 */
	public static void storageHolo(VillageId village) {

		for (Entry<StructuresId, String> struct : village.getStructuresList().entrySet()) {
			String tipo = struct.getKey().getType();

			if (tipo.equals("GOLD_STORAGE") || tipo.equals("DARK_ELIXIR_STORAGE") || tipo.equals("ELIXIR_STORAGE")) {

				// Se è già presente nella lista
				if (lista_holo.containsKey(struct.getKey())) {
					Hologram holo = lista_holo.get(struct.getKey());
					holo.delete();
				}

				StructuresId struttura = struct.getKey();
				Location loc = struttura.getCuboid().corners()[0].getLocation();
				loc.setY(VillageSettings.getHeight(village) + 1.80);
				loc.setZ(loc.getZ() + 0.50);
				loc.setX(loc.getX() + 1.50);

				double valore = struttura.getCapacity();

				Hologram holo_nuovo = HologramsAPI.createHologram(CraftOfClans.getInstance(), loc);
				holo_nuovo.appendTextLine(Color.translate(struttura.getName()));
				holo_nuovo.appendTextLine(Color.translate(str_lvl + struttura.getLevel()));
				
				// Se lo storage è pieno
				String colore = "&6";
				if (struttura.getResourcesInside() >= valore) {
					colore = "&4";
					String full = Color.translate(CraftOfClansM.getString("messages.structure-full-resources"));
					holo_nuovo.appendTextLine(full);
				}

				holo_nuovo.appendTextLine(Color.translate(colore + "" + _Number.showNumero(struttura.getResourcesInside()) + " &c/ " + colore + _Number.showNumero(valore)));

				lista_holo.put(struct.getKey(), holo_nuovo);
			}
		}

	}
}
