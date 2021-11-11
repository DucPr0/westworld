package me.ducpro.westworldranks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class UnmuteCommand implements CommandExecutor {
	private Main plugin;
	public UnmuteCommand(Main plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (sender instanceof Player) {
			if (!plugin.getPlayerInfo((Player) sender).getRank().equals("admin")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
				return true;
			}
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "/unmute {name}.");
			return true;
		}
		String target = args[0];
		Player p = Bukkit.getPlayerExact(target);
		if (p == null) {
			sender.sendMessage(ChatColor.RED + "That player isn't online.");
			return true;
		}
		plugin.getData().setLong(p.getUniqueId(), plugin.getData().MUTE, 0);
		sender.sendMessage(ChatColor.GREEN + "Successfully unmuted " + plugin.getPlayerInfo(p).getName());
		return true;
	}
}
