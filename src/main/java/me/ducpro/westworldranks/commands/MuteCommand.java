package me.ducpro.westworldranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class MuteCommand implements CommandExecutor {
	private Main plugin;
	public MuteCommand(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	private String colored(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		String rank = null;
		if (sender instanceof Player) {
			rank = plugin.getPlayerInfo((Player) sender).getRank();
			if (rank.equals("member") || rank.equals("donator")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return true;
			}
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /mute {name} {timeInMinutes} {reason}.");
			return true;
		}
		String name = args[0], reason = "";
		for (int i = 2; i < args.length; i++) reason += args[i] + " ";
		if (reason.equals("")) reason = "You have been muted";
		long minutes;
		try {
			minutes = Long.parseLong(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Please enter a valid duration.");
			return true;
		}
		if (rank != null && rank.equals("mod")) {
			if (minutes == -1 || minutes > 60) {
				sender.sendMessage(ChatColor.RED + "You can only mute for an hour maximally.");
				return true;
			}
		}
		Bukkit.broadcastMessage("name = " + name);
		Player p = Bukkit.getPlayerExact(name);
		if (p == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		plugin.getData().addMute(p.getUniqueId(), reason);
		plugin.getData().setLong(p.getUniqueId(), plugin.getData().MUTE, System.currentTimeMillis() + minutes * 60000);
		Bukkit.broadcastMessage(colored("&ePlayer &7" + plugin.getPlayerInfo(p).getName() + " &ehas been muted for &7" + reason));
		return true;
	}
}
