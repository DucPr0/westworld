package me.ducpro.westworldhorse.commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldhorse.utils.Utils;

public class Spawnpoints {
	public static void addSpawnpoint(Main plugin, CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return;
		}
		Player p = (Player) sender;
		plugin.getConfigFile().addSpawnpoint(p.getLocation());
		sender.sendMessage(ChatColor.GREEN + "Successfully added spawnpoint.");
	}
	public static void listSpawnpoint(Main plugin, CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "All horse spawnpoints:");
		ConfigurationSection sec = plugin.getConfigFile().getHorseSpawnpoints();
		Set<String> keys = sec.getKeys(false);
		String horsespawnpath = "horse-spawnpoints";
		for (String s: keys) {
			if (s.equals("count")) continue;
			double x = plugin.getConfigFile().getDouble(horsespawnpath + "." + s + ".x");
			double y = plugin.getConfigFile().getDouble(horsespawnpath + "." + s + ".y");
			double z = plugin.getConfigFile().getDouble(horsespawnpath + "." + s + ".z");
			sender.sendMessage(Utils.colored("&e" + s + ": " + x + " " + y + " " + z));
		}
	}
	public static void removeSpawnpoint(Main plugin, CommandSender sender, String args[]) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Correct usage: /westcore removespawnpoint {ID}.");
			return;
		}
		boolean removed = plugin.getConfigFile().removeSpawnpoint(args[1]);
		if (!removed) {
			sender.sendMessage(ChatColor.RED + "No spawnpoints with that ID exists!");
			return;
		} else {
			sender.sendMessage(ChatColor.GREEN + "Successfully removed spawnpoint " + args[1]);
		}
	}
}
