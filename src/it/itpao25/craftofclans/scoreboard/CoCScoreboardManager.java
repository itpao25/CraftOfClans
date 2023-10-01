package it.itpao25.craftofclans.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util._Number;

public class CoCScoreboardManager {

	public boolean isEnable;

	public boolean isActive() {
		/*boolean set = CraftOfClansC.getString("scoreboard.enable") != null ? CraftOfClansC.getBoolean("scoreboard.enable") : false;
		return set;
		*/
		return false;
	}

	/**
	 * Aggiungo un giocatore nella scoreboard (appena joina nel server)
	 * 
	 * @param p
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean addScoreboard(Player p) {

		PlayerStored pstored = new PlayerStored(p);

		String title = Color.translate(CraftOfClans.config.getString("scoreboard.title") != null && CraftOfClans.config.getString("scoreboard.title").trim().equals("") == false ? CraftOfClans.config.getString("scoreboard.title") : "&c&lCraftOfClans");
		String subtitle = Color.translate(CraftOfClans.config.getString("scoreboard.subtitle") != null && CraftOfClans.config.getString("scoreboard.subtitle").trim().equals("") == false ? CraftOfClans.config.getString("scoreboard.subtitle") : "&e");
		String clan_name = null;
		if (pstored.hasClan()) {
			clan_name = Color.translate(CraftOfClans.config.getString("scoreboard.clan").replace("[name_clan]", pstored.getClan().getName()));
		}
		String conto_gemme = Color.translate(CraftOfClans.config.getString("scoreboard.gems"));
		String conto_gold = Color.translate(CraftOfClans.config.getString("scoreboard.gold"));
		String conto_elixir = Color.translate(CraftOfClans.config.getString("scoreboard.elixir"));
		String conto_trofei = Color.translate(CraftOfClans.config.getString("scoreboard.trophies"));
		String conto_elixir_nero = Color.translate(CraftOfClans.config.getString("scoreboard.elixir_dark"));

		// Variabili
		conto_gemme = conto_gemme.replace("[amount]", pstored.getGems() + "");
		conto_gold = conto_gold.replace("[amount]", pstored.getGold() + "").replace("[tot]", pstored.getMaxGold() + "");
		conto_elixir = conto_elixir.replace("[amount]", pstored.getElixir() + "").replace("[tot]", pstored.getMaxElixir() + "");
		conto_trofei = conto_trofei.replace("[amount]", pstored.getTrofei() + "");
		conto_elixir_nero = conto_elixir_nero.replace("[amount]", pstored.getElixirNero() + "").replace("[tot]", pstored.getMaxElixirNero() + "");

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("CraftOfClans", "CraftOfClans");

		objective.setDisplayName(title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Team team = board.registerNewTeam("Team");
		team.setDisplayName(ChatColor.RED + "");

		Score score1 = objective.getScore(Bukkit.getOfflinePlayer(subtitle));
		score1.setScore(7);

		if (pstored.hasClan()) {
			Score scoreclan = objective.getScore(Bukkit.getOfflinePlayer(clan_name));
			scoreclan.setScore(7);
		}

		Score score2 = objective.getScore(Bukkit.getOfflinePlayer(conto_gemme));
		score2.setScore(6);

		Score score3 = objective.getScore(Bukkit.getOfflinePlayer(conto_gold));
		score3.setScore(5);

		Score score4 = objective.getScore(Bukkit.getOfflinePlayer(conto_elixir));
		score4.setScore(4);

		Score score5 = objective.getScore(Bukkit.getOfflinePlayer(conto_elixir_nero));
		score5.setScore(3);

		Score score6 = objective.getScore(Bukkit.getOfflinePlayer(conto_trofei));
		score6.setScore(2);

		p.setScoreboard(board);

		return true;
	}

	/**
	 * Faccio comparire la scoreboard a tutti i giocatori
	 */
	public void loadAllPlayers() {
		if (!isEnable) {
			return;
		}
		for (Player p : _Number.getOnlinePlayers()) {
			addScoreboard(p);
		}
	}

	public void removeAllPlayers() {
		for (Player p : _Number.getOnlinePlayers()) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
}
