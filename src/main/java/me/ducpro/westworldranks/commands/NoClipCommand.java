package me.ducpro.westworldranks.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.ducpro.westworldcore.main.Main;

public class NoClipCommand implements CommandExecutor {
	private Main plugin;
	public NoClipCommand(Main plugin) {
		// TODO Auto-generated constructor stub
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command Command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return true;
		}
		Player p = (Player) sender;
		String rank = plugin.getPlayerInfo(p).getRank();
		if (rank.equals("mod") || rank.equals("admin")) {
			if (p.getGameMode() != GameMode.SPECTATOR) {
				plugin.getPlayerInfo(p).setLastNoclip(p.getLocation());
				p.setGameMode(GameMode.SPECTATOR);
			} else {
				p.teleport(plugin.getPlayerInfo(p).getLastNoclip());
				p.setGameMode(GameMode.ADVENTURE);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
			return true;
		}
		return true;
	}
}
