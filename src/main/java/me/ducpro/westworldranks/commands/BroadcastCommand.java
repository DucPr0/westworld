package me.ducpro.westworldranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class BroadcastCommand implements CommandExecutor {
	private Main plugin;
	public BroadcastCommand(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (sender instanceof Player) {
			String rank = plugin.getPlayerInfo((Player) sender).getRank();
			if (!rank.equals("admin")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return true;
			}
		}
		String message = "";
		for (int i = 0; i < args.length; i++) {
			message += args[i];
			if (i < args.length - 1) message += " ";
		}
		Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + message);
		return true;
	}
}
