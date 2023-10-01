package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.attack.Attack;
import it.itpao25.craftofclans.attack.AttackerManager;
import it.itpao25.craftofclans.attack.FindVillage;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;
import it.itpao25.craftofclans.village.SpectatorMode;
import it.itpao25.craftofclans.village.VillageId;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAttack {
	public CommandAttack(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}
		if (!PermissionUtil._has(sender, _Permission.PERM_ATTACK) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}

		Player p = (Player) sender;
		if (args.length == 1) {

			// Controllo se il giocatore ha un villaggio
			if (new PlayerStored(p).hasVillage() == false) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village")));
				return;
			}

			// Controllo se il giocatore è presente nel cooldown
			if (AttackerManager.isInCooldown(p.getUniqueId()) != 0 && !p.isOp()) {
				p.sendMessage(Color.message(CraftOfClansM.getString("messages.attack-in-cooldown").replace("%1%", AttackerManager.isInCooldown(p.getUniqueId()) + "")));
				return;
			}

			// Controllo se non sta spectando un villaggio
			if (SpectatorMode.inSpectator(p)) {
				if (SpectatorMode.player_inspect.get(p) != null) {

					VillageId villo = SpectatorMode.player_inspect.get(p);
					if (villo.isAvailable()) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-attack-disabled")));
						return;
					}
					if ((villo.getExpanses().size() == 0 && villo.getStructuresList().size() == 1) || villo.isOwnerOnline()) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-attack-disabled")));
						return;
					}
					if (villo.isAttacked()) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-village-in-attack").replace("%1%", "/coc leave")));
						return;
					}
					if (villo.isActiveScudo()) {
						p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-village-shield")));
						return;
					}

					// Controllo se il giocatore non è nello stesso clan
					PlayerStored pstored = new PlayerStored(p);
					if (pstored.hasClan()) {
						PlayerStored ptarget = new PlayerStored(villo.getOwnerID());
						if (ptarget.hasClan()) {
							if (pstored.getClan().equals(ptarget.getClan())) {
								p.sendMessage(Color.message(CraftOfClansM.getString("messages.spectator-attack-disabled")));
								return;
							}
						}
					}
					
					// Rimuovo il player dalla modalità spettatore
					SpectatorMode.remove(p, true);

					// Inizio l'attacco
					new Attack(new PlayerStored(p), villo);
				}
				return;
			}

			@SuppressWarnings("unused")
			FindVillage attacca = new FindVillage(p);
			return;
		}
		p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.attack-use")).replace("%1%", "/coc attack"));
	}
}
