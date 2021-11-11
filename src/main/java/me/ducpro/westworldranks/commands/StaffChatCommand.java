package me.ducpro.westworldranks.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class StaffChatCommand implements CommandExecutor {
	private Main plugin;
	public StaffChatCommand(Main plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return true;
		}
		Player p = (Player) sender;
		String rank = plugin.getPlayerInfo(p).getRank();
		if (rank.equals("member") || rank.equals("donator")) {
			p.sendMessage(ChatColor.RED + "You don't have permission to access staff chat.");
			return true;
		}
		plugin.getPlayerInfo(p).setChatMode(2);
		p.sendMessage(ChatColor.GREEN + "You are now in staff chat.");
		return true;
	}
}
