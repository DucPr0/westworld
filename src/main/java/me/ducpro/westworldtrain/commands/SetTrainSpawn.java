package me.ducpro.westworldtrain.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class SetTrainSpawn {
	public static void setTrainSpawn(Main plugin, CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return;
		}
		Player p = (Player) sender;
		plugin.getConfigFile().setLocation("train-spawn", p.getLocation());
		sender.sendMessage(ChatColor.GREEN + "Successfully set train's spawn.");
	}
}
