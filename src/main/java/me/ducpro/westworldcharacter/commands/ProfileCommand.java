package me.ducpro.westworldcharacter.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcharacter.utils.DisplayProfile;
import me.ducpro.westworldcore.main.Main;

public class ProfileCommand implements CommandExecutor {
	private Main plugin;
	public ProfileCommand(Main plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		Player target = null;
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
				return true;
			}
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayerExact(args[0]);
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "This player is not online currently.");
				return true;
			}
		}
		DisplayProfile.displayProfile(plugin, sender, target);
		return true;
	}
}
