package it.itpao25.craftofclans.handler;

import java.util.UUID;
import org.bukkit.OfflinePlayer;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.validator.PluginValidator;

public class PlaceholderAPI_register extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return "itpao25";
	}

	@Override
	public String getIdentifier() {
		return "coc";
	}

	@Override
	public String getVersion() {
		String ver = PluginValidator.ver();
		return ver;
	}

	@Override
	public String onRequest(OfflinePlayer player, String identifier) {

		UUID uuid_player = player.getUniqueId();
		PlayerStored pstored = new PlayerStored(uuid_player);

		if (pstored == null || !pstored.isExist()) {
			return null;
		}
		
		switch (identifier) {
			case "player_gems":
				return _Number.coolFormat(pstored.getGems());
			case "player_gold":
				return _Number.coolFormat(pstored.getGold());
			case "player_elixir":
				return _Number.coolFormat(pstored.getElixir());
			case "player_dark_elixir":
				return _Number.coolFormat(pstored.getElixirNero());
			case "max_player_gold":
				return _Number.coolFormat(pstored.getMaxGold());
			case "max_player_elixir":
				return _Number.coolFormat(pstored.getMaxElixir());
			case "max_player_dark_elixir":
				return _Number.coolFormat(pstored.getMaxElixirNero());
			case "player_trophies":
				return pstored.getTrofei() + "";
			case "player_clan":
				return pstored.hasClan() ? pstored.getClan().getName() : CraftOfClansM.getString("clan.clanless");
			case "player_tier_last":
				return pstored.getLastBuyedTier() != null ? pstored.getLastBuyedTier().name : CraftOfClansM.getString("tiers.still-none-bought");
			case "player_tier_next":
					return pstored.getNextTier() != null ? pstored.getNextTier().name : CraftOfClansM.getString("tiers.reached-last-tier");
					
			case "player_gems_noformat":
				return pstored.getGems() + "";
			case "player_gold_noformat":
				return pstored.getGold() + "";
			case "player_elixir_noformat":
				return pstored.getElixir() + "";
			case "player_dark_elixir_noformat":
				return pstored.getElixirNero() + "";
			case "max_player_gold_noformat":
				return pstored.getMaxGold() + "";
			case "max_player_elixir_noformat":
				return pstored.getMaxElixir() + "";
			case "max_player_dark_elixir_noformat":
				return pstored.getMaxElixirNero() + "";
		}
		return null;
	}
}
