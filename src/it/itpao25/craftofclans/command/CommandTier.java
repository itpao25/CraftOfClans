package it.itpao25.craftofclans.command;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.tier.TierListGUI;
import it.itpao25.craftofclans.tier.TierManager;
import it.itpao25.craftofclans.tier.TierObject;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.util.PermissionUtil;
import it.itpao25.craftofclans.util._Number;
import it.itpao25.craftofclans.util._Permission;
import it.itpao25.craftofclans.util._String;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTier implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Color.translate("&4Only player can run this command!"));
			return false;
		}

		Player p = (Player) sender;
		if (args.length == 0) {

			if (!PermissionUtil._has(sender, _Permission.PERM_TIER_GUI) && !sender.isOp() && !PermissionUtil._has(sender, _Permission.PERM_TIER_ADMIN)) {
				_String.nopermission(sender);
				return false;
			}
			@SuppressWarnings("unused")
			TierListGUI TierListGUI = new TierListGUI(p);
			return false;

		} else if (args.length > 0) {
			
			if (!PermissionUtil._has(sender, _Permission.PERM_TIER_ADMIN) && !sender.isOp() && PermissionUtil._has(sender, _Permission.PERM_TIER_GUI)) {
				
				if (args.length == 1) {
					
					TierObject object = new TierObject(args[0], p);
					if (object.exits() == false) {
						p.sendMessage(Color.message("&cThis tier does not exist!"));
						return false;
					}
					if (object.hasBought()) {
						object.tpPlayer();
					} else {
						sender.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-must-buy")));
					}
					return true;

				} else if (args.length == 2) {
					
					switch (args[0]) {
						case "tp":
							
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							
							if (object.hasBought()) {
								object.tpPlayer();
							} else {
								sender.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-must-buy")));
							}
							return true;
							
						case "buy":
							
							TierObject object1 = new TierObject(args[1], p);
							
							if (object1.exits()) {
								if (object1.hasBought()) {
									sender.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-already-bought")));
								} else {
									
									// Controllo se ha precedenti da controllare
									if (!object1.hasRequirements()) {
										p.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-bought-failed-requirements").replace("%1%", object1.getRequirements())));
										return false;
									}
									
									// Compro la Tier per il giocatore
									if (object1.buy()) {
										if (object1.addPlayer()) {
											p.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-bought-success").replace("%1%", args[1])));
										}
										return false;
									}
								}
							} else {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
							}
							return true;
					}
				}
				p.sendMessage(Color.message("&eUse &c/tier tp <name tier> &eor &c/tier buy <name tier>"));
				return false;
			}
			
			if(PermissionUtil._has(sender, _Permission.PERM_TIER_ADMIN) || sender.isOp()) {
				switch (args[0]) {
					case "create":
		
						if (args.length == 2) {
							boolean request = new TierManager().create(p, args[1]);
							if (request) {
								String name = args[1].toLowerCase();
								sender.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-created").replace("%1%", name)));
								
								if(CraftOfClans.freemode) {
									sender.sendMessage(Color.translate("&6Remember to define a new zone with WorldGuard to create a safe-zone to protect the zone from overlapping villages on the new tier."));
								}
								return true;
							}
							return false;
						}
						break;
					case "spawnpoint":
		
						if (args.length == 2) {
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							object.setSpawnPoit();
							return true;
						}
						break;
					case "tp":
		
						if (args.length == 2) {
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							object.tpPlayer();
							return true;
						}
						break;
					case "list":
		
						// Lista delle tier disponibili
						new TierManager().list(p);
						break;
					case "cost_elixir":
		
						if (args.length == 3 && _Number.isNumero(args[2])) {
							int value = Integer.parseInt(args[2]);
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							if (object.setCost("cost_elixir", value)) {
								p.sendMessage(Color.message("&2Cost set successful!"));
							}
							return true;
						}
						break;
		
					case "cost_dark_elixir":
		
						if (args.length == 3 && _Number.isNumero(args[2])) {
							int value = Integer.parseInt(args[2]);
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							if (object.setCost("cost_dark_elixir", value)) {
								p.sendMessage(Color.message("&2Cost set successful!"));
							}
							return true;
						}
						break;
					case "cost_gold":
		
						if (args.length == 3 && _Number.isNumero(args[2])) {
							int value = Integer.parseInt(args[2]);
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							if (object.setCost("cost_gold", value)) {
								p.sendMessage(Color.message("&2Cost set successful!"));
							}
							return true;
						}
						break;
					case "cost_gems":
		
						if (args.length == 3 && _Number.isNumero(args[2])) {
							int value = Integer.parseInt(args[2]);
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							if (object.setCost("cost_gems", value)) {
								p.sendMessage(Color.message("&2Cost set successful!"));
							}
							return true;
						}
						break;
		
					case "add-requirement":
						if (args.length == 3) {
							TierObject object1 = new TierObject(args[1], p);
							if (object1.exits() == false) {
								p.sendMessage(Color.message("&cTier " + args[1] + " does not exist!"));
								return false;
							}
							TierObject object2 = new TierObject(args[2], p);
							if (object2.exits() == false) {
								p.sendMessage(Color.message("&cTier " + args[2] + " does not exist!"));
								return false;
							}
							if (object1.addRequirement(object2)) {
								p.sendMessage(Color.message("&2Added requirement for tier " + args[1] + " (" + args[2] + ")"));
							} else {
								p.sendMessage(Color.message("&cAdd requirement field for tier " + args[1] + " (" + args[2] + "). Maybe already added?"));
							}
							return true;
						}
						break;
		
					case "remove-requirement":
						if (args.length == 3) {
							TierObject object1 = new TierObject(args[1], p);
							if (object1.exits() == false) {
								p.sendMessage(Color.message("&cTier " + args[1] + " does not exist!"));
								return false;
							}
							TierObject object2 = new TierObject(args[2], p);
							if (object2.exits() == false) {
								p.sendMessage(Color.message("&cTier " + args[2] + " does not exist!"));
								return false;
							}
							if (object1.removeRequirement(object2)) {
								p.sendMessage(Color.message("&2Removed requirement for tier " + args[1] + " (" + args[2] + ")"));
							} else {
								p.sendMessage(Color.message("&cRemove requirement field for tier " + args[1] + " (" + args[2] + "). Maybe already removed?"));
							}
							return true;
						}
						break;
		
					case "remove-player":
						if (args.length == 3) {
		
							@SuppressWarnings("deprecation")
							OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
							if (player == null) {
								sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
								return false;
							}
		
							TierObject object1 = new TierObject(args[1], p);
							if (object1.exits() == false) {
								p.sendMessage(Color.message("&cTier " + args[1] + " does not exist!"));
								return false;
							}
		
							PlayerStored ptarget = new PlayerStored(player.getUniqueId());
							if (!ptarget.hasTier(object1.name)) {
								p.sendMessage(Color.message("&cThe player does not have this tier!"));
								return false;
							}
							ptarget.removeTier(args[1]);
							p.sendMessage(Color.message("&2Removed tier " + args[1] + " for player " + player.getName()));
		
							return true;
						}
						break;
		
					case "add-player":
						if (args.length == 3) {
		
							@SuppressWarnings("deprecation")
							OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
							if (player == null) {
								sender.sendMessage(Color.message(CraftOfClansM.getString("messages.give-player-not-present")));
								return false;
							}
		
							TierObject object1 = new TierObject(args[1], p);
							if (object1.exits() == false) {
								p.sendMessage(Color.message("&cTier " + args[1] + " does not exist!"));
								return false;
							}
		
							PlayerStored ptarget = new PlayerStored(player.getUniqueId());
							ptarget.addTier(args[1]);
							p.sendMessage(Color.message("&2Added tier " + args[1] + " for player " + player.getName()));
		
							return true;
						}
						break;
		
					case "delete":
		
						if (args.length == 2) {
							TierObject object = new TierObject(args[1], p);
							if (object.exits() == false) {
								p.sendMessage(Color.message("&cThis tier does not exist!"));
								return false;
							}
							object.delete();
							p.sendMessage(Color.message(CraftOfClansM.getString("messages.tier-deleted")));
							return true;
						}
						break;
				}
				p.sendMessage(Color.message("&eUse &c/tier < create | delete | spawnpoint | tp | cost_elixir | cost_gold | cost_gems | cost_dark_elixir | add-requirement | remove-requirement | add-player | remove-player > [name] [value]"));
				return true;
			}
			_String.nopermission(sender);
		}
		p.sendMessage(Color.message("&eUse &c/tier < create | delete | spawnpoint | tp | cost_elixir | cost_gold | cost_gems | cost_dark_elixir | add-requirement | remove-requirement | add-player | remove-player > [name] [value]"));
		return false;
	}
}
