package it.itpao25.craftofclans.scoreboard;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.api.AttackComplete;
import it.itpao25.craftofclans.api.ClanCreateEvent;
import it.itpao25.craftofclans.api.ClanPlayerJoinEvent;
import it.itpao25.craftofclans.api.ClanPlayerLeave;
import it.itpao25.craftofclans.api.ResourceChangeValue;
import it.itpao25.craftofclans.api.StructuresBuild;
import it.itpao25.craftofclans.api.StructuresResourceCollectionFinal;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * scoreboard:
    # If you use plugin like FeatherBoard or other similar
    # you must set false this
    enable: true
    title: '  &e&lCraftOfClans  '
    subtitle: ''
    clan: '&c&lClan: &6[name_clan]'
    gems: '&2&lGems &a[amount]'
    gold: '&e&lGold &e[amount]&c/&e[tot]'
    elixir: '&d&lElixir &5[amount]&d/&5[tot]'
    elixir_dark: '&8&lElixir Dark &8[amount]&0/&8[tot]'
    trophies: '&6&lTrophies &e[amount]'
    
 * @author Paolo Trombini
 *
 */
public class CoCScoreboardEvent implements Listener {

	@EventHandler
	public void onjoin(final PlayerJoinEvent e) {
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(e.getPlayer());
		}
	}

	// Quando l'utente costruisce una struttura
	@EventHandler
	public void onStructuresBuild(StructuresBuild event) {
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(event.getPlayer());
		}
	}

	// Quando il giocatore preliva le risorse dalla struttura (Effettuato)
	@EventHandler
	public void onStructuresWithDrawsFinal(StructuresResourceCollectionFinal event) {
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(event.getPlayer());
		}
	}

	// Quando un giocatore termina un attacco
	@EventHandler
	public void onAttackComplete(final AttackComplete event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(CraftOfClans.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (CraftOfClans.isScoreboard) {
					CoCScoreboardManager.addScoreboard(event.getPlayer());
				}
			}
		}, 40L);
	}

	@EventHandler
	public void onClanCreateEvent(ClanCreateEvent event) {
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(event.getPlayer());
		}
	}

	@EventHandler
	public void onClanPlayerQuit(ClanPlayerLeave event) {
		if (event.getPlayerLeaved() == null)
			return;
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(event.getPlayerLeaved());
		}
	}

	@EventHandler
	public void onClanPlayerJoin(ClanPlayerJoinEvent event) {
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(event.getPlayer());
		}
	}

	@EventHandler
	public void onResourceUpdate(ResourceChangeValue event) {
		if (CraftOfClans.isScoreboard) {
			CoCScoreboardManager.addScoreboard(event.getPlayer());
		}
	}

}
