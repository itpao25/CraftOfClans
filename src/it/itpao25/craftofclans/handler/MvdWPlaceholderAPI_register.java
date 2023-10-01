package it.itpao25.craftofclans.handler;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.tier.TierObject;
import it.itpao25.craftofclans.util._Number;

import java.util.UUID;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;

public class MvdWPlaceholderAPI_register {

	/**
	 * Registrazione dei placeholders usando MvdWPlaceholderAPI
	 * 
	 */
	public static void registerPlacers() {

		// Gemme
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_gems", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getGems());
				}
				return null;
			}
		});

		// Gold
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_gold", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getGold());
				}
				return null;
			}
		});

		// Elixir
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_elixir", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getElixir());
				}
				return null;
			}
		});

		// Elixir Dark
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_dark_elixir", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getElixirNero());
				}
				return null;
			}
		});

		// get max gold
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_max_player_gold", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getMaxGold());
				}
				return null;
			}
		});

		// get max elixir
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_max_player_elixir", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getMaxElixir());
				}
				return null;
			}
		});

		// get max dark elixir
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_max_player_dark_elixir", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return _Number.coolFormat(pstored.getMaxElixirNero());
				}
				return null;
			}
		});

		// Trofei
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_trophies", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getTrofei() + "";
				}
				return null;
			}
		});

		// Nome del clan
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_clan", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					String name = pstored.hasClan() ? pstored.getClan().getName() : CraftOfClansM.getString("clan.clanless");
					return name;
				}
				return null;
			}
		});
		
		// Tier ultimo comprato
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_tier_last", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					if(pstored.getLastBuyedTier() != null) {
						TierObject tierobject = pstored.getLastBuyedTier();
						return tierobject.name;
					} else {
						return CraftOfClansM.getString("tiers.still-none-bought");
					}
				}
				return null;
			}
		});
		
		// Tier prossimo da comprare
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_tier_next", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					if(pstored.getNextTier() != null) {
						TierObject tierobject = pstored.getNextTier();
						return tierobject.name;
					} else {
						return CraftOfClansM.getString("tiers.reached-last-tier");
					}
				}
				return null;
			}
		});
		
		// Gemme No format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_gems_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getGems() + "";
				}
				return null;
			}
		});

		// Gold no format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_gold_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getGold() + "";
				}
				return null;
			}
		});

		// Elixir no format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_elixir_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getElixir() + "";
				}
				return null;
			}
		});

		// Elixir Dark no format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_player_dark_elixir_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getElixirNero() + "";
				}
				return null;
			}
		});

		// get max gold no format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_max_player_gold_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getMaxGold() + "";
				}
				return null;
			}
		});

		// get max elixir no format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_max_player_elixir_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getMaxElixir() + "";
				}
				return null;
			}
		});

		// get max dark elixir no format
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(CraftOfClans.getInstance(), "coc_max_player_dark_elixir_noformat", new be.maximvdw.placeholderapi.PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				UUID uuid_player = event.getPlayer().getUniqueId();
				PlayerStored pstored = new PlayerStored(uuid_player);
				if (pstored != null && pstored.isExist()) {
					return pstored.getMaxElixirNero() + "";
				}
				return null;
			}
		});
	}
}
