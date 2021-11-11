package me.ducpro.westworldranks.commands;

import java.util.Date;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class BanCommand implements CommandExecutor {
	private Main plugin;
	public BanCommand(Main plugin) {
		this.plugin = plugin;
	}
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	private int HOUR_TO_MILISECS = 3600000;
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (sender instanceof Player) {
			String rank = plugin.getPlayerInfo((Player) sender).getRank();
			if (rank.equals("member") || rank.equals("donator")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return true;
			}
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /ban {name} {timeInHours} {reason}.");
			return true;
		}
		String target = args[0];
		String reason = "";
		for (int i = 2; i < args.length; i++) reason += args[i] + " ";
		if (args.length < 3) reason = "The Ban Hammer has spoken!";
		long hours;
		try {
			hours = Long.parseLong(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Ban duration must be a valid number.");
			return true;
		}
		if (sender instanceof Player) {
			String rank = plugin.getPlayerInfo((Player) sender).getRank();
			if (rank.equals("mod")) {
				if (hours == -1 || hours > 24) {
					sender.sendMessage(ChatColor.RED + "Ban duration can only be a day maximally.");
					return true;
				}
			}
		}
		Player p = Bukkit.getPlayerExact(target);
		if (p == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		
		String reason2 = reason;
		reason = ChatColor.translateAlternateColorCodes('&', "&f" + reason);
		
		plugin.getData().addBan(p.getUniqueId(), reason);
		Bukkit.broadcastMessage(colored("&ePlayer &7" + plugin.getPlayerInfo(p).getName() + " &ehas been banned for &7" + reason2));
		p.kickPlayer(reason);
		
		long expires = System.currentTimeMillis() + hours * HOUR_TO_MILISECS;
		if (hours != -1) Bukkit.getBanList(BanList.Type.NAME).addBan(target, reason, new Date(expires), null);
		else Bukkit.getBanList(BanList.Type.NAME).addBan(target, reason, null, null);
		return true;
	}
}
