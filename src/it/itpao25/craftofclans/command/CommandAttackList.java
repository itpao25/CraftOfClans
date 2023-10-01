package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAttackList {
	
	private String DB_ERROR_COMMAND  = "&4Error in listening to the database";
	
	public CommandAttackList(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return;
		}
		
		if (!PermissionUtil._has(sender, _Permission.PERM_ATTACK) && !sender.isOp()) {
			_String.nopermission(sender);
			return;
		}

		Player p = (Player) sender;
		PlayerStored pstored = new PlayerStored(p);
		
		// Controllo se il giocatore ha un villaggio
		if (pstored.hasVillage() == false) {
			p.sendMessage(Color.message(CraftOfClansM.getString("messages.player-nothave-village")));
			return;
		}
		
		if (args.length == 1) {
			try {
				pstored.getListAttacks(p, 1);
				return;
			} catch (SQLException e) {
				p.sendMessage(Color.message(DB_ERROR_COMMAND));
			}
		} else if (args.length == 3) {
			int pagina = _Number.isNumero(args[2]) ? Integer.parseInt(args[2]) : 0;
			try {
				pstored.getListAttacks(p, pagina);
				return;
			} catch (SQLException e) {
				p.sendMessage(Color.message(DB_ERROR_COMMAND));
			}
		}
		
		p.sendMessage(Color.message(CraftOfClansM.getString("commands-syntax.attacks-list-use")).replace("%1%", "/coc attack-list [-p] [page]"));
	}
}
