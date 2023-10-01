package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.map.ExpanderRegister;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.util.Color;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTest {
	public CommandTest(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can pay another user! Use instead /coc give "));
			return;
		}

		Player p = (Player) sender;
		
		if(ExpanderRegister.isMuraExpanded(p.getLocation().getChunk())) {
			p.sendMessage("si");
		} else {
			p.sendMessage("no");
		}
		
		if (MapInfo.getStructures(p.getLocation()) != null) {
			StructuresId id = MapInfo.getStructures(p.getLocation());
			p.sendMessage(id.getVillage().owner.getName());
		}
	}
}
