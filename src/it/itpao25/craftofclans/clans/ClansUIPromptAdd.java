package it.itpao25.craftofclans.clans;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.clans.ClansUIItem.ClansUIItemType;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.entity.Player;

public class ClansUIPromptAdd implements ConversationAbandonedListener {

	/**
	 * Aggiungo la persona alla lista
	 */
	public void addUser(Player sender, ClansUIItemType type) {

		Conversation conversation = new Conversation(CraftOfClans.getInstance(), sender, new ClansUIPrompt(sender, type));
		conversation.setLocalEchoEnabled(false);
		conversation.addConversationAbandonedListener(this);
		sender.beginConversation(conversation);

	}

	@Override
	public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {
		// TODO Auto-generated method stub
		Player p = (Player) paramConversationAbandonedEvent.getContext().getForWhom();
		ClansUIListeners.getReasumedRender(p);
	}

}
