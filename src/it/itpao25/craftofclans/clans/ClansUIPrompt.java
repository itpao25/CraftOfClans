package it.itpao25.craftofclans.clans;

import java.util.HashMap;

import it.itpao25.craftofclans.clans.ClansUIItem.ClansUIItemType;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ClansUIPrompt extends StringPrompt {

	private ClansUIItemType type;
	private Player player;

	public ClansUIPrompt(Player p, ClansUIItemType type) {
		setType(type);
		setPlayer(p);
	}

	@Override
	public Prompt acceptInput(ConversationContext paramConversationContext, String paramString) {

		// Aggiornare la strina delle impostazioni e i valori degli item
		HashMap<ClansUIItemType, String> setting_old = new HashMap<>();

		if (!ClansUIListeners.settings_players.containsKey(player)) {
			ClansUIListeners.settings_players.put(player, setting_old);
		}

		setting_old.putAll(ClansUIListeners.settings_players.get(player));
		setting_old.put(type, paramString);

		if (type == ClansUIItemType.SET_MIN_TROPHIES) {
			if (!_Number.isNumero(paramString.trim())) {
				return null;
			}
		}
		if (type == ClansUIItemType.SET_NAME) {
			// Rimuovo gli spazi e prendo solo la prima parola
			if (paramString.contains(" ")) {
				String[] splitted = paramString.split("\\s+");
				paramString = splitted[0];
			}
		}
		ClansUIListeners.settings_players.put(player, setting_old);
		ClansUIListeners.setReasumedRenderItem(player, type, paramString);
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public String getPromptText(ConversationContext paramConversationContext) {

		switch (type) {
		case SET_DESC:
			return Color.message(CraftOfClansM.getString("clan.message-to-set-desc"));
		case SET_MIN_TROPHIES:
			return Color.message(CraftOfClansM.getString("clan.message-to-set-mintrophies"));
		case SET_NAME:
			return Color.message(CraftOfClansM.getString("clan.message-to-set-name"));
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean blocksForInput(ConversationContext arg0) {
		return true;
	}

	public ClansUIItemType getType() {
		return type;
	}

	public void setType(ClansUIItemType type) {
		this.type = type;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
