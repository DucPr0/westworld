package me.ducpro.westworldintro.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class SetSpawn {
	public static void setIntroSpawn(Main plugin, CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return;
		}
		Player p = (Player) sender;
		plugin.getConfigFile().setLocation("intro-spawn", p.getLocation());
		sender.sendMessage(ChatColor.GREEN + "Successfully set intro's spawn.");
	}
	public static void setTownSpawn(Main plugin, CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return;
		}
		Player p = (Player) sender;
		plugin.getConfigFile().setLocation("town-spawn", p.getLocation());
		sender.sendMessage(ChatColor.GREEN + "Successfully set town's spawn.");
	}
}
