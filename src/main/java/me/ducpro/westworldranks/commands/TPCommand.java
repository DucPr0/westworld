package me.ducpro.westworldranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class TPCommand implements CommandExecutor {
	private Main plugin;
	public TPCommand(Main plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return true;
		}
		Player p = (Player) sender;
		String rank = plugin.getPlayerInfo(p).getRank();
		if (!rank.equals("admin")) {
			p.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
			return true;
		}
		if (args.length == 0) {
			p.sendMessage(ChatColor.RED + "Please enter a target to teleport to.");
			return true;
		}
		if (args.length >= 2) {
			String target1 = args[0], target2 = args[1];
			Player targetp1 = Bukkit.getPlayerExact(target1), targetp2 = Bukkit.getPlayerExact(target2);
			if (targetp1 == null || targetp2 == null) {
				p.sendMessage(ChatColor.RED + "Invalid player name.");
				return true;
			}
			targetp1.teleport(targetp2);
			p.sendMessage(ChatColor.GREEN + "Successfully teleported " + plugin.getPlayerInfo(targetp1).getName() + " to " + plugin.getPlayerInfo(targetp2).getName());
			return true;
		}
		String target = args[0];
		Player targetp = Bukkit.getPlayerExact(target);
		if (targetp == null) {
			p.sendMessage(ChatColor.RED + "Target player isn't online.");
			return true;
		}
		p.teleport(targetp);
		p.sendMessage(ChatColor.GREEN + "Teleported to " + plugin.getPlayerInfo(targetp).getName());
		return true;
	}
}
