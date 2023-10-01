package it.itpao25.craftofclans.village;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.util.Color;

public class VillageDestroy implements Runnable {

	private VillageId villo;
	private CommandSender sender;

	public VillageDestroy(VillageId villo, CommandSender sender) {
		this.villo = villo;
		this.sender = sender;
	}

	@Override
	public void run() {
		
		String nomeOwner = villo.getOwnerName();
		
		if (villo.isOwnerOnline()) {
			PlayerStored pstored = new PlayerStored(villo.getOwnerID());
			Bukkit.getServer().dispatchCommand(pstored.get(), "spawn");
		}
		
		sender.sendMessage(Color.message(nomeOwner + "'s village elimination in progress..."));
		this.villo.destroy(sender);
		sender.sendMessage(Color.message(nomeOwner + "'s village deleted!"));
		
		LogHandler.log(nomeOwner + "'s village deleted!");
	}
}
