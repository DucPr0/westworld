package me.ducpro.westworldranks.commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class UnbanCommand implements CommandExecutor {
	private Main plugin;
	public UnbanCommand(Main plugin) {
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
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /unban {name}.");
			return true;
		}
		Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
		sender.sendMessage(ChatColor.GREEN + "Successfully unbanned " + args[0]);
		return true;
	}
}
