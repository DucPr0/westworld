package me.ducpro.westworldcombat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcombat.utils.DuelHandler;
import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class DuelCommand implements CommandExecutor {
	private Main plugin;
	public DuelCommand(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return true;
		}
		Player p = (Player) sender;
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please enter a player name to duel with.");
			return true;
		}
		Player target = Bukkit.getPlayerExact(args[0]);
		if (target == null) {
			p.sendMessage(ChatColor.RED + "Target isn't online.");
			return true;
		}
		if (plugin.getPlayerInfo(p).isDuelling()) {
			p.sendMessage(ChatColor.RED + "You cannot send a duel request while being in a duel.");
			return true;
		}
		if (plugin.getPlayerInfo(p).isRequestingDuel(target.getUniqueId())) {
			//target already requested a duel before. This accepts it.
			if (plugin.getPlayerInfo(target).isDuelling()) {
				p.sendMessage(ChatColor.RED + "Target is already in a duel. Please wait.");
				return true;
			}
			DuelHandler.beginDuel(plugin, p, target);
			p.sendMessage(ChatColor.GREEN + "Successfully accepted " + plugin.getPlayerInfo(target).getName() + "'s request.");
			target.sendMessage(ChatColor.GREEN + plugin.getPlayerInfo(p).getName() + " has accepted your duel request.");
		} else {
			plugin.getPlayerInfo(target).addDuelRequest(p.getUniqueId());
			p.sendMessage(ChatColor.GREEN + "Successfully sent a duel request to " + plugin.getPlayerInfo(target).getName());
			String msg = "&e" + plugin.getPlayerInfo(p).getName() + " has requested to duel with you. Type &7/duel " + 
																			plugin.getPlayerInfo(p).getName() + " &eto accept.";
			target.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
		return true;
	}
}
