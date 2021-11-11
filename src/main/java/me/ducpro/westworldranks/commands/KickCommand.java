package me.ducpro.westworldranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class KickCommand implements CommandExecutor {
	private Main plugin;
	public KickCommand(Main plugin) {
		this.plugin = plugin;
	}
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		String rank = null;
		if (sender instanceof Player) {
			rank = plugin.getPlayerInfo((Player) sender).getRank();
		}
		if (rank.equals("member") || rank.equals("donator")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /kick {name} {reason}.");
			return true;
		}
		String reason = "You have been kicked from the server.";
		if (args.length >= 1) {
			reason = "";
			for (int i = 1; i < args.length; i++) reason += args[i] + " ";
		}
		String name = args[0];
		Player kicked = Bukkit.getPlayerExact(name);
		if (kicked == null) {
			sender.sendMessage(ChatColor.RED + kicked.getName() + " isn't online.");
			return true;
		}
		plugin.getData().addKick(kicked.getUniqueId(), reason);
		Bukkit.broadcastMessage(colored("&ePlayer &7" + plugin.getPlayerInfo(kicked).getName() + " &ehas been kicked for &7" + reason));
		kicked.kickPlayer(reason);
		return true;
	}
}
