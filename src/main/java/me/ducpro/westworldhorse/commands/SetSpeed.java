package me.ducpro.westworldhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.*;

import me.ducpro.westworldcore.main.Main;

public class SetSpeed {
	public static void setSpeed(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore setspeed {speedtype} {value}.");
			return;
		}
		double speedval;
		try {
			speedval = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Please enter a valid number.");
			return;
		}
		if (args[1].equalsIgnoreCase("slow") || args[1].equalsIgnoreCase("normal") || args[1].equalsIgnoreCase("fast")) {
			args[1] = args[1].toLowerCase();
			plugin.getConfigFile().setDouble(args[1], speedval, true);
			sender.sendMessage(ChatColor.GREEN + "Successfully set speed value for " + args[1] + " to " + speedval);
		} else {
			sender.sendMessage(ChatColor.RED + "Please enter a valid speedtype.");
		}
	}
}
