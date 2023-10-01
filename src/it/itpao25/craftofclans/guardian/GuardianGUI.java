package it.itpao25.craftofclans.guardian;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;

public class GuardianGUI {

	private String title;
	private String type;
	private PlayerStored p;
	private GuardianVillage id;
	private boolean response = false;

	public GuardianGUI(String title, GuardianVillage id, PlayerStored p) {
		this.title = title;
		this.id = id;
		this.p = p;
		initd();
		action();
	}

	private boolean initd() {
		for (String key : CraftOfClans.config.get().getConfigurationSection("guardian-gui").getKeys(false)) {
			if (CraftOfClans.config.get().getConfigurationSection("guardian-gui." + key) == null)
				continue;
			String slot = Color.translate(CraftOfClans.config.get().getString("guardian-gui." + key + ".title"));
			if (this.title.equals(slot)) {
				this.type = CraftOfClans.config.get().getString("guardian-gui." + key + ".type");
			}
		}
		return true;
	}

	/**
	 * Eseguo l'azione
	 * 
	 * @return
	 */
	private boolean action() {

		// Controllo il tipo di oggetto
		if (this.type == null)
			return false;

		if (this.type.equals("INFO_TOWNHALL")) {
			
			p.get().sendMessage("");
			for (String string : id.getVillage().info()) {
				p.get().sendMessage(string);
			}
			this.response = true;

		} else if (this.type.equals("VIEW_INFO")) {
			// Nothing
			
		} else if (this.type.equals("UPGRADE")) {
			
			GuardianUpgrade gupgrade = new GuardianUpgrade(id, p);
			this.response = gupgrade.getResponse();
			
		} else if (this.type.equals("MOVE")) {
			id.requestToMove(p.get());
			this.response = true;
		}
		return false;
	}

	public boolean getResponse() {
		return this.response;
	}

}
