package me.ducpro.westworldtrain.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class TownCommands {
	public static void addTown(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Please enter a town name.");
			return;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return;
		}
		Player p = (Player) sender;
		String townname = args[1];
		plugin.getConfigFile().addTown(townname, p.getLocation());
		p.sendMessage(ChatColor.GREEN + "Successfully added town " + townname + " to the train's cycle.");
	}
	public static void listTowns(Main plugin, CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "All available towns: " + ChatColor.GRAY);
		ConfigurationSection townsec = plugin.getConfigFile().listTowns();
		for (String townname: townsec.getKeys(false)) {
			int order = townsec.getInt(townname + ".order");
			sender.sendMessage(ChatColor.YELLOW + townname + ChatColor.GRAY + ": " + order + ChatColor.YELLOW + ".");
		}
	}
	public static void removeTown(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Please enter a town name.");
			return;
		}
		String townname = args[1];
		boolean removed = plugin.getConfigFile().removeTown(townname);
		if (!removed) sender.sendMessage(ChatColor.RED + "Town does not exist!");
		else sender.sendMessage(ChatColor.GREEN + "Successfully removed town " + townname + ".");
	}
	public static void setOrder(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore setorder {townname} {order}.");
			return;
		}
		String townname = args[1];
		int order;
		try {
			order = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Please enter a valid order.");
			return;
		}
		boolean set = plugin.getConfigFile().setOrder(townname, order);
		if (set) sender.sendMessage(ChatColor.GREEN + "Successfully set order of " + townname + " to " + order);
		else sender.sendMessage(ChatColor.RED + "Town does not exist!");
	}
}
